package pt.mashashi.javaroles.composition;

import static org.junit.Assert.assertEquals;

import pt.mashashi.javaroles.annotations.ObjRole;

public class TestWithNotAccessibleClasses {
	
	interface Human{ String hello(); }
	interface Monkey{ String hello(); }
	
	static class Portuguese implements Human{
		@Override
		public String hello() {
			return "Hello buddy";
		}
	}
	static class Bonobo implements Monkey{
		@Override public String hello() { return "Ugauga"; } 
	}
	static class AnimalRoles implements Human, Monkey{
		@ObjRole public Human human;
		@ObjRole public Monkey monkey;
		public AnimalRoles(){}
		@Override
		public String hello() { return "Default hello "+this.getClass().getName(); }
	}
	
	public static void test() throws IllegalAccessException{
		AnimalRoles a = new AnimalRoles();
		assertEquals("Yap", "Default hello "+AnimalRoles.class.getName(), a.hello());
	}
	
}
