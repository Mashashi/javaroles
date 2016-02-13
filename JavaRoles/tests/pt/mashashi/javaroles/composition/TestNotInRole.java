package pt.mashashi.javaroles.composition;

import static org.junit.Assert.assertEquals;

import pt.mashashi.javaroles.ObjRole;

public class TestNotInRole {
	
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
		@ObjRole public Human human;
		@ObjRole public Monkey monkey;
		public AnimalRoles(){}
		@Override
		public String hello() { return "Default hello "+this.getClass().getName(); }
		public String notInRole(){ return "Not in role";}
	}
	public static void test(){
		AnimalRoles a = new AnimalRoles();
		assertEquals("Yap", "Not in role", a.notInRole());
	}
	
}
