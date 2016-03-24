package pt.mashashi.javaroles.register;

import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewConstructor;
import javassist.CtNewMethod;
import javassist.NotFoundException;
import pt.mashashi.javaroles.ClassUtils;
import pt.mashashi.javaroles.RoleBus;
import pt.mashashi.javaroles.annotations.AnnotationException;
import pt.mashashi.javaroles.annotations.MissMsgReceptor;
import pt.mashashi.javaroles.annotations.MissUseAnnotationExceptionException;
import pt.mashashi.javaroles.annotations.ObjRigid;
import pt.mashashi.javaroles.annotations.ObjRole;
import pt.mashashi.javaroles.annotations.Play;
import pt.mashashi.javaroles.annotations.Player;
import pt.mashashi.javaroles.annotations.Play.Place;
import pt.mashashi.javaroles.composition.TestPlay;
import pt.mashashi.javaroles.injection.InjectionStrategy;

/**
 * Offers a way to go through all the classes in the class path searching for the points that need code injection
 * 
 * @author Rafael
 *
 */
public abstract class RoleRegister {
	
	public enum MatchType{ 
		EXACT
		,STARTS_WITH
		,REGEX
	}
	
	protected String roleBusVarName;
	
	private ClassPool cp;
	private List<String> onlyFor;
	private List<String> excludeGiven;
	private HashMap<String, MatchType> matchType;
	private List<String> pkgs;
	private InjectionStrategy injRigStrategy  = InjectionStrategy.getInstanceSingle();
	//protected InjectionStrategy injRigStrategy = new InjectionStrategyMultiple();
	private Collection<String> clazzesForPkgs;
	private List<String> classReport;
	
	String classesDir;
	ClassScheduler classScheduler;
	
	public RoleRegister(){
		{
			// CONFIG Suppress console output from log4j missing config file 
			// log4j:WARN No appenders could be found for logger... When log4j config file is not set
			Logger.getRootLogger().setLevel(Level.OFF);
		}
		roleBusVarName = ClassUtils.generateIdentifier();
		cp = ClassPool.getDefault();
		matchType = new HashMap<>();
		classReport = new LinkedList<>();
		classScheduler = new ClassScheduler();
		excludeGiven = new LinkedList<String>();
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
			
			applyInjections(cn);
			
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
						//System.out.println(clazzName);
						wasInjected = true;
						CtField newField = CtField.make(getRoleBusDeclaration(), cn);
						cn.addField(newField);
					}
					
					CtMethod created = injectRoleDependency(cn, method);
					
					List<CtClass> inters = ClassUtils.definedOnInterfaces(method, cn);
					applyIndirect(method, created, originals, inters);
					
					Logger.getLogger(RoleBus.class.getName()).debug(cn.getName()+" add code injected in method "+method.getName()+" done");
					
				}
				
			}
			
			
			if(wasInjected || originals.size()!=0){
				// COMMENT // cn.writeFile(); - Writing to file will create a new .class file. Just use .toClass(); to place the class available on the class path
				/*
				 We want to put the class on the class path after modifications if it was injected with new methods or if 
				 it was some fields annotated with original. In the later case we change the constructor.
				 */
				classScheduler.scheduleFinalCmd(CmdCloseClass.neu(this,cn));
				classReport.add(clazzName);
			}
			
			{ // BLOCK freeze originals
				classScheduler.scheduleFinalCmd(new CmdCloseClasses(originals.values(),classesDir));
			}
			
		} catch (CannotCompileException | NotFoundException | ClassNotFoundException e) {
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
		List<CtField> objectRoles = ClassUtils.getListFieldAnnotated(cn, MissMsgReceptor.class);
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

	private void applyInjections(CtClass cn) throws ClassNotFoundException {
		if(!cn.isFrozen()){
			try {
				LinkedList<CtConstructor> pConstructor = new LinkedList<CtConstructor>();
				{ // BLOCK Process constructor
					Player a = (Player) cn.getAnnotation(Player.class); 
					if(a!=null){
						for(CtConstructor c: cn.getConstructors()){
							c.insertAfter(injRigStrategy.setAll());
							/*
								// OLDFEAT - Try to call the inject before constructor.
								// Not done it has hard because default initializations injected byte code into the constructor.
								// Another alternative is to use the insertAt but we have to have a method to find out where the code begins
								// Another problem is that the injected code makes use of the keyword this which is not available before the constructor starts
								final String name = ClassUtils.generateIdentifier();
								final CtMethod method = c.toMethod(name, cn);
								final String varC = ClassUtils.generateIdentifier(); 
								cn.addMethod(method);
								c.setBody("{"
										
										+injStrategy.getCode()+
										
										CtConstructor.class.getName()+" "+varC+" = "+ClassUtils.class.getName()+".getExecutingConstructor("+
										"\""+cn.getName()+"\","+
										"\""+c.getSignature()+"\");"+
										
										ClassUtils.class.getName()+".invokeWithNativeTypes("+
										"this,"+
										"\""+name+"\","+
										varC+".getParameterTypes(),"+
										"$args);"
										
										+"}");
							 */
							pConstructor.add(c);
							Logger.getLogger(RoleBus.class.getName()).trace("Test:"+TestPlay.class.getName()+"-"+c.getName()+"-rc");
						}	
					}
				}
				{ // BLOCK Process play constructors
					for(CtConstructor c: cn.getConstructors()){
						Player a = (Player) c.getAnnotation(Player.class);
						if(a!=null && !pConstructor.contains(c)){
							c.insertAfter(injRigStrategy.setAll());
							Logger.getLogger(RoleBus.class.getName()).trace("Test:"+TestPlay.class.getName()+"-"+c.getName()+"-pc");
						}
					}
				}
				
				{ // BLOCK Process play methods
					for(CtMethod m: cn.getDeclaredMethods()){
						Play a = (Play) m.getAnnotation(Play.class);
						if(a!=null){
							if(m.getParameterTypes().length==0){
								insertAccordingOrder(m, a, injRigStrategy.setAll());
								Logger.getLogger(RoleBus.class.getName()).trace("Test:"+TestPlay.class.getName()+"-"+m.getDeclaringClass().getName()+"-"+m.getName()+"-pm");
							}else{
								insertAccordingOrder(m, a, injRigStrategy.setParams());
							}
						}
					}
				}
				
				/*{ // TODO Inherit annotations
					for(CtMethod m: cn.getDeclaredMethods()){
						InheritAnnots a = (InheritAnnots) m.getAnnotation(InheritAnnots.class);
						if(a!=null){
							classScheduler.scheduleNextCommand(new CmdExtendAnnotation(this, m));
						}
					}
				}*/
				
				
				
				
			} catch (SecurityException|CannotCompileException|NotFoundException e) {
				throw new RuntimeException(e.getMessage());
			} 
		}
	}
	
	private void insertAccordingOrder(CtBehavior m, Play a, String code) throws ClassNotFoundException, CannotCompileException{
		assert a!=null;
		assert a.order().equals(Place.AFTER) || a.order().equals(Place.BEFORE);
		if(a.order().equals(Place.AFTER)){
			m.insertAfter(code);//m.hashCode()
		}else{
			m.insertBefore(code);
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
		List<CtField> objectOriginal = ClassUtils.getListFieldAnnotated(cn, ObjRigid.class);
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
				//final String core = ClassUtils.generateIdentifier();
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
	
	
	
	
	public RoleRegister setMatchType(MatchType matchT){
		if(pkgs!=null){
			for(String pkg : pkgs){ 
				matchType.put(pkg, matchT); 
			}
		}
		if(onlyFor!=null){
			for(String clazz : onlyFor){ 
				matchType.put(clazz, matchT); 
			}
		}
		return this;
	}
	/*public RoleRegister setPkgMatchType(int pkgIdx, MATCH_TYPE_PKG matchType){
		matchTypePkg.put(pkgs.get(pkgIdx), matchType);
		return this;
	}*/
	public RoleRegister writeClasses(String dir){
		classesDir = dir;
		return this;
	}
	public RoleRegister setRigidInjectionStrategy(InjectionStrategy injRigStrategy){
		this.injRigStrategy = injRigStrategy;
		return this;
	}
	
	public RoleRegister includeGiven(Class<?>... clazzes){
		//this.onlyFor = new LinkedList<String>();
		if(this.onlyFor==null){
			this.onlyFor = new LinkedList<>();
		}
		for(Class<?> clazz : clazzes){
			onlyFor.add(clazz.getName());
		}
		setMatchType(MatchType.STARTS_WITH);
		return this;
	}
	
	/**
	 * Sometimes when using classes some problems may arise to to the class loader. When so try to use a string identifying the
	 * class to workaround the problem.
	 * 
	 * @param clazzes
	 * @return
	 */
	public RoleRegister includeGivenRaw(String... clazzes){
		//this.onlyFor = new LinkedList<String>();
		if(this.onlyFor==null){
			this.onlyFor = new LinkedList<>();
		}
		for(String clazz : clazzes){
			onlyFor.add(clazz);
		}
		setMatchType(MatchType.STARTS_WITH);
		return this;
	}
	
	public RoleRegister excludeGiven(Class<?>... clazzes){	
		//excludeGiven.clear();
		if(clazzes!=null){
			for(Class<?> clazz : clazzes){
				excludeGiven.add(clazz.getName());
			}
		}
		return this;
	}
	
	public RoleRegister excludeGivenRaw(String... clazzes){	
		//excludeGiven.clear();
		if(clazzes!=null){
			for(String clazz : clazzes){
				excludeGiven.add(clazz);
			}
		}
		return this;
	}
	
	public RoleRegister includeGivenPkg(Class<?>... pkgs){	
		if(pkgs!=null){
			this.pkgs = new LinkedList<>();
			for(Class<?> pkg : pkgs){
				this.pkgs.add(pkg.getPackage().getName());
			}
		}
		setMatchType(MatchType.STARTS_WITH);
		return this;
	}
	
	public RoleRegister excludeGivenPkg(Class<?>... pkgs){	
		//excludeGiven.clear();
		if(pkgs!=null){
			for(Class<?> pkg : pkgs){
				excludeGiven.add(pkg.getPackage().getName());
			}
		}
		return this;
	}

	public RoleRegister inheritAnnots(){
		classScheduler.scheduleNextCmd(new CmdExtendAnnotationFind(this));
		classScheduler.execSchedule();
		return this;
	}
	
	public RoleRegister callSuperAnnots(){
		classScheduler.scheduleNextCmd(new CmdSuperAnnotationFind(this));
		classScheduler.execSchedule();
		return this;
	}
	
	
	
	
	
	/**
	 * Before invoking this method be sure that:
	 * - Any instances for the referenced classes are freed.
	 * - Their owning classloader has still instances in existence (has not been GC-ed).
	 * - The java.lang.Class object is not referenced from anywhere (same goes for reflective access to their members).
	 * 
	 */
	public void registerRoles(){
		
		// classScheduler.scheduleCommand(new Class, Order.NEXT);
		
		/*
		classScheduler.executeSchedule();
		*/
		
		if(onlyFor!=null){
			
			{ // BLOCK Free up reference in the class loader
				  /*
			   This will unload the classes from the class loader if the roles for unloading a class are verified
			   */
				System.gc(); // Doesn't work for inner inner classes
			}
			
			List<String> computedOnlyFor = new LinkedList<String>(onlyFor);
			
			{ // BLOCK Remove classes to exclude
				for(String o : onlyFor){
					for(String e : excludeGiven){
						if(o.startsWith(e)){
							 computedOnlyFor.remove(o);
						}
					}
				}
			}
			
			registerRoles(computedOnlyFor);
			
		}else{
			
			for(String className : getAllClassesForPkgs()){
				//System.out.println("-->"+className);
				registerRool(className);
			}
		}
		classScheduler.execSchedule();
		classScheduler.finalize();
	}
	
	private void registerRoles(List<String> clazzes){
		for(String clazz :clazzes){
			CtClass c = null;
			try {
				c = cp.get(clazz);
			} catch (NotFoundException e) {
				throw new RuntimeException(e.getMessage());
			}
			try {
				final MatchType match = matchType.get(clazz);
				if(match!=null && match.equals(MatchType.EXACT)){
					// Don't register inner classes
				}else{ //if(match.equals(MatchType.STARTS_WITH)){
					for(CtClass i : c.getDeclaredClasses()){
						List<String> t = new LinkedList<String>();
						t.add(i.getName());
						registerRoles(t);
					}
				}
				
			} catch (NotFoundException e) {
				throw new RuntimeException(e.getMessage());
			}
			registerRool(clazz);
		}
	}
	
	/**
	 * Memoization was applied
	 * 
	 * @return
	 */
	Collection<String> getAllClassesForPkgs(){
		
		
		if(clazzesForPkgs==null){
			
			{ // TODO
				if(this.pkgs==null || this.pkgs.size()==0){ 
					//this.pkgs = new String[0]; 
					this.pkgs = new LinkedList<String>();
					this.pkgs.add("");
				}
				if(this.pkgs.size()==0){ throw new IllegalArgumentException("Supply at least one package perfix."); }
				setMatchType(MatchType.STARTS_WITH);
			}
			
			clazzesForPkgs = ClassUtils.getAllClassNames();
			
			Iterator<String> i = clazzesForPkgs.iterator();
			for(String pkg:pkgs){
				//final MATCH_TYPE_PKG matchType = matchTypePkg.get(pkg);
				next: while(i.hasNext()){
					final String next = i.next();
					
					boolean exclude = false;
					
					if(onlyFor!=null){
						// BLOCK just process classes that are on only for if setted
						checkOnlyFor: {
							for(String o : onlyFor){
								if(match(next,o))
									break checkOnlyFor;
							}
							exclude = true;
						}
					}
					
					if(!exclude){
						// BLOCK exclude classes block
						for(String clazzNameExclude : excludeGiven){
							if(next.startsWith(clazzNameExclude)){ 
								// BLOCK The condition is with .startsWith because we want to stop registration of inner classes
								exclude = true;
							}
						}
					}
					
					if(!exclude)
						if(match(next, pkg)) 
							continue next;
					
					i.remove();
				}
			}
		}
		
		return clazzesForPkgs;
	}
	
	
	private boolean match(String current, String matcher){
		
		final MatchType match = matchType.get(matcher);
		boolean r = false;
		switch(match){
			case STARTS_WITH: 
				r = current.startsWith(matcher);
				break;
			case EXACT: 
				r = current.equals(matcher);
				break;
			/*
			case REGEX: 
				r = next.matches(pkg));
				break;
			*/
			default:
				throw new RuntimeException("Match type is undefined for: "+matcher);
		}
		
		return r;
	}
	
}