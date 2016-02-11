package pt.mashashi.javaroles.typed.rolemethod;

import pt.mashashi.javaroles.Human;
import pt.mashashi.javaroles.Monkey;
import pt.mashashi.javaroles.ObjectForRole;
import pt.mashashi.javaroles.composition.OriginalRigid;
import pt.mashashi.javaroles.composition.TurnOffRole;
import pt.mashashi.javaroles.typed.TurnOnRole;

/**
 * 
 * @author Rafael
 *
 */
public class AnimalRoles implements Human, Monkey{
	
	public static final String HALLO = "Default hallo";
	public static final String DIE = "Default they kill me...";
	public static final String EAT = "Default eat...";
	
	@ObjectForRole public Human human;
	
	@ObjectForRole public Monkey monkey;
	
	public AnimalRoles(){
		human = new Portuguese();
		monkey = new Bonobo();
	}
	
	@Override
	@TurnOnRole
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
	
}

