package pt.mashashi.javaroles.impl.typed;

/**
 * 
 * @author Rafael
 *
 */
@SuppressWarnings("serial")
public class ConditionalExprNotSupportedByRoleSystemException extends RuntimeException{
	public ConditionalExprNotSupportedByRoleSystemException(String expr) {
		super("Conditional expressions are not supported by the role system. Exception on code: "+expr);
	}
}