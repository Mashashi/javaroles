package pt.mashashi.javaroles.composition;

import static org.junit.Assert.assertEquals;

import pt.mashashi.javaroles.ObjectForRole;

public class TestCallWrongStackOverflow {
	
	public interface Human{ String hello(); }
	public interface Monkey{ String hello(); }
	
	public static class Portuguese implements Human{
		@Override
		public String hello() { return "Hello buddy"; }
	}
	public static class Bonobo implements Monkey{
		@Override public String hello() { return "Ugauga"; } 
	}
	public static class AnimalRoles implements Human, Monkey{
		@ObjectForRole public Human human;
		@ObjectForRole public Monkey monkey;
		public AnimalRoles(){
			human = new Portuguese();
			monkey = new Bonobo();
		}
		@TurnOffRole
		@Override
		public String hello() { return "Default hello "+this.getClass().getName(); }
	}
	public static void test(){
		AnimalRoles a = new AnimalRoles();
		assertEquals("Yap", "Default hello "+AnimalRoles.class.getName(), a.hello());
	}
	
}
