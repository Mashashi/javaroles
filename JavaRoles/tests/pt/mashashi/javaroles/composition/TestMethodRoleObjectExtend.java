package pt.mashashi.javaroles.composition;

import static org.junit.Assert.assertEquals;

import pt.mashashi.javaroles.annotations.ObjRigid;
import pt.mashashi.javaroles.annotations.ObjRole;
import pt.mashashi.javaroles.impl.composition.RoleRegisterComposition;
import pt.mashashi.javaroles.register.RoleRegisterAssembler;

public class TestMethodRoleObjectExtend {
	
	public interface Human{ 
		String hello(); 
		String goodbye(); 
		String walk();
		String cry();
	}
	public interface Monkey{ 
		String hello(); 
		String goodbye(); 
		String walk();
		String cry();
	}
	
	static class Portuguese implements Human{
		@Override
		public String hello() { return "Hello buddy"; }

		@Override
		public String goodbye() { return "Goodbye buddy"; }

		@Override
		public String walk() { return "ChepChepChep"; }
		
		@Override
		public String cry() { return "Ai jasus..."; }
		
	}
	static class Bonobo implements Monkey{
		@Override public String hello() { return "Ugauga"; }
		
		@Override
		public String goodbye() { return "Uhuhuhuhh"; }

		@Override
		public String walk() { return "ChapChapChap"; }
		
		@Override
		public String cry() { return "Ohhohohoh"; }
	}
	
	static class AnimalRoles implements Human, Monkey{
		
		@ObjRigid public Human rigid;
		@ObjRole public Human human;
		@ObjRole public Monkey monkey;
		
		public AnimalRoles(){
			human = new Portuguese(); monkey = new Bonobo();
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
	
	public static class AnimalRolesExtra extends AnimalRoles {
		
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
		
		@Override
		public String cry() {
			return super.rigid.cry();
		}
	}
	
	
	public static void test(){
			
		new RoleRegisterAssembler(new RoleRegisterComposition())
			.includeGiven(TestMethodRoleObjectExtend.class)
			.get()
			.registerRoles();
			
		AnimalRolesExtra a = new AnimalRolesExtra();
		//"Default hello "+TestMethodRoleObjectExtend.AnimalRoles.class.getName()
		assertEquals("nop", a.hello());
		assertEquals("Goodbye buddy extended", a.goodbye());
		assertEquals("ChepChepChep", a.walk());
		assertEquals("Default cry "+TestMethodRoleObjectExtend.AnimalRoles.class.getName(), a.cry());
	}
	
}
