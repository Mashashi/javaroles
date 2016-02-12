package pt.mashashi.javaroles.composition;

import static org.junit.Assert.assertEquals;

import pt.mashashi.javaroles.ObjectForRole;

public class TestOriginalRigidTurnOffRoleMethod {
	
	public interface Human{ 
		String hello(); 
		//String die(); 
	}
	public interface Monkey{ 
		String hello(); 
		//String die();
	}
	
	public static class Portuguese implements Human{
		@Override
		public String hello() { return "Hello buddy"; }
		//@Override public String die() {return null;}
	}
	public static class Bonobo implements Monkey{
		@Override public String hello() { return "Ugauga"; } 
		//@Override public String die() {return null;}
	}
	public static class AnimalRoles implements Human, Monkey{
		@ObjectForRole public Human human;
		@ObjectForRole public Monkey monkey;
		@OriginalRigid public Human original;
		public AnimalRoles(){}
		@Override
		@TurnOffRole
		public String hello() { return "Default hello "+this.getClass().getName(); }
		public String notInRole(){ return "Not in role";}
		//@Override public String die() {return null;}
	}
	public static void test(){
		AnimalRoles a = new AnimalRoles();
		assertEquals("Yap",  "Default hello "+ AnimalRoles.class.getName(), a.hello());
		assertEquals("Yap",  "Default hello "+ AnimalRoles.class.getName(), a.original.hello());
	}
	
}