package pt.mashashi.javaroles.test.composition;

import static org.junit.Assert.assertEquals;

import pt.mashashi.javaroles.annotations.ObjRole;

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
		@ObjRole private Human human;
		@ObjRole public Monkey monkey;
		public AnimalRoles(Human human, Monkey monkey){
			this.human = human; 
			this.monkey = monkey;
		}
		@Override
		public String hello() { return "Default hello "+this.getClass().getName(); }
	}
	static class AnimalRolesSwaped implements Human, Monkey{
		@ObjRole public Monkey monkey;
		@ObjRole public Human human;
		public AnimalRolesSwaped(){
			human = new Portuguese(); 
			monkey = new Bonobo(); 
		}
		@Override 
		public String hello() { return "Default hello "+this.getClass().getName(); }
	}
	
	
	public static void test(){
		
		AnimalRoles a = new AnimalRoles(new Portuguese(), null);
		assertEquals("Yap", "Hello buddy", a.hello());
		
		AnimalRoles b = new AnimalRoles(null, new Bonobo());
		assertEquals("Yap", "Ugauga", b.hello());
		
		AnimalRolesSwaped c = new AnimalRolesSwaped();
		assertEquals("Yap", "Ugauga", c.hello());
		
	}
	
}
