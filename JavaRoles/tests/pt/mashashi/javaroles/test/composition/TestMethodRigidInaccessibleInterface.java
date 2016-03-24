package pt.mashashi.javaroles.test.composition;

import static org.junit.Assert.*;

import pt.mashashi.javaroles.annotations.ObjRole;
import pt.mashashi.javaroles.impl.composition.RoleRegisterComposition;
import pt.mashashi.javaroles.register.RoleRegisterAssembler;

public class TestMethodRigidInaccessibleInterface {
	
	private interface Human{ 
		String hello(); 
		String goodbye(); 
		String walk();
		String cry();
	}
	private interface Monkey{ 
		String hello(); 
		String goodbye(); 
		String walk();
		String cry();
	}
	
	private static class Portuguese implements Human{
		@Override
		public String hello() { return "Hello buddy"; }

		@Override
		public String goodbye() { return "Goodbye buddy"; }

		@Override
		public String walk() { return "ChepChepChep"; }
		
		@Override
		public String cry() { return "Ai jasus..."; }
		
	}
	private static class Bonobo implements Monkey{
		@Override public String hello() { return "Ugauga"; }
		
		@Override
		public String goodbye() { return "Uhuhuhuhh"; }

		@Override
		public String walk() { return "ChapChapChap"; }
		
		@Override
		public String cry() { return "Ohhohohoh"; }
	}
	
	private static class AnimalRoles implements Human, Monkey{
		
		@ObjRole public Human human;
		@ObjRole public Monkey monkey;
		
		public AnimalRoles(){
			human = new Portuguese(); 
			monkey = new Bonobo();
		}
		@Override
		public String hello() { return "Default hello "+this.getClass().getName(); }
		@Override
		public String goodbye() { return "Default goodbye "+this.getClass().getName(); }
		@Override
		public String walk() { return "Default walk "+this.getClass().getName(); }
		@Override
		public String cry() { return "Default cry "+this.getClass().getName(); }
	}
	
	private static class AnimalRolesExtra extends AnimalRoles {
		
		@Override
		public String hello() {
			return "nop";
		}
		
		@Override
		public String goodbye() {
			String result = super.goodbye();
			result += " extended";
			return result;
		}
		
	}
	
	
	public static void test(){
		
		/*try{*/
		new RoleRegisterAssembler(new RoleRegisterComposition())
			.includeGiven(TestMethodRigidInaccessibleInterface.class)
			.get()
			.registerRoles();
		AnimalRoles a = new AnimalRoles();
		assertEquals("Hello buddy", a.hello());
		AnimalRolesExtra b = new AnimalRolesExtra();
		assertEquals("nop", b.hello());
		/*	//fail("The reported error is not happening any more.");
		}catch(RuntimeException e){
			fail("the error is happens again");
			assertTrue(e.getMessage().contains("cannot access its superinterface "+TestMethodRigidInaccessibleInterface.Human.class.getName()));
		}*/
		
	}
	
}
