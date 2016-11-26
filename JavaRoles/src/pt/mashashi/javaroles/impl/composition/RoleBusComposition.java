package pt.mashashi.javaroles.impl.composition;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;

import org.apache.commons.lang3.reflect.FieldUtils;

import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;
import pt.mashashi.javaroles.ClassUtils;
import pt.mashashi.javaroles.MissProcessingException;
import pt.mashashi.javaroles.RoleBus;
import pt.mashashi.javaroles.annotations.InjObjRigid;
import pt.mashashi.javaroles.annotations.MissMsgReceptor;
import pt.mashashi.javaroles.annotations.ObjRole;
import pt.mashashi.javaroles.annotations.ProxyRules;
import pt.mashashi.javaroles.injection.InjectionStrategy;

/**
 * Very restricted implementation of role objects the point of this RoleBus is to provide a way to mixin classes
 * 
 * Does not make use of the {@link RoleBus} life cycle method callbacks
 *  
 * @author Rafael
 *
 */
public class RoleBusComposition extends RoleBus{
	
	private InjectionStrategy injectionStrategy;
	
	private static WeakHashMap<String, Object> mutex = new WeakHashMap<String, Object>();
	
	@SuppressWarnings("unused")
	private RoleBusComposition() {}
	
	public RoleBusComposition(Object target, String injStrategyType) {
		this.target = target;
		injectionStrategy = InjectionStrategy.getInjectionStrategy(injStrategyType);
		
	}
	
	public Object resolve(CtMethod methodInvoked, Object[] params) throws MissProcessingException, Throwable{
		
		Object returnByRole = null;
		
	    try {
	    	boolean done = false;
	    	List<CtField> fieldsTried = new LinkedList<>();
	    	HashMap<String, Object> details = new HashMap<>();
	    	do{
				CtField ctFieldRole = getTargetObjectRoleField(methodInvoked, fieldsTried.toArray(new CtField[fieldsTried.size()]));
				if(ctFieldRole==null){
					throw new MissProcessingException(details);
				}
		    	String declaringClass = ctFieldRole.getDeclaringClass().getName();
		    	Field fieldRole = Class.forName(declaringClass).getDeclaredField(ctFieldRole.getName());
		    	try{
		    		returnByRole = invokeRoleMethod(methodInvoked, params, fieldRole, details);
		    		done = true;
		    	}catch(MissProcessingException e){
		    		// TODO Pass hashmap parameter
		    		fieldsTried.add(ctFieldRole);
		    		details = e.getDetails();
		    	}
	    	}while(!done);
		} catch (NotFoundException | NoSuchFieldException | SecurityException | ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	    	    
	    return returnByRole;
	}
	
	/**
	 * Check whether the method exist on the interface type declared on the role object or 
	 * on the list of types declared on the annotation {@link ObjRole}.
	 * 
	 * @param fieldMethod
	 * @param field
	 * @return
	 * @throws ClassNotFoundException
	 * @throws NotFoundException
	 */
	private boolean existsInAnyInterface(CtMethod fieldMethod, CtField field) throws ClassNotFoundException, NotFoundException{
		ObjRole objRole = (ObjRole) field.getAnnotation(ObjRole.class);
		Class<?>[] interfazzis = objRole.value();
		boolean computed = false;
		Class<?> objRoleType = ClassUtils.getNativeType(field.getType());
		if(objRoleType.isInterface() && !Arrays.asList(interfazzis).contains(objRoleType)){
			// if the object type of the role type is an interface it also is subject to the search
			int newPos = interfazzis.length;
			interfazzis = Arrays.copyOf(interfazzis, newPos+1);
			interfazzis[newPos] = objRoleType;
		}
		interfazzis: for(Class<?> i : interfazzis){
			for(Method m: i.getDeclaredMethods()){
				String iMethodId = ClassUtils.getMethodIdFromSignature(m.toGenericString());
				String fMethodId = ClassUtils.getMethodIdFromSignature(fieldMethod.getLongName());
				computed = iMethodId.equals(fMethodId);
				if(computed){
					break interfazzis;
				}
			}
		}
		return computed;
	}
	
	public CtField getTargetObjectRoleField(CtMethod methodInvoked, CtField... exclude) throws ClassNotFoundException, NotFoundException {
		CtField ctFieldRole = null;
		List<CtField> roleObjects = ClassUtils.getListFieldAnotated(target, ObjRole.class);
		
		roleSearch: for(CtField field: roleObjects){
			
			CtMethod[] fieldMethods = field.getType().getMethods();
			for(CtMethod fieldMethod : fieldMethods){
				
				Field fieldRole = null;
				useIt: {
					
					if(!(fieldMethod.equals(methodInvoked) && existsInAnyInterface(fieldMethod, field))){
						break useIt;
					}
					
			    	try {
			    		Object o = null;
			    		
			    		{
				    		// BLOCK check if it is not null
				    		String declaringClass = field.getDeclaringClass().getName();
							fieldRole = Class.forName(declaringClass).getDeclaredField(field.getName());
							o = FieldUtils.readField(fieldRole, target, true);
							
					    	if(o==null){
					    		break useIt;
					    	}
			    		}
			    		
				    	for(Field f : ClassUtils.getListFieldAnnotated(o.getClass(), InjObjRigid.class)){
				    		// BLOCK Exclude object roles that require the rigid and it is not set on them
				    		InjObjRigid a = f.getAnnotation(InjObjRigid.class);
				    		if(f.getType().isInstance(target) && a.required()){
				    			Object o2 = FieldUtils.readField(f, o, true);
				    			if(o2 == null || !o2.equals(target)){
				    				break useIt;
				    			}
				    		}
				    	}
				    	
				    	// TODO Check rules on the object
				    	// get all methods which have rigid, role
				    	ProxyRules proxyRules = target.getClass().getAnnotation(ProxyRules.class);
				    	if(proxyRules!=null){
				    		for(Class<?> r : proxyRules.value()){
				    			List<Method> ms1 = ClassUtils.getMethodsWithParams(r, target.getClass(), o.getClass());
				    			List<Method> ms2 = ClassUtils.getMethodsWithParams(r, target.getClass());
				    			List<List<Method>> mss = new LinkedList<>();
				    			mss.add(ms1);
				    			mss.add(ms2);
				    			
				    			for(List<Method> ms : mss){
					    			for(Method m : ms){
					    				if(	
					    						m.getReturnType().equals(Boolean.class) && 
					    						Modifier.isStatic(m.getModifiers())
					    				){
						    				try {
						    					Boolean ret =null;
						    					if(ms==ms1){
						    						ret = (Boolean) m.invoke(r, target, o); // return might be null
						    					}else if(ms==ms2){
						    						ret = (Boolean) m.invoke(r, target); // return might be null
						    					}
												if(ret!=true){ 
													break useIt;
												}
											} catch (IllegalArgumentException e) {
												// This is not supposed to happen
												throw new RuntimeException(e);
											} catch (InvocationTargetException e) {
												// TODO Possible loop nesting of runtime exceptions
												throw new RuntimeException(e.getCause());
											}
					    				}
					    			}
				    			}
				    		}
				    	}
				    	
			    	} catch (NoSuchFieldException | SecurityException | IllegalAccessException e) {
			    		throw new RuntimeException(e);
			    	}
			    	
			    	
			    	for(CtField e:exclude){
						// Exclude fields given has parameter
						if(e.getName().equals(fieldRole.getName()))
							break useIt;
					}
			    	
			    	ctFieldRole = field;
					break roleSearch;
					
				}
				
			}
		}
		return ctFieldRole;
	}
	
	private Object invokeRoleMethod(
			CtMethod methodInvoked,
			Object[] params, 
			Field objectRole, 
			HashMap<String, Object> details) throws Throwable{
		
		Object roleReturned = null;
		
		try {
			Object o = FieldUtils.readField(objectRole, target, true);
							
			{// Set miss msg receptor
				HashMap<String, Field> tmsg = ClassUtils.getTypeFieldAnotatedNative(o, MissMsgReceptor.class);
				for(Field v : tmsg.values()){
					v.set(o, details);
				}
			}
			
			Class<?>[] paramsObjectRole = ClassUtils.getNativeTypes(methodInvoked.getParameterTypes());
			
			if(Modifier.isStatic(objectRole.getModifiers())){
				synchronized ( getMutex(Integer.toString(o.hashCode())) ) {
					// BLOCK Make static roles assume roles each time a method is invoked through a rigid
					injectionStrategy.doIt(o, target, true);
					roleReturned = ClassUtils.invokeWithNativeTypes(o, methodInvoked.getName(), paramsObjectRole, params);
				}
			}else{
				roleReturned = ClassUtils.invokeWithNativeTypes(o, methodInvoked.getName(), paramsObjectRole, params);
			}
			
		}catch (NotFoundException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			
			// TODO Enhance error handling code elegance
			
			if(e.getClass().equals(InvocationTargetException.class)){
				Throwable cause = e.getCause();
				if(cause.getClass().equals(MissProcessingException.class)){
					// Programmer method doesn't wish to process
					throw (MissProcessingException) e.getCause();
				}else{
					 if(cause.getClass().equals(StackOverflowError.class)){
						 // Miss use of rigid type
						throw (StackOverflowError) cause; 
					}else{
						// Error thrown by programmer method
						throw (Throwable) cause;
					}
				}
			}else{
				// Unknown error
				throw new RuntimeException(e);
			}
			
			
		}
		
		return roleReturned;
	}
	
	
	private Object getMutex(String id){
		Object monitor = null;
		synchronized(mutex){
			monitor = mutex.get(id);
			if(monitor==null){
				monitor = new Object();
				mutex.put(id, monitor);
			}
		}
		return monitor;
	}
	
}