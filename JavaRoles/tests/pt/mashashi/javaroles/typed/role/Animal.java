package pt.mashashi.javaroles.typed.role;

import javassist.CtMethod;
import pt.mashashi.javaroles.Human;
import pt.mashashi.javaroles.Monkey;
import pt.mashashi.javaroles.TurnOnRole;
/**
 * We can than use other patterns to implement all the roles.
 * 
 * 
 * Order of method callback
 * + <role>
 * + <role>Stop
 * + <role>Start
 * + Pre
 * 
 * If the callback is not implemented than it will simply not be called
 * 
 * @author Rafael
 *
 */

/*
public class Animal implements Human, Monkey{
	public Role role;
	public String ret;
	public Animal(){
		role = new Role(this);
	}
	public void Pre(String role, Boolean callCount){
		this.ret = role;
	}
	public void HumanPre(Boolean callCount){
		System.out.println("Hello: "+callCount);
	}
	@Override
	public String hello() {
		return (ret = role.resolve());
	}
} 
*/

/**
 * 
 * @author Rafael
 *
 */
public class Animal implements Human, Monkey{
	
	public String ret;
	
	public String getRet(){
		return ret;
	}
	
	public Animal(){
		ret = "Init";
	}
	
	
	/**
	 * When a method of a role is called the pre is always invoked first
	 * 
	 * @param role
	 * @param method
	 */
	public void Pre(String role, CtMethod method){
		System.out.println("Pre "+role+", method "+method.getName());
		this.ret = role;
	}
	
	/**
	 * When a object which is playing another role starts to play the role human this method is called
	 * @param previous
	 */
	public void HumanStart(String previous){
		System.out.println("Human Start : "+previous);
	}
	/**
	 * When a object which is playing human stops playing it and plays another role this method is called
	 * @param next
	 */
	public void HumanStop(String next){
		System.out.println("Human Stop : "+next);
	}
	
	public void MonkeyStart(String previous){
		System.out.println("Monkey Start : "+previous);
	}
	/**
	 * This method is called the first time the role is played
	 * */
	public void HumanPre(CtMethod method){
		System.out.println("Human Pre "+method.getName());
	}
	/**
	 * This method is called the first time the role is played
	 * */
	public void Human(){
		System.out.println("Human");
	}
	public void Monkey(){
		System.out.println("Monkey");
	}
	
	
	
	
	
	
	
	@Override
	@TurnOnRole
	public String hello() {
		return ret;
	}
	
	@Override
	@TurnOnRole
	public String die(String age) {
		return "Mar";
	}

	@Override
	public String eat() {
		return null;
	}

	@Override
	public String dance() {
		// TODO Auto-generated method stub
		return null;
	}
	
}

