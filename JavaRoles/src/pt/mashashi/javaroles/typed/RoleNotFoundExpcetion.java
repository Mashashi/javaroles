package pt.mashashi.javaroles.typed;

/**
 * 
 * @author Rafael
 *
 */
@SuppressWarnings("serial")
class RoleNotFoundExpcetion extends RuntimeException{
	public RoleNotFoundExpcetion(String in){
		super("Role name in \""+in+"\" not found.");
	}
}