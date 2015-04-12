package net.jueb.util4j.classLoader.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * 自定义类加载器
 * @author Administrator
 *打破双亲委托加载顺序,让子类先加载,父类后加载
 */
public class MyClassLoder extends ClassLoader {
	
    public MyClassLoder() throws IOException {
		super(null);
	}
	
	/**
	 * @param  classNames-(二进制名称)有效类名称的示例包括： "java.lang.String"
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void defineClassByDir(String basedir,String[] classNames) throws IOException
	{
		for (int i = 0; i < classNames.length; i++) 
		{ 
			defineClassByDir(basedir,classNames[i]); 
        } 
	}
	/**
	 * 根据类名在基本目录中加载class文件
	 * @param className-(二进制名称)有效类名称的示例包括： "java.lang.String"
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public Class<?> defineClassByDir(String basedir,String className) throws IOException
	{ 
        Class<?> cls = null; 
        StringBuffer sb = new StringBuffer(basedir); 
        String classFname = className.replace('.', File.separatorChar) + ".class";
        sb.append(File.separator + classFname); 
        File classFile = new File(sb.toString()); 
        cls = defineClassByFile(className,classFile);
        return cls; 
    } 
	
	/**
	 * 将一个 classFile读取的byte 数组转换为 Class 类的实例。
	 * @param className-(二进制名称)有效类名称的示例包括： "java.lang.String"
	 * @param classFile class文件
	 * @return
	 * @throws IOException
	 */
	private Class<?> defineClassByFile(String className,File classFile) throws IOException
	{ 
		FileInputStream fis=new FileInputStream(classFile);
		long len=classFile.length();
        byte[] raw = new byte[(int) len]; 
        fis.read(raw); 
        fis.close(); 
        return defineClassByBytes(className, raw); 
    }
	
	/**
	 * 根据字节数组定义一个class对象,成功则记录
	 * @param className
	 * @param raw
	 * @return
	 */
	private Class<?> defineClassByBytes(String className,byte[] raw)
	{
		System.out.println("InputStream:"+getResource(className));
		
		Class<?> cls = null; 
		cls=defineClass(className,raw,0,raw.length);
		if(cls!=null)
		{
			System.out.println("Class:"+className+"被定义!");
		}
		return cls;
	}
	
	/**
	 * 只有先执行了类的定义，才能加载到该类
	 * 使用二进制名称的className加载类
	 * 父类的loadClass(String name)方法要调用该方法
	 * className-(二进制名称)有效类名称的示例包括： "java.lang.String"
	 */
	@Override
	protected Class<?> loadClass(String className, boolean resolve) throws ClassNotFoundException { 
		System.out.println("开始加载类:"+className);
		Class<?> clazz=null;
		if(className.startsWith("java.")||className.startsWith("javax."))
		{//如果是系统类加载器
			System.out.println("交给系统类加载器加载:"+className);
//			clazz=super.loadClass(name);
			clazz=findSystemClass(className);
			if (clazz != null) 
			{//解析类结构
				if (resolve)
					resolveClass(clazz);
				return (clazz);
			}
			return clazz;
		}
		if(clazz==null)
		{
			System.out.println("交给当前类加载器加载:"+className);
			clazz=findLoadedClass(className);
			if (clazz != null) 
			{//解析类结构
				if (resolve) 
				{
					resolveClass(clazz);
				}
			}
		}
		if(clazz==null)
		{
			System.out.println("交给当前类加载器定义:"+className);
			try {
				clazz=findClass(className);
			} catch (Exception e) {
				//如果该类没有加载过，并且不属于必须由该类加载器加载之列都委托给系统加载器进行加载。
				System.out.println("交给当前线程类加载器加载:"+className);
				clazz=Thread.currentThread().getContextClassLoader().loadClass(className);
			}
		}
		if(clazz==null)
		{
			System.out.println("交给当前系统类加载器加载:"+className);
			clazz=findSystemClass(className);
		}
		System.out.println("类:"+clazz+"被"+clazz.getClassLoader()+"成功加载!");
		return clazz;
    }
	
	@Override
	protected Class<?> findClass(String className) throws ClassNotFoundException {
		System.out.println("当前类加载器classpath中查找并定义类:"+className);
		Class<?> clazz=null;
		byte[] bytes=findClassFileBytes(className);
		if(bytes!=null)
		{
			clazz=defineClass(className,bytes, 0, bytes.length);
		}
		if(clazz==null)
		{
			throw new ClassNotFoundException();
		}
		return clazz;
	}
	
	/**
	 * 根据类名获取类文件字节
	 * @param className
	 * @return
	 */
	public byte[] findClassFileBytes(String className)
	{
		//TODO
		return null;
	}
}

