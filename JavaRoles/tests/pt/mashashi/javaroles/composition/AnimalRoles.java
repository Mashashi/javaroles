package pt.mashashi.javaroles.composition;

import pt.mashashi.javaroles.Human;
import pt.mashashi.javaroles.Monkey;
import pt.mashashi.javaroles.ObjectForRole;
import pt.mashashi.javaroles.TurnOffRole;

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
	
	public AnimalRoles(Human human, Monkey monkey){
		this.human = human;
		this.monkey = monkey;
		if(this.human!=null){
			((Portuguese)this.human).core = this;
		}
		if(this.monkey!=null){
			((Bonobo)this.monkey).core = this;
		}
	}
	
	@Override
	public String hello() {
		return HALLO;
	}
	
	@Override
	@TurnOffRole
	public String die(String age) {
		return DIE+age;
	}

	@Override
	public String eat() {
		return EAT;
	}

	@Override
	public String dance() {
		return "Just dance";
	}
	
	public String notInRole(){
		return "Oh oh";
	}

	@Override
	public String stuffing(String str, Object[] input) {
		// TODO Auto-generated method stub
		return null;
	}
}