package pt.mashashi.javaroles.typed.rolemethod;

import java.util.HashMap;

/**
 * 
 * @author Rafael
 *
 */
public class MaleAnimalRoles extends AnimalRoles{
	
	public MaleAnimalRoles() {
		super();
		super.monkey = new MaleBonobo();
	}
	
	public void hello_MissProcessing(String role, HashMap<String,Object> args) {
		System.out.println("Process missing from role: "+role+". Argument count: "+args.size()+".");
	}
}
