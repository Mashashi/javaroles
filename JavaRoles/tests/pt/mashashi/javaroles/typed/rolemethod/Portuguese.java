package pt.mashashi.javaroles.typed.rolemethod;

import pt.mashashi.javaroles.Human;

/**
 * 
 * @author Rafael
 *
 */
public class Portuguese implements Human{
	
	public static final String HALLO = "Ol� mo�o";
	public static final String DIE = "Ai matarem-me...";
	public static final String EAT = "Estou cheio dred";
	
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

}
