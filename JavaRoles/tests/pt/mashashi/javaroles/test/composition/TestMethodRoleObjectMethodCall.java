package pt.mashashi.javaroles.test.composition;

import static org.junit.Assert.assertEquals;

import pt.mashashi.javaroles.annotations.ObjRole;

public class TestMethodRoleObjectMethodCall {
	
	interface Human{ String hello(String[] a); }
	interface Monkey{ String hello(String[] a); }
	
	static class Portuguese implements Human{
		@Override
		public String hello(String[] a) {
			return "Hello buddy";
		}
	}
	static class Bonobo implements Monkey{
		@Override public String hello(String[] a) { return "Ugauga"; } 
	}
	static class AnimalRoles implements Human, Monkey{
		@ObjRole private Human human; // Human
		@ObjRole private Monkey monkey; // Monkey
		public AnimalRoles(Portuguese human, Bonobo monkey){
			this.human = human; 
			this.monkey = monkey;
		}
		@Override
		public String hello(String[] a) { return "Default hello "+this.getClass().getName(); }
	}
	static class AnimalRolesSwaped implements Human, Monkey{
		@ObjRole public Monkey monkey;
		@ObjRole public Human human;
		public AnimalRolesSwaped(){
			human = new Portuguese(); 
			monkey = new Bonobo(); 
		}
		@Override 
		public String hello(String[] a) { return "Default hello "+this.getClass().getName(); }
	}
	
	
	public static void test(){
		
		AnimalRoles a = new AnimalRoles(new Portuguese(), null);
		assertEquals("Yap", "Hello buddy", a.hello(null));
		
		AnimalRoles b = new AnimalRoles(null, new Bonobo());
		assertEquals("Yap", "Ugauga", b.hello(null));
		
		AnimalRolesSwaped c = new AnimalRolesSwaped();
		assertEquals("Yap", "Ugauga", c.hello(null));
		
	}
	
}
