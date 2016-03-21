package pt.mashashi.javaroles.composition;

import static org.junit.Assert.assertEquals;

import pt.mashashi.javaroles.annotations.MissUseAnnotationExceptionException;
import pt.mashashi.javaroles.annotations.ObjRole;
import pt.mashashi.javaroles.impl.composition.RoleRegisterComposition;
import pt.mashashi.javaroles.register.RoleRegisterAssembler;

public class TestMethodRoleObjectMethodCallNotInter {
	
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
	static class AnimalRoles {
		
		@ObjRole public Human human;
		@ObjRole public Monkey monkey;
		
		public AnimalRoles(){
			human = new Portuguese(); monkey = new Bonobo();
		}
		public String hello() { return "Default hello "+this.getClass().getName(); }
	}
	
	
	
	public static void test(){
		
		try{
			new RoleRegisterAssembler(new RoleRegisterComposition())
				.includeGiven(TestMethodRoleObjectMethodCallNotInter.class)
				.get()
				.registerRoles();
		}catch(MissUseAnnotationExceptionException e){
			assertEquals("The annotation @ObjRole was used incorrectly.\n The class \"pt.mashashi.javaroles.composition.TestMethodRoleObjectMethodCallNotInter$AnimalRoles\" has to implement the interface \"pt.mashashi.javaroles.composition.TestMethodRoleObjectMethodCallNotInter$Human\" of the field \"human\"", e.getMessage());
		}
		
		AnimalRoles a = new AnimalRoles();
		assertEquals("Yap", "Default hello "+TestMethodRoleObjectMethodCallNotInter.AnimalRoles.class.getName(), a.hello());
		
	}
	
}
