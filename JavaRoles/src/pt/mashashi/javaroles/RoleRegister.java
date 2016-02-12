package pt.mashashi.javaroles;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewConstructor;
import javassist.CtNewMethod;
import javassist.NotFoundException;
import pt.mashashi.javaroles.composition.MissMsgReceptor;
import pt.mashashi.javaroles.composition.OriginalRigid;

/**
 * Offers a way to go through all the classes in the class path searching for the points that need code injection
 * 
 * @author Rafael
 *
 */
public abstract class RoleRegister {
	
	protected String roleBusVarName;
	private ClassPool cp;
	private String[] onlyFor;
	
	public RoleRegister(){
		
		{
			// CONFIG Suppress console output from log4j missing config file 
			// log4j:WARN No appenders could be found for logger... When log4j config file is not set
			Logger.getRootLogger().setLevel(Level.OFF); 
		}
		
		roleBusVarName = ClassUtils.generateIdentifier();
		cp = ClassPool.getDefault();
	}
	
	public RoleRegister(Class<?>... clazzes){
		this();
		List<String> onlyFor = new LinkedList<String>();
		for(Class<?> clazz : clazzes){
			onlyFor.add(clazz.getName());
		}
		this.onlyFor = onlyFor.toArray(new String [onlyFor.size()]);
	}
	
	
	/**
	 * 
	 * The main idea is to inject the code into the rigid type method a 
	 * call to {@link RoleBus#resolve(CtMethod, Object[])} to resolve its object role type method.
	 * 
	 * Usually subclasses that implement this method should copy the method rename it and set the body for the functionality they
	 * wish to add. The conditions in which a call to the original rename method present in rigid type is implementation dependent.
	 * 
	 * @param cn The class that contains the method target of the code injection
	 * @param method The method in which the code is to be injected 
	 * @param roleObjectClass This is the object in the rigid type with an annotation {@link ObjectForRole} 
	 * 							that has a method with the same signature than the parameter {@code method}
	 * @throws CannotCompileException
	 * @throws NotFoundException
	 */
	protected abstract CtMethod injectRoleDependency(CtClass cn, CtMethod method) throws CannotCompileException, NotFoundException;
	
	/**
	 * Returns a string having the RoleBus method field declaration to be injected
	 * 
	 * @return
	 */
	protected abstract String getRoleBusDeclaration();
	
	/**
	 * If the method being processed is to be injected with the code for enhancement
	 *  
	 * @param method
	 * @param objectRoles 
	 * @return
	 */
	protected abstract boolean isToInject(CtMethod method, HashMap<String,CtField> objectRoles) throws ClassNotFoundException;
	
	/**
	 * 
	 * If through this code it was uses the native class object instead of {@link CtClass} 
	 * we would get an error for trying to modify an already loaded class.
	 * 
	 * Build upon the template methods {@link RoleRegister#getRoleBusDeclaration()} and {@link RoleRegister#injectRoleDependency(CtClass, CtMethod, CtField)}
	 * 
	 * @param clazzName The qualified class name. It is a string because we can not use {@link Class} at this point. 
	 */
	public void registerRool(String clazzName){
		
		CtClass cn = cp.getOrNull(clazzName);
		boolean wasInjected = false;
		
		 // SNIPPET Insert a method call in every method of a class that has an annotation
		 /*
		 This was made to register the signature of a function because it is not possible to get it from the stack trace
		 */
		 /*{
			try {
				Object a = cn.getAnnotation(RoleObject.class);
				if(a!=null){
					CtMethod[] ct = cn.getMethods();
					for(CtMethod m : ct){
						try{
							m.insertBefore(ClassUtils.class.getName()+".submitSignature(\""+m.getName()+"\", $sig);");
						}catch (CannotCompileException e){
							// Probably just an abstract
						}
					}
					try {
						cn.toClass();
					} catch (CannotCompileException e) {
						Logger.getLogger(RoleBus.class.getName()).debug("error processing role class: "+clazzName+" "+e.getMessage());
						e.printStackTrace();
						throw new RuntimeException();
					}
					return ;
				}
			} catch (ClassNotFoundException e) {
				// Do nothing this is not a role object class
			}
		}*/
		
		
		
		try {
			
			{ // BLOCK Check type @MissMsgReceptor
				List<CtField> objectRoles = ClassUtils.getListFieldAnotated(cn, MissMsgReceptor.class);
				if(objectRoles!=null){
					for(CtField o:objectRoles){
						if(
								!o.getType().equals((ClassUtils.getMissMsgReceptorType())) && 
								(
								o.getType().getGenericSignature()==null ||
								!o.getType().getGenericSignature().equals(ClassUtils.getMissMsgReceptorSigGen())
								)
										){
							throw new MissUseAnnotationExceptionException(MissMsgReceptor.class, AnnotationException.BAD_TYPE, cn.getName(), o.getName());
						}
					}
				}
			}
			
			CtMethod[] methods = cn.getDeclaredMethods();
			HashMap<String, CtField> objectRoles = ClassUtils.getTypeFieldAnotatedAssist(cn, ObjectForRole.class);
			
			HashMap<String, CtClass> originals = getOriginals(cn);
			
			methodInj: for(CtMethod method : methods){
				
				boolean isTargetInjection = isToInject(method, objectRoles);	
				if(isTargetInjection){
					
					if(cn.isFrozen()){
						/* BLOCK If the class was already loaded don't patch it
						 Maybe it was already processed
						 This happens when running multiple test cases via "mvn test"
						 The registerRool method will be called multiple times
						*/
						Logger.getLogger(RoleBus.class.getName()).debug(cn.getName()+" is frozen");
						break methodInj;
					}
					
					if(!wasInjected){						
						CtField newField = CtField.make(getRoleBusDeclaration(), cn);
						cn.addField(newField);
					}
					
					CtMethod created = injectRoleDependency(cn, method);
					
					List<CtClass> inters = ClassUtils.definedOnInterfaces(method, cn);
					applyIndirect(method, created, originals, inters);
					
					wasInjected = true;
					
					Logger.getLogger(RoleBus.class.getName()).debug(cn.getName()+" add code injected in method "+method.getName()+" done");
					
				}
				
			}
			
			
			if(wasInjected || originals.size()!=0){
				// COMMENT // cn.writeFile(); - Writing to file will create a new .class file. Just use .toClass(); to place the class available on the class path
				/*
				 We want to put the class on the class path after modifications if it was injected with new methods or if 
				 it was some fields annotated with original. In the later case we change the constructor.
				 */
				cn.toClass();
			}
			
			{ // freeze originals
				for(CtClass o : originals.values()){
					o.toClass();
				}
			}
			
		} catch (CannotCompileException | NotFoundException | ClassNotFoundException e) {
			Logger.getLogger(RoleBus.class.getName()).debug("error processing class: "+clazzName+" "+e.getMessage());
			e.printStackTrace();
			throw new RuntimeException();
		}
		
		
		
		
	}

	public void applyIndirect(CtMethod method, 
								CtMethod created, 
								HashMap<String, CtClass> originals,
								List<CtClass> inters) throws CannotCompileException {
		for(CtClass i:inters){
			CtClass o = originals.get(i.getName());
			if(o!=null){
				for(CtMethod mo: o.getMethods()){
					if(method.getName().equals(mo.getName()) && method.getSignature().equals(mo.getSignature())){
						final String varLocal = ClassUtils.generateIdentifier();
						
						// COMMENT #1.1 Hear we complete with the code for each method
						/*
						 We can only do it hear because only hear we have injected the methods on the rigid type
						 */
						mo.setBody(
							"{"+
								CtMethod.class.getName()+" "+varLocal+" = "+ClassUtils.class.getName()+".getExecutingMethod("+
																			"\""+o.getName()+"\","+
																			"\""+method.getName()+"\","+
																			"\""+method.getSignature()+"\");"+
								"return ($r) "+ClassUtils.class.getName()+".invokeWithNativeTypes("+
								"core,"+
								"\""+created.getName()+"\","+
								varLocal+".getParameterTypes(),"+
								"$args);"+
							"}"		
						);
						
					}
				}
			}
		}
	}

	public HashMap<String, CtClass> getOriginals(CtClass cn)
			throws ClassNotFoundException, NotFoundException, CannotCompileException {
		
		HashMap<String, CtClass> originals = new HashMap<>();
		if(cn.isFrozen()){
			// BLOCK Ignore frozen classes, assume already processed
			return originals;
		}
		ClassPool cp = ClassPool.getDefault();
		List<CtField> objectOriginal = ClassUtils.getListFieldAnotated(cn, OriginalRigid.class);
		Iterator<CtField> ite = objectOriginal.iterator();
		while(ite.hasNext()){
			CtField n = ite.next();
			CtClass i = cp.getOrNull(n.getType().getName());
			
			if(!i.isInterface()){
				throw new MissUseAnnotationExceptionException(OriginalRigid.class, AnnotationException.MISS_USE, cn.getName(), n.getName(), cn.getSimpleName());
			}
			
			if(!ClassUtils.classImplementsInterface(cn, i)){
				throw new MissUseAnnotationExceptionException(OriginalRigid.class, AnnotationException.NOT_IMPLEMENTED, cn.getName(), n.getName(), cn.getSimpleName());
			}		
					
			CtClass evalClass = originals.get(i.getName());
			
			if(evalClass==null){
				evalClass = cp.makeClass(ClassUtils.generateIdentifier());	
				evalClass.addInterface(i);
				originals.put(i.getName(), evalClass);
				evalClass.addField(CtField.make("public "+i.getName()+" core;", evalClass));
				for(CtMethod method : evalClass.getMethods()){
					if(Modifier.isAbstract(method.getModifiers())){
						CtMethod m = CtNewMethod.make(method.getModifiers() & ~Modifier.ABSTRACT & Modifier.PUBLIC, 
								method.getReturnType(), 
								method.getName(), 
								method.getParameterTypes(), 
								method.getExceptionTypes(), 
								"{return null;}", 
								evalClass
						);
						final String var = ClassUtils.generateIdentifier();
						{
							m.setBody(
								"{"+
									CtMethod.class.getName()+" "+var+" = "+ClassUtils.class.getName()+".getExecutingMethod("+
																				"\""+evalClass.getName()+"\","+
																				"\""+method.getName()+"\","+
																				"\""+method.getSignature()+"\");"+
									"return ($r) "+ClassUtils.class.getName()+".invokeWithNativeTypes("+
									"core,"+
									"\""+method.getName()+"\","+
									var+".getParameterTypes(),"+
									"$args);"+
								"}"
							);
						}
						evalClass.addMethod(m);
					}
				}
				evalClass.addConstructor(CtNewConstructor.make("public " + evalClass.getSimpleName() + "("+i.getName()+" core) {this.core = core;}", evalClass));
			}
			
			// COMMENT #1.0 //evalClass.toClass(); - Was commented out because we just want to created the structure of the class hear 
			for(CtConstructor con : n.getDeclaringClass().getConstructors()){
				con.insertAfter("this."+n.getName()+" = new "+evalClass.getName()+"(this);");
			}
			
		}
		return originals;
	}
	
	/*public void registerRools(){
		List<String> c = ClassUtils.getAllClassNames();
		for(String className : c){
			registerRool(className);
		}
	}
	
	public void registerRools(String... clazzes){
		for(String clazz :clazzes){
			CtClass c = null;
			try {
				c = cp.get(clazz);
			} catch (NotFoundException e) {
				throw new RuntimeException(e.getMessage());
			}
			
			try {
				for(CtClass i : c.getDeclaredClasses()){
					registerRool(i.getName());
				}
			} catch (NotFoundException e) {
				throw new RuntimeException(e.getMessage());
			}
			registerRool(clazz);
		}
	}
	
	public void registerRoolsExcludeGiven(String... clazzes){
		List<String> c = ClassUtils.getAllClassNames();
		classProcessing: for(String className : c){
			for(String clazz :clazzes){
				CtClass clazzCt = null;
				try {
					clazzCt = cp.get(clazz);
				} catch (NotFoundException e) {
					throw new RuntimeException(e.getMessage());
				}
					
				if(className.startsWith(clazzCt.getName())){ 
					// BLOCK The condition is with .startsWith because we want to stop registration of inner classes
					continue classProcessing;
				}
			}
			registerRool(className);
		}
	}*/
	
	/**
	 * Before invoking this method be sure that:
	 * - Any instances for the referenced classes are freed.
	 * - Their owning classloader has still instances in existence (has not been GC-ed).
	 * - The java.lang.Class object is not referenced from anywhere (same goes for reflective access to their members).
	 * 
	 */
	public void registerRools(){
		{ // BLOCK Free up reference in the class loader
		  /*
		   This will unload the classes from the class loader if the roles for unloading a class are verified
		   */
			System.gc(); 
		}
		if(onlyFor!=null){
			registerRools(onlyFor);
		}else{
			List<String> c = ClassUtils.getAllClassNames();
			for(String className : c){
				registerRool(className);
			}
		}
	}
	
	private void registerRools(String... clazzes){
		for(String clazz :clazzes){
			CtClass c = null;
			try {
				c = cp.get(clazz);
			} catch (NotFoundException e) {
				throw new RuntimeException(e.getMessage());
			}
			
			try {
				for(CtClass i : c.getDeclaredClasses()){
					registerRool(i.getName());
				}
			} catch (NotFoundException e) {
				throw new RuntimeException(e.getMessage());
			}
			registerRool(clazz);
		}
	}
	
	public void registerRoolsExcludeGiven(Class<?>... clazzes){
		List<String> c = ClassUtils.getAllClassNames();
		classProcessing: for(String className : c){
			for(Class<?> clazz :clazzes){
				if(className.startsWith(clazz.getName())){ 
					// BLOCK The condition is with .startsWith because we want to stop registration of inner classes
					continue classProcessing;
				}
			}
			registerRool(className);
		}
		
	}

	
}
