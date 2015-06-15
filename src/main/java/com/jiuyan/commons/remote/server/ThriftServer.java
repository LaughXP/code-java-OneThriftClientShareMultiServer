package com.jiuyan.commons.remote.server;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.server.ServerContext;
import org.apache.thrift.server.TServer;
import org.apache.thrift.transport.TSaslTransportException;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 自定义的ThriftServer,修改了从容器中加载processor,加入了计算方法所用的时间
 */

public class ThriftServer extends TServer {
	private static final Logger LOGGER = LoggerFactory.getLogger(ThriftServer.class.getName());

	  public static class Args extends AbstractServerArgs<Args> {
	    public int minWorkerThreads = 5;
	    public int maxWorkerThreads = Integer.MAX_VALUE;
	    public ExecutorService executorService;
	    public int stopTimeoutVal = 60;
	    public TimeUnit stopTimeoutUnit = TimeUnit.SECONDS;
	    public int requestTimeout = 20;
	    public TimeUnit requestTimeoutUnit = TimeUnit.SECONDS;
	    public int beBackoffSlotLength = 100;
	    public TimeUnit beBackoffSlotLengthUnit = TimeUnit.MILLISECONDS;

	    public Args(TServerTransport transport) {
	      super(transport);
	    }

	    public Args minWorkerThreads(int n) {
	      minWorkerThreads = n;
	      return this;
	    }

	    public Args maxWorkerThreads(int n) {
	      maxWorkerThreads = n;
	      return this;
	    }

	    public Args requestTimeout(int n) {
	      requestTimeout = n;
	      return this;
	    }

	    public Args requestTimeoutUnit(TimeUnit tu) {
	      requestTimeoutUnit = tu;
	      return this;
	    }
	    //Binary exponential backoff slot length
	    public Args beBackoffSlotLength(int n) {
	      beBackoffSlotLength = n;
	      return this;
	    }

	    //Binary exponential backoff slot time unit
	    public Args beBackoffSlotLengthUnit(TimeUnit tu) {
	      beBackoffSlotLengthUnit = tu;
	      return this;
	    }

	    public Args executorService(ExecutorService executorService) {
	      this.executorService = executorService;
	      return this;
	    }
	  }

	  // Executor service for handling client connections
	  private ExecutorService executorService_;

	  // Flag for stopping the server
	  // Please see THRIFT-1795 for the usage of this flag
	  private volatile boolean stopped_ = false;

	  private final TimeUnit stopTimeoutUnit;

	  private final long stopTimeoutVal;

	  public ThriftServer(Args args) {
	    super(args);

	    stopTimeoutUnit = args.stopTimeoutUnit;
	    stopTimeoutVal = args.stopTimeoutVal;

	    executorService_ = args.executorService != null ?
	        args.executorService : createDefaultExecutorService(args);
	  }

	  private static ExecutorService createDefaultExecutorService(Args args) {
	    SynchronousQueue<Runnable> executorQueue =
	      new SynchronousQueue<Runnable>();
	    return new ThreadPoolExecutor(args.minWorkerThreads,
	                                  args.maxWorkerThreads,
	                                  60,
	                                  TimeUnit.SECONDS,
	                                  executorQueue);
	  }


		public void serve() {
			try {
				serverTransport_.listen();
			} catch (TTransportException ttx) {
				LOGGER.error("Error occurred during listening.", ttx);
				return;
			}

			// Run the preServe event
			if (eventHandler_ != null) {
				eventHandler_.preServe();
			}

			stopped_ = false;
			setServing(true);
			while (!stopped_) {
				TTransport client = null;
				try {
					client = serverTransport_.accept();
					WorkerProcess wp = new WorkerProcess(client);

					int retryCount = 0;
					while (true) {
						try {
							executorService_.execute(wp);
							break;
						} catch (Throwable t) {
							if (t instanceof RejectedExecutionException) {
										client.close();
										wp = null;
										LOGGER.warn("Task has been rejected by ExecutorService " + retryCount + " times till timedout, reason: " + t);
										break;
							} else if (t instanceof Error) {
								LOGGER.error("ExecutorService threw error: " + t, t);
								throw (Error) t;
							} else {
								// for other possible runtime errors from ExecutorService, should also not kill serve
								LOGGER.warn("ExecutorService threw error: " + t, t);
								break;
							}
						}
					}
				} catch (TTransportException ttx) {
					if (!stopped_) {
						LOGGER.warn("Transport error occurred during acceptance of message.", ttx);
					}
				}
			}

			executorService_.shutdown();

			// Loop until awaitTermination finally does return without a interrupted
			// exception. If we don't do this, then we'll shut down prematurely. We want
			// to let the executorService clear it's task queue, closing client sockets
			// appropriately.
			long timeoutMS = stopTimeoutUnit.toMillis(stopTimeoutVal);
			long now = System.currentTimeMillis();
			while (timeoutMS >= 0) {
				try {
					executorService_.awaitTermination(timeoutMS, TimeUnit.MILLISECONDS);
					break;
				} catch (InterruptedException ix) {
					long newnow = System.currentTimeMillis();
					timeoutMS -= (newnow - now);
					now = newnow;
				}
			}
			setServing(false);
		}

	  public void stop() {
	    stopped_ = true;
	    serverTransport_.interrupt();
	  }

	  private class WorkerProcess implements Runnable {

	    /**
	     * Client that this services.
	     */
	    private TTransport client_;

	    /**
	     * Default constructor.
	     *
	     * @param client Transport to process
	     */
	    private WorkerProcess(TTransport client) {
	      client_ = client;
	    }

	    /**
	     * Loops on processing a client forever
	     */
	    public void run() {
	      //TProcessor processor = null;
	      TTransport inputTransport = null;
	      TTransport outputTransport = null;
	      TProtocol inputProtocol = null;
	      TProtocol outputProtocol = null;

	      MonitorThriftEventHandler eventHandler = null;
	      ServerContext connectionContext = null;

	      try {
	        //processor = processorFactory_.getProcessor(client_);
	        inputTransport = inputTransportFactory_.getTransport(client_);
	        outputTransport = outputTransportFactory_.getTransport(client_);
	        inputProtocol = inputProtocolFactory_.getProtocol(inputTransport);
	        outputProtocol = outputProtocolFactory_.getProtocol(outputTransport);	  

	        eventHandler = (MonitorThriftEventHandler)getEventHandler();
	        if (eventHandler != null) {
	          connectionContext = eventHandler.createContext(inputProtocol, outputProtocol);
	        }
	        // we check stopped_ first to make sure we're not supposed to be shutting
	        // down. this is necessary for graceful shutdown.
	        while (true) {

	        	if (stopped_) {
					break;
				}
				
				long start = System.currentTimeMillis();
				//修改从容其中获得processor并执行
				if(!ThriftBaseProcessor.process(inputProtocol, outputProtocol)) {
					break;
				}
				long lantency = System.currentTimeMillis() - start;

				if (eventHandler != null) {
					eventHandler.processContext(connectionContext,inputProtocol.readMessageBegin().name,lantency,inputTransport, outputTransport);
				}
	        }
	      } catch (TSaslTransportException ttx) {
	        // Something thats not SASL was in the stream, continue silently 
	      } catch (TTransportException ttx) {
	        // Assume the client died and continue silently
	      } catch (TException tx) {
	        LOGGER.error("Thrift error occurred during processing of message.", tx);
	      } catch (Exception x) {
	        LOGGER.error("Error occurred during processing of message.", x);
	      }

	      if (eventHandler != null) {
	        eventHandler.deleteContext(connectionContext, inputProtocol, outputProtocol);
	      }

	      if (inputTransport != null) {
	        inputTransport.close();
	      }

	      if (outputTransport != null) {
	        outputTransport.close();
	      }
	    }
	  }

}
