package pt.mashashi.javaroles.composition;

import pt.mashashi.javaroles.Human;

/**
 * 
 * @author Rafael
 *
 */
public class Portuguese implements Human{
	
	public static final String HALLO = "Olá moço";
	public static final String DIE = "Ai matarem-me...";
	public static final String EAT = "Estou cheio dred";
	
	public AnimalRoles core;
	
	public Portuguese() {}
	
	@Override
	public String hello() {
		return HALLO;
	}

	@Override
	public String die(String age) {
		return DIE+age;
	}

	@Override
	public String eat() {
		return EAT;
	}

	@Override
	public String dance() {
		return core.originalHuman1.dance()+" modified!";
	}

	@Override
	public String stuffing(String str, Object[] input) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void born() {
		System.out.println("Portuguese born");
	}

}
