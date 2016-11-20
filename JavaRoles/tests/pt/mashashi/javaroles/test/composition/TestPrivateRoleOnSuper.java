package pt.mashashi.javaroles.test.composition;

import pt.mashashi.javaroles.annotations.ObjRole;
import static org.junit.Assert.assertEquals;

public class TestPrivateRoleOnSuper {
	
	interface Human{ String hello(String[] a); String hello2(); }
	interface Monkey{ String hello(String[] a); }
	
	static class Portuguese implements Human{
		@Override
		public String hello(String[] a) {
			return "Hello buddy";
		}

		@Override
		public String hello2() {
			return "Hey man";
		}
	}
	static class Bonobo implements Monkey{
		@Override public String hello(String[] a) { return "Ugauga"; } 
	}
	
	static class AnimalRolesSwaped implements Human, Monkey{
		@ObjRole private Monkey monkey;
		@ObjRole private Human human;
		public AnimalRolesSwaped(){
			human = new Portuguese(); 
			monkey = new Bonobo(); 
		}
		@Override
		public String hello(String[] a) { return "Default hello "+this.getClass().getName(); }
		@Override
		public String hello2() {
			return null;
		}
	}
	
	
	static class AnimalExtend extends AnimalRolesSwaped{
		
	}
	
	public static void test(){
		assertEquals("Ugauga", new AnimalExtend().hello(null));
	}
	
}
