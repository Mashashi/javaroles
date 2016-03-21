package pt.mashashi.javaroles.composition;

import static org.junit.Assert.*;

import pt.mashashi.javaroles.annotations.MissUseAnnotationExceptionException;
import pt.mashashi.javaroles.annotations.ObjRole;
import pt.mashashi.javaroles.impl.composition.RoleRegisterComposition;
import pt.mashashi.javaroles.register.RoleRegisterAssembler;

public class TestMethodRoleObjectNotInter {
	
	interface Human{ String hello(); }
	interface Monkey{ String hello(); }
	
	public static class Portuguese implements Human{
		@Override
		public String hello() { return "Hello buddy"; }
	}
	public static class Bonobo implements Monkey{
		@Override public String hello() { return "Ugauga"; } 
	}
	
	public static class AnimalRoles implements Human, Monkey{
		
		@ObjRole public Portuguese human;
		@ObjRole public Monkey monkey;
		
		public AnimalRoles(){
			human = new Portuguese(); monkey = new Bonobo();
		}
		public String hello() { return "Default hello "+this.getClass().getName(); }
	}
	
	
	
	public static void test(){
		try{
			new RoleRegisterAssembler(new RoleRegisterComposition())
			.includeGiven(TestMethodRoleObjectNotInter.class)
			.get()
			.registerRoles();
			fail("Exception should be thrown");
		}catch(MissUseAnnotationExceptionException e){
			assertEquals("The annotation @"+ObjRole.class.getSimpleName()+" was used incorrectly.\n The field \""+TestMethodRoleObjectNotInter.AnimalRoles.class.getName()+".human\" has to be declared as an interface.", e.getMessage());
		}
		AnimalRoles a = new AnimalRoles();
		assertEquals("Default hello "+TestMethodRoleObjectNotInter.AnimalRoles.class.getName(), a.hello());
	
	}
	
}
