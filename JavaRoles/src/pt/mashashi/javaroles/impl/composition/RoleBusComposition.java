package pt.mashashi.javaroles.impl.composition;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

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

/**
 * Very restricted implementation of role objects the point of this RoleBus is to provide a way to mixin classes
 * 
 * Does not make use of the {@link RoleBus} life cycle method callbacks
 *  
 * @author Rafael
 *
 */
public class RoleBusComposition extends RoleBus{
	
	@SuppressWarnings("unused")
	private RoleBusComposition() {}
	
	public RoleBusComposition(Object target) {
		this.target = target;
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

	public CtField getTargetObjectRoleField(CtMethod methodInvoked, CtField... exclude) throws ClassNotFoundException, NotFoundException {
		CtField ctFieldRole = null;
		List<CtField> roleObjects = ClassUtils.getListFieldAnotated(target, ObjRole.class);
		
		roleSearch: for(CtField field: roleObjects){
			
			CtMethod[] fieldMethods = field.getType().getMethods();
			for(CtMethod fieldMethod : fieldMethods){
				
				Field fieldRole = null;
				useIt: {
					//boolean useIt = fieldMethod.getName().equals(methodInvoked.getName()) && fieldMethod.getSignature().equals(methodInvoked.getSignature());
					if(!fieldMethod.equals(methodInvoked)) 
						break useIt;
					
			    	try {
			    		// check if it is not null
			    		Object o = null;
			    		String declaringClass = field.getDeclaringClass().getName();
						fieldRole = Class.forName(declaringClass).getDeclaredField(field.getName());
						o = FieldUtils.readField(fieldRole, target, true);
						
				    	if(o==null) 
				    		break useIt;
				    	
				    	// BLOCK Exclude object roles that require the rigid and it is not set on them
				    	for(Field f : ClassUtils.getListFieldAnnotated(o.getClass(), InjObjRigid.class)){
				    		InjObjRigid a = f.getAnnotation(InjObjRigid.class);
				    		if(f.getType().isInstance(target) && a.required()){
				    			Object o2 = FieldUtils.readField(f, o, true);
				    			if(o2 == null || !o2.equals(target)) 
				    				break useIt;
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
			
			roleReturned = ClassUtils.invokeWithNativeTypes(o, methodInvoked.getName(), paramsObjectRole, params);

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

}