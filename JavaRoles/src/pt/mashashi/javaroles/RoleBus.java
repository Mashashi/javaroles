package pt.mashashi.javaroles;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import javassist.CtMethod;
import pt.mashashi.javaroles.register.RoleRegister;

/**
 * 
 * Offers to the subclasses that extend it an easy way to expose callback functions to control role object life cycle
 * 
 * An instance of this class is injected into each method that was role like capabilities
 * 
 * The method {@link pt.mashashi.javaroles.RoleBus#resolve(CtMethod, Object[])} is the main part that should be present in this
 * code injection
 * 
 * A instance of a class that extends {@link pt.mashashi.javaroles.register.RoleRegister} is responsible to do such code injection
 * 
 * @author Rafael
 * @see RoleRegister
 */
public abstract class RoleBus {
	
	/**
	 * Object in witch the this role bus was injected
	 */
	protected Object target;
	
	// key - rolename
	// value - first invocation
	private HashMap<String, Boolean> roleCallMade;  
	
	private CircularLifoBuffer callHistory;
	
	public RoleBus() {
		roleCallMade = new HashMap<>();
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
	
	private abstract class DealWithInvocationErrors{
		public abstract void run(Object... params) throws NoSuchMethodException, InvocationTargetException, SecurityException, IllegalAccessException, IllegalArgumentException;
		public void invoke(Object... params) throws Throwable{
			try {
				run(params);
			} catch (NoSuchMethodException e) {
				// Do nothing user probably didn't implement the method for call back
				//e.printStackTrace();
			} catch (InvocationTargetException e){
				// An exception was thrown inside the invoke method
				throw e.getCause();
			} catch (SecurityException | IllegalAccessException | IllegalArgumentException e){
				// Not handled
			}
		}
	}
	
	private DealWithInvocationErrors invokeConstructor = new DealWithInvocationErrors() {
		
		@Override
		public void run(Object... params) throws NoSuchMethodException, InvocationTargetException, SecurityException,
				IllegalAccessException, IllegalArgumentException {
			Method callback = target.getClass().getMethod((String) params[0]);
			callback.invoke(target);	
		}
		
	};
	
	private DealWithInvocationErrors invokeStopRole = new DealWithInvocationErrors() {
		
		@Override
		public void run(Object... params) throws NoSuchMethodException, InvocationTargetException, SecurityException,
				IllegalAccessException, IllegalArgumentException {
			Method callback = target.getClass().getMethod(params[0]+"Stop", String.class);
			callback.invoke(target, params[1]);
		}
		
	};
	
	private DealWithInvocationErrors invokeStartRole = new DealWithInvocationErrors() {
		
		@Override
		public void run(Object... params) throws NoSuchMethodException, InvocationTargetException, SecurityException,
				IllegalAccessException, IllegalArgumentException {
			Method callback = target.getClass().getMethod(params[0]+"Start", String.class);
			callback.invoke(target, params[1]);
		}
		
	};
	
	private DealWithInvocationErrors invokePreRole = new DealWithInvocationErrors() {
		
		@Override
		public void run(Object... params) throws NoSuchMethodException, InvocationTargetException, SecurityException,
				IllegalAccessException, IllegalArgumentException {
			Method callback = target.getClass().getMethod(params[0]+"Pre", CtMethod.class);
			callback.invoke(target, params[1]);
		}
		
	};
	
	
	private DealWithInvocationErrors invokePre = new DealWithInvocationErrors() {
		
		@Override
		public void run(Object... params) throws NoSuchMethodException, InvocationTargetException, SecurityException,
				IllegalAccessException, IllegalArgumentException {
			Method callback = target.getClass().getMethod("Pre", String.class, CtMethod.class);
			callback.invoke(target, params[0], params[1]);
		}
		
	};
	
	protected void invokeLifeCycleCallbacks(String roleName, CtMethod methodInvoked) throws Throwable {
		Boolean roleNameFirstCall = roleCallMade.get(roleName);
		
		if(roleNameFirstCall==null){
			roleNameFirstCall = Boolean.TRUE;
		}
		
		if(roleNameFirstCall){
			invokeConstructor.invoke(roleName);
		}
		
		String oldRole = (String) callHistory.get();
		if(!roleName.equals(oldRole)){
			
			invokeStopRole.invoke(oldRole, roleName);
			
			invokeStartRole.invoke(roleName, oldRole);
			
		}
		
		invokePreRole.invoke(roleName, methodInvoked);

		invokePre.invoke(roleName, methodInvoked);
		
		callHistory.add(roleName);
		
		roleCallMade.put(roleName, Boolean.FALSE);
	}
	
}