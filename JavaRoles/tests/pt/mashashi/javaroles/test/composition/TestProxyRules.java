package pt.mashashi.javaroles.test.composition;

import static org.junit.Assert.assertEquals;

import pt.mashashi.javaroles.annotations.ObjRole;
import pt.mashashi.javaroles.annotations.ProxyRules;

public class TestProxyRules {
	
	public interface Human{ String hello(); }
	public interface Monkey{ String hello(); }
	
	static class Portuguese implements Human{
		@Override
		public String hello() { return "Hello buddy"; }
	}
	static class Bonobo implements Monkey{
		@Override public String hello() { return "Ugauga"; } 
	}
	
	
	public static Boolean rolesActive(AnimalRoles a){
		return a.rolesActive;
	}
	
	@ProxyRules({TestProxyRules.class})
	static class AnimalRoles implements Human, Monkey{
		public boolean rolesActive;
		@ObjRole public Human human;
		@ObjRole public Monkey monkey;
		public AnimalRoles(){
			human = new Portuguese(); 
			monkey = new Bonobo();
			rolesActive = false;
		}
		@Override
		public String hello() { return "Default hello "+this.getClass().getName(); }
	}
	
	
	public static void test(){
		
		AnimalRoles a = new AnimalRoles();
		a.rolesActive=true;
		assertEquals("Yap", "Hello buddy", a.hello());
		a.rolesActive=false;
		assertEquals("Yap",  "Default hello "+AnimalRoles.class.getName(), a.hello());
		
	}
	
}
