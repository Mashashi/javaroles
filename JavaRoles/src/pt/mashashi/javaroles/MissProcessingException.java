package pt.mashashi.javaroles;

import java.util.HashMap;

/**
 * This class exception is thrown in two cases:
 * 
 * 1. When the definition for the role is not found;
 * 2. When the role implementor does not wish to process the request and so the implementation on the rigid type is called.
 * 
 * @author Rafael
 *
 */
@SuppressWarnings("serial")
public class MissProcessingException extends RuntimeException {
	
	private HashMap<String, Object> details;
	
	public enum WhyMiss{ NOT_FOUND_ROLE; }
	
	public MissProcessingException(){
		details = new HashMap<>();
	}
	
	public MissProcessingException(HashMap<String, Object> details){
		this.details = details;
	}
	public MissProcessingException(Object... params){
		details = new HashMap<>();
		int paramNumber = 0;
		for(Object p:params){
			details.put(Integer.toString(paramNumber), p);
		}
	}
	
	public MissProcessingException(String roleName, String clazz, WhyMiss why) {
		super("Object role for role name "+roleName+" in class "+clazz+": "+why);
	}
	
	public HashMap<String, Object> getDetails(){
		return details;
	}
	
}
