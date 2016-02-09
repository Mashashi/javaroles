package pt.mashashi.javaroles;


@SuppressWarnings("serial")
public class ProbablyRigidTypeNotDeclaredException extends RuntimeException{
	
	public ProbablyRigidTypeNotDeclaredException(String rigidClass, String roleClass) {
		super(StackOverflowError.class+" detected.\n The rigid class "+rigidClass+" might not be declared on th role class "+roleClass+".\n Please do it via @RoleObject(types = { "+rigidClass+".class })\npublic class "+roleClass+"...");
	}
	
}
