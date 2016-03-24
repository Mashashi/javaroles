package pt.mashashi.javaroles;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;

/**
 * 
 * @author Rafael
 *
 */
public class ClassUtils {
	
	private ClassUtils(){}
	
	
	/*public static String getPrefixablePackage(Class<?> clazz){
		Package pkg = RoleBus.class.getPackage();
		String pkgNamePrefixable = pkg == null ? "" : pkg.getName()+".";
		return pkgNamePrefixable;
	}
	public static String getPackage(){
		return getPrefixablePackage(ClassUtils.class);
	}*/
	
	/**
	 * Returns the base definition interface
	 * 
	 * @param method
	 * @param clazz
	 * @return
	 * @throws NotFoundException
	 */
	public static List<CtClass> definedOnInterfaces(CtMethod method, CtClass clazz) {
		//CtClass declared = method.getDeclaringClass();
		//if(declared.equals(c)){return c;}
		List<CtClass> returned = new LinkedList<CtClass>();
		CtClass[] interfaces;
		try {
			interfaces = clazz.getInterfaces();
			for(CtClass i: interfaces){
				for(CtMethod m: i.getMethods()){
					if(method.getName().equals(m.getName()) && method.getSignature().equals(m.getSignature())){
						returned.add(i);
					}
				}
				
			}
		} catch (NotFoundException e) {
			// This should not be a problem. Happens when going through all the class path for some internal java classes.
		}
		return returned;
	}
	
	
	public static String getMethodNameFromStack(StackTraceElement[] st, int level){
		
		String methodResolv = null;
		
		{ // check inner class
			String resolveRef = st[level].toString();
			Pattern p = Pattern.compile("^.*\\$.*\\.(.*)\\(.*$");
			Matcher m = p.matcher(resolveRef);
			if(m.matches()){
				methodResolv = m.group(1);
			}
		}
		
		{ // check regular class
			if(methodResolv==null){ 
				String resolveRef = st[level].toString();
				Pattern p = Pattern.compile("^.*\\.(.*)\\(.*$");
				Matcher m = p.matcher(resolveRef);
				if(m.matches()){
					methodResolv = m.group(1);
				}
			}
		}
		
		return methodResolv;
		
	}
	
	public static Class<?>[] getNativeTypes(CtClass[] jaTypes){
		Class<?>[] nativeTypes = new Class[jaTypes.length];
		for(int i =0; i < jaTypes.length; i++){
			nativeTypes[i] = getNativeType(jaTypes[i]);
		}
		return nativeTypes;
	}
	
	public static Class<?> getNativeType(CtClass jaType){
		String name = jaType.getName();
		if(jaType.isArray()){
			name = "[L"+name.substring(0, name.length()-2)+";";
		}
		try{
			return Class.forName(name);
		}catch(ClassNotFoundException e){}
		throw new RuntimeException();
	}
	
	public static Object invokeWithNativeTypes(Object target, String methodName, CtClass[] jaTypes, Object[] args) throws ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException{
		Class<?>[] types = getNativeTypes(jaTypes);
		Method m = target.getClass().getMethod(methodName, types);
		boolean access = m.isAccessible();
		m.setAccessible(true);
		Object ret = m.invoke(target, args);
		m.setAccessible(access);
		return ret;
	}
	
	public static String getMethodCall(String methodName, CtClass[] jaTypes, String objArgsVar){
		String s = new String("");
		s += methodName+"(";
		for(int i=0;i<jaTypes.length;i++){
			s +="(("+jaTypes[i].getName()+")"+objArgsVar+"["+i+"]"+")";
			if(i+1<jaTypes.length)
				s+=",";
		}
		s += ");";
		return s;
	}
	
	public static Object invokeSuperWithNativeTypes(Object target, String methodName, CtClass[] jaTypes, Object[] args) throws ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException{
		Class<?>[] types = getNativeTypes(jaTypes);
		Method m = target.getClass().getMethod(methodName, types);
		boolean access = m.isAccessible();
		m.setAccessible(true);
		Object ret = m.invoke(target, args);
		m.setAccessible(access);
		return ret;
	}
	
	/*public static Object invokeSuperWithNativeTypes(Object target, String methodName, CtClass[] jaTypes, Object[] args) throws ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException{
		Class<?>[] types = getNativeTypes(jaTypes);
		Method m = target.getClass().getSuperclass().getDeclaredMethod(methodName, types);
		boolean access = m.isAccessible();
		m.setAccessible(true);
		Object ret = m.invoke(target, args);
		m.setAccessible(access);
		return ret;
	}*/
	
	public static Object invokeWithNativeTypes(Object target, String methodName, Class<?>[] types, Object[] args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException{
		Method m = target.getClass().getDeclaredMethod(methodName, types);
		boolean access = m.isAccessible();
		m.setAccessible(true);
		Object ret = m.invoke(target, args);
		m.setAccessible(access);
		return ret;
	}
	
	public static CtMethod getExecutingMethod(String clazzName, String name, String sig){
		
		ClassPool cp = ClassPool.getDefault();
		CtClass c = cp.getOrNull(clazzName);
		
		for(CtMethod clazzMethod: c.getDeclaredMethods()){
			String nameProcessing = clazzMethod.getName();
			String sigProcessing = clazzMethod.getSignature();
			if(nameProcessing.equals(name) && sigProcessing.equals(sig)){
				return clazzMethod;
			}
		}
		
		return null;
	}
	/*public static CtConstructor getExecutingConstructor(String clazzName, String sig){
		
		ClassPool cp = ClassPool.getDefault();
		CtClass c = cp.getOrNull(clazzName);
		
		for(CtConstructor clazzMethod: c.getDeclaredConstructors()){
			String sigProcessing = clazzMethod.getSignature();
			if(sigProcessing.equals(sig)){
				return clazzMethod;
			}
		}
		
		return null;
	}*/
	
	public static List<CtField> getListFieldAnnotated(CtClass target, Class<? extends Annotation> annotation) throws ClassNotFoundException{
		List<CtField> roleObjects = new LinkedList<>();
		for(CtField field : target.getFields()){//Class.forName(target.getName()).getFields();
			if(field.getAnnotation(annotation)!=null){
				roleObjects.add(field);
			}
		}
		{ // Get the private fields also
			for(CtField field : target.getDeclaredFields()){//Class.forName(target.getName()).getFields();
				if(field.getAnnotation(annotation)!=null && !roleObjects.contains(field)){
					roleObjects.add(field);
				}
			}
		}
		return roleObjects;
	}
	public static List<Field> getListFieldAnnotated(Class<?> target, Class<? extends Annotation> annotation) throws ClassNotFoundException{
		List<Field> roleObjects = new LinkedList<>();
		for(Field field : target.getFields()){//Class.forName(target.getName()).getFields();
			if(field.getAnnotation(annotation)!=null){
				roleObjects.add(field);
			}
		}
		{ // Get the private fields also
			for(Field field : target.getDeclaredFields()){//Class.forName(target.getName()).getFields();
				if(field.getAnnotation(annotation)!=null && !roleObjects.contains(field)){
					roleObjects.add(field);
				}
			}
		}
		return roleObjects;
	}
	public static List<Method> getListMethodAnotated(Class<?> target, Class<? extends Annotation> annotation) throws ClassNotFoundException{
		List<Method> methods = new LinkedList<>();
		for(Method m : target.getMethods()){
			if(m.getAnnotation(annotation)!=null){
				methods.add(m);
			}
		}
		return methods;
	}
	public static List<CtField> getListFieldAnotated(Object target, Class<? extends Annotation> annotation) throws ClassNotFoundException, NotFoundException{
		ClassPool pool = ClassPool.getDefault();
		CtClass cc = pool.get(target.getClass().getName());
		return getListFieldAnnotated(cc, annotation);
	}
	public static <T extends Annotation> HashMap<String, Field> getTypeFieldAnotatedNative(Object target, Class<T> annotation){
		HashMap<String, Field> roleObjects = new HashMap<String, Field>();
		for(Field field : target.getClass().getFields()){
			if(field.getAnnotation(annotation)!=null){
				roleObjects.put(field.getType().getSimpleName(), field);
			}
		}
		return roleObjects;
	}
	public static HashMap<String, CtField> getTypeFieldAnotatedAssist(CtClass target, Class<?> annotation) throws ClassNotFoundException, NotFoundException{
		// Injected fields seem to only appear with target.getDeclaredFields()
		HashMap<String, CtField> roleObjects = new HashMap<>();
		for(CtField field : target.getFields()){
			if(field.getAnnotation(annotation)!=null){
				roleObjects.put(field.getType().getSimpleName(), field);
			}
		}		
		return roleObjects;
	}
	
	
	
	
	/*public static void addAnnotation(CtClass cn, CtField newField, String annotation) {
		// Add annotation so we don't inject the code more than once
		ConstPool cpool = cn.getClassFile().getConstPool();
		AnnotationsAttribute attr = new AnnotationsAttribute(cpool, AnnotationsAttribute.visibleTag);
		javassist.bytecode.annotation.Annotation annot = new javassist.bytecode.annotation.Annotation(annotation, cpool);
		attr.addAnnotation(annot);
		newField.getFieldInfo().addAttribute(attr);
	}*/
	
	
	
	
	
	public static Collection<String> getAllClassNames(){
		Set<String> classNames = new HashSet<>();
		String classpath = System.getProperty("java.class.path");
		String[] classpathEntries = classpath.split(File.pathSeparator);
		for(String classPathEntry: classpathEntries){
			classNames.addAll( getAllClassNamesFolder(classPathEntry) );
		}
		return classNames;
	}
	
	public static List<String> getAllClassNamesFolder(String path){
		return getAllClassNamesFolder(path, "");
	}
	public static List<String> getAllClassNamesFolder(String path, String pkg){
		
		List<String> classNames = new LinkedList<>();
		File d = new File(path);
		path = FileUtils.separatorAtEnt(path);
		
		if(d.isDirectory()){ 
			
			for(String fname : d.list()){
				
				String relativePath = path+fname;
				File f = new File(relativePath);
				
				if(f.isDirectory()){
					final String currentPkg = pkg+fname+".";
					classNames.addAll(getAllClassNamesFolder(f.getAbsolutePath(), currentPkg));
				}else{
					processFile(classNames, pkg, fname);
				}
				
			}
			
		}else{
			processFile(classNames, pkg, path);
		}
		return classNames;
	}
	public static void processFile(List<String> results, String pkg, String fName){
		if(fName.endsWith(".class")){
			String className = fName.substring(0, fName.length()-".class".length());
			results.add(pkg+className);
		}else if(fName.endsWith(".jar"+File.separator)){
			
			try {
				JarFile jarFile = new JarFile(fName);
	            Enumeration<JarEntry> allEntries = jarFile.entries();
	            while (allEntries.hasMoreElements()) {
	                JarEntry entry = (JarEntry) allEntries.nextElement();
	                if(entry.getName().endsWith(".class")){
	        			String pathZip = entry.getName().substring(0, entry.getName().length()-".class".length());
	        			String clazzName = null;
	        			{ // We have to brute force because we can not be sure about which file separator is used
		        			clazzName = pathZip.replace("/", ".");
		        			if(pathZip.equals(clazzName)){
		        				clazzName = pathZip.replace("\\", ".");
		        			}
	        			}
	        			results.add(clazzName);
	        		}
	            }
	            jarFile.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			
		}
	}
	
	public static String getExecutingClass(int stackLevel) {
        return Thread.currentThread().getStackTrace()[stackLevel].getClassName();
    }
	
	public static String generateIdentifier() {
        return "i"+UUID.randomUUID().toString().replace("-", "");
    }
	
	public static boolean classImplementsInterface(CtClass clazz, CtClass interfaze) throws NotFoundException{
		for(CtClass i : clazz.getInterfaces()){
			if(interfaze.equals(i))
				return true;
		}
		return false;
	}
	
	public static final HashMap<String,Object> missMsgReceptor = null;
	public static final CtClass getMissMsgReceptorType(){
		try {
			return ClassPool.getDefault().getOrNull(ClassUtils.class.getName()).getField("missMsgReceptor").getType();
		} catch (NotFoundException e) {
			// TODO
			throw new RuntimeException(e.getMessage());
		}
	}
	public static final String getMissMsgReceptorSigGen(){
		try {
			return ClassPool.getDefault().getOrNull(ClassUtils.class.getName()).getField("missMsgReceptor").getType().getGenericSignature();
		} catch (NotFoundException e) {
			// TODO
			throw new RuntimeException(e.getMessage());
		}
	}


	public static void addAnnotation(
            CtMethod cmethod,
            Annotation annot)
    {
        ClassFile cfile = cmethod.getDeclaringClass().getClassFile();
        ConstPool cpool = cfile.getConstPool();
 
        AnnotationsAttribute attr =
                new AnnotationsAttribute(cpool, AnnotationsAttribute.visibleTag);
        javassist.bytecode.annotation.Annotation annotAssist = new javassist.bytecode.annotation.Annotation(annot.annotationType().getName(), cpool);
        //annot.addMemberValue("value", new IntegerMemberValue(ccFile.getConstPool(), 0));
        attr.addAnnotation(annotAssist);
        cmethod.getMethodInfo().addAttribute(attr);
    }
	
	
	
	public static List<CtClass> extendz(CtClass clazz, CtClass possibleExtends){
		
		List<CtClass> l =  new LinkedList<CtClass>();
		
		ClassPool pool = ClassPool.getDefault();
		
		if(clazz.equals(possibleExtends)) return l;
		
		try {
			final Object objectCt = pool.get(Object.class.getName());
			do{
				l.add(possibleExtends);
				possibleExtends = possibleExtends.getSuperclass();
				if(clazz.equals(possibleExtends)){
					return l;
				}
			}while(!possibleExtends.equals(objectCt));
		} catch (NotFoundException e) {
			// No problem some classes like javax.mail.Authenticator are not found
			//throw new RuntimeException(e);
		}
		
		l.clear();
		return l;
	}
	
}
