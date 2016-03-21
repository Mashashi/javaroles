package pt.mashashi.javaroles.test.composition;

import pt.mashashi.javaroles.annotations.sprinkles.NotNullParams;
import pt.mashashi.javaroles.impl.composition.RoleRegisterComposition;
import pt.mashashi.javaroles.register.RoleRegisterAssembler;

import static org.junit.Assert.*;



public class TestNotNullParams {
	
	public static class Animal{
		public Animal() {}
		public String move(String pos){
			return "Moving to "+pos;
		}
	}
	
	
	@NotNullParams
	public static class Human extends Animal{
		
		public Human(String id) {
			
		}
		
		public String hello(String name){
			return "Hello "+name;
		}
		
		public String age(int age){
			return "My age is "+age;
		}
		
	}
	
	public static class Student extends Human{
		
		public Student() {
			super("x");
		}
		
		public String study(Long howLongInYears){
			return "Studying for: "+howLongInYears;
		}
		
	}
	
	public static void test(){
		
		new RoleRegisterAssembler(new RoleRegisterComposition())
				.includeGiven(TestNotNullParams.class)
				//.inheritAnnots()
				//.callSuperAnnots()
				.notNullParams()
				//.sealClasses()
				.get()
				.registerRoles();
		
		Animal a = new Animal();
		a.move("(1,1)");
		a.move(null);
		
		Human h = null;
		try{
			h = new Human(null);
		}catch(RuntimeException e){
			assertEquals("Param number 0 on method pt.mashashi.javaroles.composition.TestNotNullParams$Human(java.lang.String) can not be null.", e.getMessage()); 
		}
		h = new Human("x");
		h.hello("Rafael");
		try{
			h.hello(null);
			fail("A runtime exception should be thrown");
		}catch(RuntimeException e){
			assertEquals("Param number 0 on method pt.mashashi.javaroles.composition.TestNotNullParams$Human.hello(java.lang.String) can not be null.", e.getMessage());
		}
		h.age(1);
		
		Student s = new Student();
		s.study(1000l);
		
		try{
			s.study(null);
			fail("A runtime exception should be thrown");
		}catch(RuntimeException e){
			assertEquals("Param number 0 on method pt.mashashi.javaroles.composition.TestNotNullParams$Student.study(java.lang.Long) can not be null.", e.getMessage());
		}
		
	}
	
}
