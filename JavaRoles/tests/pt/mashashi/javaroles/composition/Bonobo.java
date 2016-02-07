package pt.mashashi.javaroles.composition;

import pt.mashashi.javaroles.Monkey;

/**
 * 
 * @author Rafael
 *
 */
public class Bonobo implements Monkey{

	public static final String HALLO = "Ugauga";
	public static final String EAT = "Nhamnham";
	
	public Bonobo() {}
	
	@Override
	public String hello(){
		return "Ugauga";
	}

	@Override
	public String eat() {
		return EAT;
	}

}
