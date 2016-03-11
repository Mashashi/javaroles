package pt.mashashi.javaroles;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.NotImplementedException;
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
import pt.mashashi.javaroles.annotations.AnnotationException;
import pt.mashashi.javaroles.annotations.MissMsgReceptor;
import pt.mashashi.javaroles.annotations.MissUseAnnotationExceptionException;
import pt.mashashi.javaroles.annotations.ObjRigid;
import pt.mashashi.javaroles.annotations.ObjRole;
import pt.mashashi.javaroles.annotations.Rigid;
import pt.mashashi.javaroles.composition.TestPlay;
import pt.mashashi.javaroles.injection.InjectionStrategy;

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
	private String[] pkgs;
	private String classesDir;
	
	private InjectionStrategy injRigStrategy  = InjectionStrategy.getInstanceSingle();
	//protected InjectionStrategy injRigStrategy = new InjectionStrategyMultiple();
	
	
	public enum MATCH_TYPE{ EXACT, STARTS_WITH, REGEX }
	private HashMap<String, MATCH_TYPE> matchTypePkg;
	
	private List<String> classReport;
	
	
	
	@SuppressWarnings("unused")
	private RoleRegister(){
		throw new NotImplementedException("This shouldn't be used");
	}
	
	public RoleRegister(String[] pkgs){
		{
			// CONFIG Suppress console output from log4j missing config file 
			// log4j:WARN No appenders could be found for logger... When log4j config file is not set
			Logger.getRootLogger().setLevel(Level.OFF);
		}
		roleBusVarName = ClassUtils.generateIdentifier();
		cp = ClassPool.getDefault();
		{
			this.pkgs = pkgs;
			if(pkgs==null){ this.pkgs = new String[0]; }
			if(this.pkgs.length==0){ throw new IllegalArgumentException("Supply at least one package perfix."); }
			matchTypePkg = new HashMap<>();
			setPkgMatchType(MATCH_TYPE.STARTS_WITH);
		}
		classReport = new LinkedList<>();
		
	}
	
	public RoleRegister(String[] pkgs, Class<?>... clazzes){
		this(pkgs);
		List<String> onlyFor = new LinkedList<String>();
		for(Class<?> clazz : clazzes){
			onlyFor.add(clazz.getName());
		}
		this.onlyFor = onlyFor.toArray(new String [onlyFor.size()]);
	}
	
	public RoleRegister(Class<?>... clazzes){
		this(new String[]{""});
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
	 * @param preCode 
	 * @param roleObjectClass This is the object in the rigid type with an annotation {@link ObjRole} 
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
	private void registerRool(String clazzName){
		
		CtClass cn = cp.getOrNull(clazzName);
		boolean wasInjected = false;
		
		try {
			
			checkMsgReceptorTypes(cn);
			
			CtMethod[] methods = cn.getDeclaredMethods();
			HashMap<String, CtField> objectRoles = ClassUtils.getTypeFieldAnotatedAssist(cn, ObjRole.class);
			
			applyInjectionOnRoles(cn);
			
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
				if(classesDir!=null){
					cn.writeFile(classesDir);
				}
				cn.toClass();
				classReport.add(clazzName);
			}
			
			{ // freeze originals
				for(CtClass o : originals.values()){
					o.toClass();
				}
			}
			
		} catch (CannotCompileException | NotFoundException | ClassNotFoundException | IOException e) {
			Logger.getLogger(RoleBus.class.getName()).debug("error processing class: "+clazzName+" "+e.getMessage());
			e.printStackTrace();
			throw new RuntimeException();
		}
		
		
		
		
	}
	
	/**
	 * If it is not using HashMap<String, Object> is an error
	 * 
	 * @param cn
	 * @throws ClassNotFoundException
	 * @throws NotFoundException
	 */
	private void checkMsgReceptorTypes(CtClass cn) throws ClassNotFoundException, NotFoundException {
		List<CtField> objectRoles = ClassUtils.getListFieldAnotated(cn, MissMsgReceptor.class);
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

	private void applyInjectionOnRoles(CtClass cn) throws ClassNotFoundException {
		if(!cn.isFrozen()){
			try {
				boolean setUpInjection = cn.getAnnotation(Rigid.class)!=null;
				
				
				LinkedList<CtConstructor> pConstructor = new LinkedList<CtConstructor>(); 
				if(setUpInjection){
					for(CtConstructor c: cn.getConstructors()){
						
						c.insertAfter(injRigStrategy.setAll());
// OLDFEAT - Try to call the inject before constructor.
// Not done it has hard because default initializations injected byte code into the constructor.
// Another alternative is to use the insertAt but we have to have a method to find out where the code begins
//							final String name = ClassUtils.generateIdentifier();
//							final CtMethod method = c.toMethod(name, cn);
//							final String varC = ClassUtils.generateIdentifier(); 
//							cn.addMethod(method);
//							c.setBody("{"
//									
//									+injStrategy.getCode()+
//									
//									CtConstructor.class.getName()+" "+varC+" = "+ClassUtils.class.getName()+".getExecutingConstructor("+
//									"\""+cn.getName()+"\","+
//									"\""+c.getSignature()+"\");"+
//									
//									ClassUtils.class.getName()+".invokeWithNativeTypes("+
//									"this,"+
//									"\""+name+"\","+
//									varC+".getParameterTypes(),"+
//									"$args);"
//									
//									+"}");
						
						pConstructor.add(c);
						Logger.getLogger(RoleBus.class.getName()).trace("Test:"+TestPlay.class.getName()+"-"+c.getName()+"-rc");
					}	
				}
				
				{ // Process play constructors
					for(CtConstructor c: cn.getConstructors()){
						if(c.getAnnotation(Rigid.class)!=null && !pConstructor.contains(c)){
							c.insertAfter(injRigStrategy.setAll());
							Logger.getLogger(RoleBus.class.getName()).trace("Test:"+TestPlay.class.getName()+"-"+c.getName()+"-pc");
						}
					}
				}
				
				{ // Process play methods
					for(CtMethod m: cn.getDeclaredMethods()){
						if(m.getAnnotation(Rigid.class)!=null){
							if(m.getParameterTypes().length==0){
								m.insertBefore(injRigStrategy.setAll());
								Logger.getLogger(RoleBus.class.getName()).trace("Test:"+TestPlay.class.getName()+"-"+m.getDeclaringClass().getName()+"-"+m.getName()+"-pm");
							}else{
								m.insertBefore(injRigStrategy.setParams());
							}
						}
					}
				}
				
			} catch (SecurityException|CannotCompileException|NotFoundException e) {
				throw new RuntimeException(e.getMessage());
			} 
		}
	}

	private void applyIndirect(CtMethod method, 
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

	private HashMap<String, CtClass> getOriginals(CtClass cn)
			throws ClassNotFoundException, NotFoundException, CannotCompileException {
		
		HashMap<String, CtClass> originals = new HashMap<>();
		if(cn.isFrozen()){
			// BLOCK Ignore frozen classes, assume already processed
			return originals;
		}
		ClassPool cp = ClassPool.getDefault();
		List<CtField> objectOriginal = ClassUtils.getListFieldAnotated(cn, ObjRigid.class);
		Iterator<CtField> ite = objectOriginal.iterator();
		while(ite.hasNext()){
			CtField n = ite.next();
			CtClass i = cp.getOrNull(n.getType().getName());
			
			if(!i.isInterface()){
				throw new MissUseAnnotationExceptionException(ObjRigid.class, AnnotationException.MISS_USE, cn.getName(), n.getName(), cn.getSimpleName());
			}
			
			if(!ClassUtils.classImplementsInterface(cn, i)){
				throw new MissUseAnnotationExceptionException(ObjRigid.class, AnnotationException.NOT_IMPLEMENTED, cn.getName(), n.getName(), cn.getSimpleName());
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
	
	
	
	
	
	public List<String> getClassReport() {
		return new LinkedList<>(classReport);
	}
	
	
	
	
	public RoleRegister setPkgMatchType(MATCH_TYPE matchType){
		for(String pkg : pkgs){ 
			matchTypePkg.put(pkg, matchType); 
		}
		return this;
	}
	public RoleRegister setPkgMatchType(int pkgIdx, MATCH_TYPE matchType){
		matchTypePkg.put(pkgs[pkgIdx], matchType);
		return this;
	}
	public RoleRegister writeClasses(String dir){
		classesDir = dir;
		return this;
	}
	public RoleRegister setRigidInjectionStrategy(InjectionStrategy injRigStrategy){
		this.injRigStrategy = injRigStrategy;
		return this;
	}
	
	
	
	
	
	
	
	
	/**
	 * Before invoking this method be sure that:
	 * - Any instances for the referenced classes are freed.
	 * - Their owning classloader has still instances in existence (has not been GC-ed).
	 * - The java.lang.Class object is not referenced from anywhere (same goes for reflective access to their members).
	 * 
	 */
	public void registerRools(){
		if(onlyFor!=null){
			{ // BLOCK Free up reference in the class loader
			  /*
			   This will unload the classes from the class loader if the roles for unloading a class are verified
			   */
				System.gc(); 
			}
			registerRools(onlyFor);
		}else{
			List<String> c = getAllClassesForPkgs();
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
		if(onlyFor!=null){
			throw new IllegalStateException("The register configuration does not allow for this method to be called.\n Supply at least one pkg prefix when building the object.");
		}
		List<String> c = getAllClassesForPkgs();
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
	
	
	
	
	
	
	private List<String> getAllClassesForPkgs(){
		List<String> clazzes = ClassUtils.getAllClassNames();
		Iterator<String> i = clazzes.iterator();
		for(String pkg:pkgs){
			final MATCH_TYPE matchType = matchTypePkg.get(pkg);
			next: while(i.hasNext()){	
				final String next = i.next();
				switch(matchType){
					case STARTS_WITH: 
						if(next.startsWith(pkg)){ 
							continue next;
						}/*else{assert(!pkg.startsWith(next));}*/
						break;
					case EXACT: 
						if(next.equals(pkg)){ 
							continue next; 
						}/*else{assert(!pkg.equals(next));}*/
						break;
					case REGEX: 
						if(next.matches(pkg)){
							continue next; 
						}/*else{assert(!pkg.equals(next));}*/
						break;
				}
				
				i.remove();
			}
		}
		return clazzes;
	}
	
}
