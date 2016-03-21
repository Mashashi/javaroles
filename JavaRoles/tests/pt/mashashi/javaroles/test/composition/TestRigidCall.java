package pt.mashashi.javaroles.test.composition;

import static org.junit.Assert.assertEquals;

import pt.mashashi.javaroles.annotations.ObjRigid;
import pt.mashashi.javaroles.annotations.ObjRole;

public class TestRigidCall {
	
	public interface Human{ String hello(); }
	public interface Monkey{ String hello(); }
	
	public static class Portuguese implements Human{
		@Override
		public String hello() {
			return "Hello buddy";
		}
	}
	public static class Bonobo implements Monkey{
		@Override public String hello() { return "Ugauga"; } 
	}
	public static class AnimalRoles implements Human, Monkey{
		@ObjRole public Human human;
		@ObjRole public Monkey monkey;
		
		@ObjRigid public Monkey rigidMonkey1;
		@ObjRigid public Monkey rigidMonkey2;
		@ObjRigid public Human rigidHuman;
		
		public AnimalRoles(){
			this.human = new Portuguese();
			this.monkey = new Bonobo();
		}
		
		@Override
		public String hello() { return "Default hello "+this.getClass().getName(); }
	}
	
	public static void test(){
		AnimalRoles a = new AnimalRoles();
		assertEquals("Yap", "Default hello "+AnimalRoles.class.getName(), a.rigidMonkey1.hello());
		assertEquals("Yap", "Default hello "+AnimalRoles.class.getName(), a.rigidMonkey2.hello());
		assertEquals("Yap", "Default hello "+AnimalRoles.class.getName(), a.rigidHuman.hello());
		assertEquals("Yap", "Hello buddy", a.hello());
	}
	
}
