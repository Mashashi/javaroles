package pt.mashashi.javaroles.test.composition;

import static org.junit.Assert.*;

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
			fail("Exception should be thrown");
		}catch(MissUseAnnotationExceptionException e){
			assertEquals("The annotation @"+ObjRole.class.getSimpleName()+" was used incorrectly.\n The class \""+TestMethodRoleObjectMethodCallNotInter.AnimalRoles.class.getName()+"\" has to implement the interface \""+TestMethodRoleObjectMethodCallNotInter.Human.class.getName()+"\" of the field \"human\".", e.getMessage());
		}
		
		AnimalRoles a = new AnimalRoles();
		assertEquals("Default hello "+TestMethodRoleObjectMethodCallNotInter.AnimalRoles.class.getName(), a.hello());
		
	}
	
}
