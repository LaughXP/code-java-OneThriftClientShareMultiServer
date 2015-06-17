/**
 * 
 */
package com.jiuyan.commons.remote.util;

import java.io.File;
import java.net.URL;
import java.util.Enumeration;

/**
 * @Title: ClassScanner.java
 * @Package com.jiuyan.commons.remote.util
 * @Description:
 * @author xiaoyu
 * @date 2015年6月17日 下午9:32:45
 */
public class ClassScanner {

	private Map<String, Class<?>> classes = new HashMap<String, Class<?>>();
	private FilenameFilter javaClassFilter; // 类文件过滤器,只扫描一级类
	private final String CLASS_FILE_SUFFIX = ".class"; // Java字节码文件后缀
	private String packPrefix;

	public static Integer scanning(String packagePath, boolean recursive) {
		Enumeration<URL> dir;
		String filePackPath = packagePath.replace('.', '/');
		try {
			// 得到指定路径中所有的资源文件
			dir = Thread.currentThread().getContextClassLoader().getResources(filePackPath);
			packPrefix = Thread.currentThread().getContextClassLoader().getResource("").getPath();
			if (System.getProperty("file.separator").equals("\\")) {
				packPrefix = packPrefix.substring(1);
			}
			// 遍历资源文件
			while (dir.hasMoreElements()) {
				URL url = dir.nextElement();
				String protocol = url.getProtocol();
				if ("file".equals(protocol)) {
					File file = new File(url.getPath().substring(1));
					scan0(file);
				} else if ("jar".equals(protocol)) {
					scanJ(url, recursive);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return classes.size();
	}
}
