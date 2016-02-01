package pt.mashashi.javaroles;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;

/**
 * Offers a way to go through all the classes in the class path searching for the points that need code injection
 * 
 * @author Rafael
 *
 */
public abstract class RoleRegister {
	
	protected String roleBusVarName;
	
	public RoleRegister(){
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
	protected abstract void injectRoleDependency(CtClass cn, CtMethod method, CtField roleObjectClass) throws CannotCompileException, NotFoundException;
	
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
		
		try {
							
			CtMethod[] methods = cn.getDeclaredMethods();
			HashMap<String, CtField> objectRoles = ClassUtils.getTypeFieldAnotated(cn, ObjectForRole.class);
			
			methodInj: for(CtMethod method : methods){
				CtClass i = ClassUtils.definedOnInterface(method, cn);
				CtField roleObject = objectRoles.get(i!=null?i.getSimpleName():"");
				
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
						
						cn.addField(
								CtField.make(
										getRoleBusDeclaration(), cn
										)
								);
					}
					
					injectRoleDependency(cn, method, roleObject);
					
					wasInjected = true;
					
					Logger.getLogger(RoleBus.class.getName()).debug(cn.getName()+" add code injected in method "+method.getName()+" done");
					
				}
				
			}
			
			if(wasInjected){
				cn.writeFile();
				cn.toClass();
			}
		
		} catch (CannotCompileException | NotFoundException | IOException | ClassNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
			
	}
	
	public void registerRools(){
		List<String> c = ClassUtils.getAllClassNames();
		for(String className : c){
			registerRool(className);
		}
	}
	
}
