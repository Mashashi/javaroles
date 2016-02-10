package pt.mashashi.javaroles.composition;

import pt.mashashi.javaroles.Human;
import pt.mashashi.javaroles.Monkey;
import pt.mashashi.javaroles.ObjectForRole;
import pt.mashashi.javaroles.TurnOffRole;
import pt.mashashi.javaroles.typed.rolemethod.Bonobo;
import pt.mashashi.javaroles.typed.rolemethod.Portuguese;

/**
 * 
 * @author Rafael
 *
 */
public class AnimalRolesSwaped implements Human, Monkey{
	
	public static final String HALLO = "Default hallo";
	public static final String DIE = "Default they kill me...";
	public static final String EAT = "Default eat...";
	
	@ObjectForRole public Monkey monkey;
	
	@ObjectForRole public Human human;
	
	public AnimalRolesSwaped(){
		human = new Portuguese();
		monkey = new Bonobo();
	}
	
	@Override
	public String hello() {
		return HALLO;
	}
	
	@Override
	public String die(String age) {
		return DIE+age;
	}

	@Override
	@TurnOffRole
	public String eat() {
		return EAT;
	}

	@Override
	public String dance() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String stuffing(String str, Object[] input) {
		// TODO Auto-generated method stub
		return null;
	}
	
}