package pt.mashashi.javaroles.composition;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.log4j.Logger;

import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;
import pt.mashashi.javaroles.ClassUtils;
import pt.mashashi.javaroles.MissProcessingException;
import pt.mashashi.javaroles.ObjectForRole;
import pt.mashashi.javaroles.ProbablyRigidTypeNotDeclaredException;
import pt.mashashi.javaroles.RoleBus;

/**
 * Very restricted implementation of role objects the point of this RoleBus is to provide a way to mixin classes
 * 
 * Does not make use of the {@link RoleBus} life cycle method callbacks
 *  
 * @author Rafael
 *
 */
public class RoleBusComposition extends RoleBus{
	
	private RoleBusComposition() {}
	
	public RoleBusComposition(Object target) {
		this();
		this.target = target;
	}
	
	public Object resolve(CtMethod methodInvoked, Object[] params) throws MissProcessingException{
		
		Object returnByRole = null;
		
	    try {
	    	
			// invokeLifeCycleCallbacks(roleName, methodInvoked);
			CtField ctFieldRole = getTargetObjectRoleField(methodInvoked);
			if(ctFieldRole==null){
				throw new MissProcessingException(methodInvoked.getClass().getSimpleName(), target.getClass().getName(), MissProcessingException.WhyMiss.NULL_OBJECT);
			}
	    	String declaringClass = ctFieldRole.getDeclaringClass().getName();
	    	Field fieldRole = Class.forName(declaringClass).getDeclaredField(ctFieldRole.getName());
			returnByRole = invokeRoleMethod(methodInvoked, params, fieldRole);
			
		} catch (NotFoundException | NoSuchFieldException | SecurityException | ClassNotFoundException e) {
			Logger.getLogger(RoleBus.class.getName()).debug("error resolving method: "+methodInvoked.getLongName()+" "+e.getMessage());
		}
	    	    
	    return returnByRole;
	}

	public CtField getTargetObjectRoleField(CtMethod methodInvoked) throws ClassNotFoundException, NotFoundException {
		CtField ctFieldRole = null;
		List<CtField> roleObjects = ClassUtils.getListFieldAnotated(target, ObjectForRole.class);
		
		roleSearch: for(CtField field: roleObjects){
			
			CtMethod[] fieldMethods = field.getType().getMethods();
			for(CtMethod fieldMethod : fieldMethods){
				boolean useIt = fieldMethod.getName().equals(methodInvoked.getName()) &&  fieldMethod.getSignature().equals(methodInvoked.getSignature());
				{ // check if it is not null
					Object o = null;
			    	try {
			    		String declaringClass = field.getDeclaringClass().getName();
						Field fieldRole = Class.forName(declaringClass).getDeclaredField(field.getName());
						o = FieldUtils.readField(fieldRole, target, true);
					} catch (NoSuchFieldException | SecurityException | IllegalAccessException e) {}
			    	useIt = useIt && o!=null;
				}
				
				if(useIt){	
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
			Field objectRole) throws NotFoundException {
		
		Object roleReturned = null;
		
		if(objectRole!=null){
			try {
				
				Object o = FieldUtils.readField(objectRole, target, true);
				if(o == null){
					throw new MissProcessingException(methodInvoked.getClass().getSimpleName(), target.getClass().getName(), MissProcessingException.WhyMiss.NULL_OBJECT);
				}
				Class<?>[] paramsObjectRole = ClassUtils.getNativeTypes(methodInvoked.getParameterTypes());
				roleReturned = o.getClass().getMethod(methodInvoked.getName(), paramsObjectRole).invoke(o, params);
				
			}catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | SecurityException  | ClassNotFoundException e) {
				
				//
				if(e.getClass().equals(InvocationTargetException.class)){
					Throwable cause = e.getCause();
					if(cause.getClass().equals(MissProcessingException.class)){
						throw (MissProcessingException) e.getCause();
					}else{
						if(cause.getClass().equals(StackOverflowError.class) || cause.getClass().equals(ProbablyRigidTypeNotDeclaredException.class)){
							// throw new MissProcessingException(); // This would cause the method on the rigid type to be called without warning the programmer.
							throw new ProbablyRigidTypeNotDeclaredException(methodInvoked.getDeclaringClass().getName(), objectRole.getType().getName());
						}else{
							Logger.getLogger(RoleBus.class.getName()).debug("error calling "+methodInvoked.getLongName()+" "+cause.getMessage());
							e.printStackTrace();
							throw new RuntimeException();
						}
					}
				}
				
				
			}
		} else {
			throw new MissProcessingException(methodInvoked.getClass().getSimpleName(), target.getClass().getName(), MissProcessingException.WhyMiss.NOT_FOUND);
		}
		
		return roleReturned;
	}

}