package pt.mashashi.javaroles.composition;

import static org.junit.Assert.assertEquals;

import pt.mashashi.javaroles.ObjectForRole;

public class TestMethodRoleObjectMethodCall {
	
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
		@ObjectForRole public Human human;
		@ObjectForRole public Monkey monkey;
		public AnimalRoles(){
			human = new Portuguese(); monkey = new Bonobo();
		}
		@Override
		public String hello() { return "Default hello "+this.getClass().getName(); }
	}
	static class AnimalRolesSwaped implements Human, Monkey{
		@ObjectForRole public Monkey monkey;
		@ObjectForRole public Human human;
		public AnimalRolesSwaped(){
			human = new Portuguese(); monkey = new Bonobo(); 
		}
		@Override 
		public String hello() { return "Default hello "+this.getClass().getName(); }
	}
	
	
	public static void test(){
		
		AnimalRoles a = new AnimalRoles();
		assertEquals("Yap", "Hello buddy", a.hello());
		
		AnimalRolesSwaped b = new AnimalRolesSwaped();
		assertEquals("Yap", "Ugauga", b.hello());
		
	}
	
}
