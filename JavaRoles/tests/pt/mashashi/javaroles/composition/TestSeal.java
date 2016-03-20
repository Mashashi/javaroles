package pt.mashashi.javaroles.composition;

import pt.mashashi.javaroles.annotations.sprinkles.Seal;
import pt.mashashi.javaroles.impl.composition.RoleRegisterComposition;
import pt.mashashi.javaroles.register.RoleRegisterAssembler;

import static org.junit.Assert.*;



public class TestSeal {
	
	public static class Human{
		
		@Seal public boolean seal;
		
		public Human() {
			seal = false;
		}
		
		public String hello(){
			return "hey there";
		}
		
	}
	
	public static class Student extends Human{
		
		public String study(){
			return "I am studying";
		}
		
	}

	public static void test(){
		
		new RoleRegisterAssembler(new RoleRegisterComposition())
				.includeGiven(TestSeal.class)
				.sealClasses()
				//.inheritAnnots()
				//.callSuperAnnots()
				.get()
				.registerRoles();
		
		Human h = new Human();
		assertEquals("hey there",h.hello());
		h.seal = true;
		try{
			System.out.println(h.hello());
			fail("Exception should be thrown");
		}catch(RuntimeException e){
			assertEquals("This class is sealed",e.getMessage());
		}
		
		Student s = new Student();
		assertEquals("hey there",s.hello());
		s.seal = true;
		try{
			System.out.println(s.hello());
			fail("Exception should be thrown");
		}catch(RuntimeException e){
			assertEquals("This class is sealed",e.getMessage());
		}
		try{
			System.out.println(s.study());
			fail("Exception should be thrown");
		}catch(RuntimeException e){
			assertEquals("This class is sealed",e.getMessage());
		}
		
	}
	
}
