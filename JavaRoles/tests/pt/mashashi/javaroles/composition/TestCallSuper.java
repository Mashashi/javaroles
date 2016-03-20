package pt.mashashi.javaroles.composition;

import pt.mashashi.javaroles.annotations.InjObjRigid;
import pt.mashashi.javaroles.annotations.InjObjRigidPos;
import pt.mashashi.javaroles.annotations.ObjRole;
import pt.mashashi.javaroles.annotations.Player;
import pt.mashashi.javaroles.annotations.sprinkles.CallSuper;
import pt.mashashi.javaroles.annotations.sprinkles.InheritAnnots;
import pt.mashashi.javaroles.impl.composition.RoleRegisterComposition;
import pt.mashashi.javaroles.register.RoleRegisterAssembler;

import static org.junit.Assert.*;



public class TestCallSuper {
	
	public interface Human{ String hello(); }
	public interface Monkey{ String hello(); }
	
	//public interface AnimalRoles{}
	
	public static class Portuguese implements Human{
		
		@InjObjRigid public AnimalRoles animalRoles;
		
		public String flag = "";
		
		@InheritAnnots
		@CallSuper
		@InjObjRigidPos
		public void callback(AnimalRoles animalRoles){
			assertEquals(this.animalRoles, animalRoles);
			flag += "set";
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
			this.human = new Talense();
			this.monkey = new Bonobo();
		}
		
		@Override
		public String hello() { return "Default hello "+this.getClass().getName(); }
		
	}
	
	public static class Lisboeta extends Portuguese{
		
		@Override
		public void callback(AnimalRoles animalRoles) {
			//super.callback(animalRoles);
			super.flag+="extended1";
		}
		
	}
	
	public static class Sacavenense extends Lisboeta{
		
		@Override
		public void callback(AnimalRoles animalRoles) {
			//super.callback(animalRoles);
			super.flag+="extended2";
		}
		
	}
	
	public static class Palmense extends Sacavenense{
		
		@Override
		public void callback(AnimalRoles animalRoles) {
			//super.callback(animalRoles);
			super.flag+="extended3";
		}
		
	}
	
	public static class Talense extends Palmense{
		
		@Override
		public void callback(AnimalRoles animalRoles) {
			//super.callback(animalRoles);
			super.flag+="extended4";
		}
		
	}
	
	public static class Gajo extends Talense{
		
	}

	public static void test(){
		
		new RoleRegisterAssembler(new RoleRegisterComposition())
				.includeGiven(TestCallSuper.class)
				.inheritAnnots()
				.callSuperAnnots()
				.get()
				.registerRoles();
		
		AnimalRoles animalroles = new AnimalRoles();
		assertEquals("setextended1extended2extended3extended4",((Portuguese)animalroles.human).flag);
		
	}
	
}
