package pt.mashashi.javaroles.test.composition;

import pt.mashashi.javaroles.annotations.sprinkles.Seal;
import pt.mashashi.javaroles.impl.composition.RoleRegisterComposition;
import pt.mashashi.javaroles.register.RoleRegisterAssembler;

import static org.junit.Assert.*;



public class TestSeal {
	
	public static class Human{
		
		private final static String TIME_OUT_SEALED = "A timeout caused this class to get sealed";
		
		@Seal public boolean seal;
		@Seal(msgSeal=Human.TIME_OUT_SEALED) public boolean sealDueToTimeOut;
		
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
				//.inheritAnnots()
				//.callSuperAnnots()
				.sealClasses()
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
		
		h.sealDueToTimeOut = true;
		try{
			System.out.println(h.hello());
			fail("Exception should be thrown");
		}catch(RuntimeException e){
			assertEquals(Human.TIME_OUT_SEALED, e.getMessage());
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
