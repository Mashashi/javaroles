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
	
	@SuppressWarnings("unused")
	private MissProcessingException(){}
	
	public MissProcessingException(HashMap<String, Object> details){
		this.details = details;
	}
	
	public enum WhyMiss{ NOT_FOUND, NULL_OBJECT; }
	
	public MissProcessingException(String roleName, String clazz, WhyMiss why) {
		super("Object role for role name "+roleName+" in class "+clazz+": "+why);
	}
	
	public HashMap<String, Object> getDetails(){
		return details;
	}
	
}
