package pt.mashashi.javaroles;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import javassist.CtMethod;

/**
 * 
 * Offers to the subclasses that extend it an easy way to expose callback functions to control role object life cycle
 * 
 * An instance of this class is injected into each method that was role like capabilities
 * 
 * The method {@link pt.mashashi.javaroles.RoleBus#resolve(CtMethod, Object[])} is the main part that should be present in this
 * code injection
 * 
 * A instance of a class that extends {@link pt.mashashi.javaroles.RoleRegister} is responsible to do such code injection
 * 
 * @author Rafael
 * @see RoleRegister
 */
public abstract class RoleBus {
	 
	protected Object target;
	
	// key - rolename
	// value - first invocation
	private HashMap<String, Boolean> rollCallMade;  
	
	private CircularLifoBuffer callHistory;
	
	public RoleBus() {
		rollCallMade = new HashMap<>();
		callHistory = new CircularLifoBuffer(5);
	}
	
	/**
	 * Runs the object role method
	 * 
	 * @param methodInvoked The method that was invoked on the rigid type
	 * @param params
	 * @return The output of the selected object role method
	 * @throws MissProcessingException If no object role method was found
	 */
	public abstract Object resolve(CtMethod methodInvoked, Object[] params) throws MissProcessingException, Throwable;
	
	protected void invokeLifeCycleCallbacks(String roleName, CtMethod methodInvoked) {
		Boolean roleNameFirstCall = rollCallMade.get(roleName);
		
		if(roleNameFirstCall==null){
			roleNameFirstCall = Boolean.TRUE;
		}
		
		if(roleNameFirstCall){
			try {
				Method callback = target.getClass().getMethod(roleName);
				callback.invoke(target);
			} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				// Do nothing user probably didn't implement the method for call back
				//e.printStackTrace();
			}
		}
		
		{	String oldRole = (String) callHistory.get();
			if(!roleName.equals(oldRole)){
				try {
					Method callback = target.getClass().getMethod(oldRole+"Stop", String.class);
					callback.invoke(target, roleName);
				} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					// Do nothing user probably didn't implement the method for call back
					//e.printStackTrace();
				}
				try {
					Method callback = target.getClass().getMethod(roleName+"Start", String.class);
					callback.invoke(target, oldRole);
				} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					// Do nothing user probably didn't implement the method for call back
					//e.printStackTrace();
				}
			}
		}
		
		try {
			Method callback = target.getClass().getMethod(roleName+"Pre", CtMethod.class);
			callback.invoke(target, methodInvoked);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			// Do nothing user probably didn't implement the method for call back
			//e.printStackTrace();
		}

		try {
			Method callback = target.getClass().getMethod("Pre", String.class, CtMethod.class);
			callback.invoke(target, roleName, methodInvoked);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			// Do nothing user probably didn't implement the method for call back
			//e.printStackTrace();
		}
		
		callHistory.add(roleName);
		
		rollCallMade.put(roleName, Boolean.FALSE);
	}
	
}