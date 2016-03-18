package pt.mashashi.javaroles.composition;

import pt.mashashi.javaroles.annotations.InjObjRigid;
import pt.mashashi.javaroles.annotations.InjObjRigidPos;
import pt.mashashi.javaroles.annotations.ObjRole;
import pt.mashashi.javaroles.annotations.Player;
import pt.mashashi.javaroles.annotations.sprinkles.CallSuper;
import pt.mashashi.javaroles.annotations.sprinkles.InheritAnnots;
import pt.mashashi.javaroles.impl.composition.RoleRegisterComposition;

import static org.junit.Assert.*;



public class TestCallSuper {
	
	public interface Human{ String hello(); }
	public interface Monkey{ String hello(); }
	
	//public interface AnimalRoles{}
	
	public static class Portuguese implements Human{
		
		@InjObjRigid public AnimalRoles animalRoles;
		
		public String flag = null;
		
		@InheritAnnots
		@CallSuper
		@InjObjRigidPos
		public void callback(AnimalRoles animalRoles){
			assertEquals(this.animalRoles, animalRoles);
			flag = "set";
		}
		
		@Override public String hello() { return "Hello buddy"; }
		
	}
	
	public static class Bonobo implements Monkey{
		
		@InjObjRigid public AnimalRoles animalRoles;
		public String flag;
		
		@InheritAnnots
		@InjObjRigidPos
		public void callback(AnimalRoles animalRoles){
			if(this.animalRoles.equals(animalRoles))
				flag = "yup";
		}
		
		@Override public String hello() { return "Ugauga"; } 
		
	}
	
	public static class AnimalRoles implements Human, Monkey{
		
		@ObjRole public Human human;
		@ObjRole public Monkey monkey;
		
		@Player
		public AnimalRoles(){
			this.human = new Lisboeta();
			this.monkey = new Bonobo();
		}
		
		@Override
		public String hello() { return "Default hello "+this.getClass().getName(); }
		
	}
	
	public static class Lisboeta extends Portuguese{
		
		@Override
		public void callback(AnimalRoles animalRoles) {
			//super.callback(animalRoles);
			super.flag+="extended";
		}
		
	}

	public static void test(){
		
		new RoleRegisterComposition()
				.includeGiven(TestCallSuper.class)
				.inheritAnnots()
				.callSuperAnnots()
				.registerRoles();
		
		AnimalRoles animalroles = new AnimalRoles();
		assertEquals("setextended",((Portuguese)animalroles.human).flag);
		
	}
	
}
