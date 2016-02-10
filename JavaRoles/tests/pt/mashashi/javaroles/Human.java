package pt.mashashi.javaroles;

/**
 * 
 * @author Rafael
 *
 */
public interface Human {
	void born();
	String hello(); 
	String die(String age);  
	String eat();
	String dance();
	String stuffing(String str, Object[] input);
}