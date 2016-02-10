package pt.mashashi.javaroles;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

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
import pt.mashashi.javaroles.composition.OriginalRigid;

/**
 * Offers a way to go through all the classes in the class path searching for the points that need code injection
 * 
 * @author Rafael
 *
 */
public abstract class RoleRegister {
	
	protected String roleBusVarName;
	
	public RoleRegister(){
		Logger.getRootLogger().setLevel(Level.OFF); // Suppress console log4j:WARN No appenders could be found for logger... When log4j config file is not set
		roleBusVarName = "roleBus"+UUID.randomUUID().toString().replace("-", "");
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
	protected abstract CtMethod injectRoleDependency(CtClass cn, CtMethod method, CtField roleObjectClass) throws CannotCompileException, NotFoundException;
	
	/**
	 * Returns a string having the RoleBus method field declaration to be injected
	 * 
	 * @return
	 */
	protected abstract String getRoleBusDeclaration();
	
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
		
		ClassPool cp = ClassPool.getDefault();
		CtClass cn = cp.getOrNull(clazzName);
		boolean wasInjected = false;
		
		
		/*{ // Insert a method call in every method of a class that has an annotation
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
							
			CtMethod[] methods = cn.getDeclaredMethods();
			HashMap<String, CtField> objectRoles = ClassUtils.getTypeFieldAnotatedAssist(cn, ObjectForRole.class);
			
			HashMap<String, CtClass> originals = getOriginals(cn);
			
			//System.out.println(cn.getName()+" "+originals.size());
			
			methodInj: for(CtMethod method : methods){
				
				List<CtClass> inters = ClassUtils.definedOnInterfaces(method, cn); // possible bug hear
				CtField roleObject = objectRoles.get(inters.size()!=0?inters.get(0).getSimpleName():"");
				
				boolean isTargetInjection = method.getAnnotation(TurnOffRole.class)==null && (method.getAnnotation(TurnOnRole.class)!=null || roleObject!=null);	
				if(isTargetInjection){
					
					if(!wasInjected){
						
						if(cn.isFrozen()){
							// If the class was already loaded don't patch it. Maybe it was already processed
							// This happens when running multiple test cases via "mvn test"
							// The registerRool method will be called multiple times
							Logger.getLogger(RoleBus.class.getName()).debug(cn.getName()+" is frozen");
							break methodInj;
						}
						
						CtField newField = CtField.make(getRoleBusDeclaration(), cn);
						
						cn.addField(newField);
						
					}
					
					CtMethod created = injectRoleDependency(cn, method, roleObject);
					
					applyIndirect(method, created, originals, inters);
					
					wasInjected = true;
					
					Logger.getLogger(RoleBus.class.getName()).debug(cn.getName()+" add code injected in method "+method.getName()+" done");
					
				}
				
			}
			
			if(wasInjected){
				//cn.writeFile();
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

	public void applyIndirect(CtMethod method, CtMethod created, HashMap<String, CtClass> originals,
			List<CtClass> inters) throws CannotCompileException {
		for(CtClass i:inters){
			CtClass o = originals.get(i.getName());
			if(o!=null){
				for(CtMethod mo: o.getMethods()){
					if(method.getName().equals(mo.getName()) && method.getSignature().equals(mo.getSignature())){
						final String varM = ClassUtils.generateIdentifier();
						mo.setBody(
							"{"+
								CtMethod.class.getName()+" "+varM+" = "+ClassUtils.class.getName()+".getExecutingMethod("+
																			"\""+o.getName()+"\","+
																			"\""+method.getName()+"\","+
																			"\""+method.getSignature()+"\");"+
								"return ($r) "+ClassUtils.class.getName()+".invokeWithNativeTypes("+
								"core,"+
								"\""+created.getName()+"\","+
								varM+".getParameterTypes(),"+
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
			// Ignore frozen classes, assume already processed
			return originals;
		}
		ClassPool cp = ClassPool.getDefault();
		List<CtField> objectOriginal = ClassUtils.getListFieldAnotated(cn, OriginalRigid.class);
		Iterator<CtField> ite = objectOriginal.iterator();
		while(ite.hasNext()){
			CtField n = ite.next();
			CtClass evalClass = cp.makeClass(ClassUtils.generateIdentifier());
			CtClass i = cp.getOrNull(n.getType().getName());
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
					evalClass.addMethod(m);
				}
			}
			evalClass.addConstructor(CtNewConstructor.make("public " + evalClass.getSimpleName() + "("+i.getName()+" core) {this.core = core;}", evalClass));
			//evalClass.toClass();
			for(CtConstructor con : n.getDeclaringClass().getConstructors()){
				con.insertAfter("this."+n.getName()+" = new "+evalClass.getName()+"(this);");
			}
			
		}
		return originals;
	}
	
	public void registerRools(){
		List<String> c = ClassUtils.getAllClassNames();
		for(String className : c){
			registerRool(className);
		}
	}
	
}
