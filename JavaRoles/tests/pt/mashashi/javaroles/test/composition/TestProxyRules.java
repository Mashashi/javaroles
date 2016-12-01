package pt.mashashi.javaroles.test.composition;

import static org.junit.Assert.*;

import pt.mashashi.javaroles.annotations.ObjRole;
import pt.mashashi.javaroles.annotations.ProxyRules;
import pt.mashashi.javaroles.impl.composition.RoleRegisterComposition;
import pt.mashashi.javaroles.register.RoleRegisterAssembler;

public class TestProxyRules {
	
	public interface Human{ String hello(); }
	public interface Monkey{ String hello(); }
	
	public static class Portuguese implements Human{
		@Override
		public String hello() { return "Hello buddy"; }
	}
	public static class Bonobo implements Monkey{
		@Override public String hello() { return "Ugauga"; } 
	}
	
	public static class InnerTestProxyRules{
		public static Boolean rolesActive(AnimalRoles a, Monkey o){
			return a.rolesActiveMonkey;
		}
		public static Boolean rolesActive(AnimalRoles a){
			return a.rolesActive;
		}
	}
	
	@ProxyRules({InnerTestProxyRules.class})
	public static class AnimalRoles implements Human, Monkey{
		public boolean rolesActive;
		public boolean rolesActiveMonkey;
		@ObjRole public Human human;
		@ObjRole public Monkey monkey;
		public AnimalRoles(){
			human = new Portuguese(); 
			monkey = new Bonobo();
		}
		@Override
		public String hello() { return "Default hello "+this.getClass().getName(); }
	}
	
	@ProxyRules({})
	public static class AnimalRoles2 implements Human, Monkey{
		@ObjRole public Human human;
		@ObjRole public Monkey monkey;
		public AnimalRoles2(){
			human = new Portuguese(); 
			monkey = new Bonobo();
		}
		@Override
		public String hello() { return "Default hello "+this.getClass().getName(); }
	}
	
	public static void test(){
	
		//if(Test.doTest(Test.Type.AGGRESSIVE)){}
		/*new RoleRegisterAssembler(new RoleRegisterComposition())
				.includeGiven(TestProxyRules.class)
				.get()
				.registerRoles();*/
		AnimalRoles a = new AnimalRoles();
		a.rolesActive = true;
		a.rolesActiveMonkey = true;
		assertEquals("Hello buddy", a.hello());
		a.rolesActive = false;
		assertEquals("Ugauga", a.hello());
		a.rolesActiveMonkey = false;
		assertEquals("Default hello pt.mashashi.javaroles.test.composition.TestProxyRules$AnimalRoles", a.hello());
		
		assertEquals("Default hello pt.mashashi.javaroles.test.composition.TestProxyRules$AnimalRoles2", new AnimalRoles2().hello());
		
	}
	
}
