package pt.mashashi.javaroles.composition;

import pt.mashashi.javaroles.annotations.InjObjRigid;
import pt.mashashi.javaroles.annotations.InjObjRigidPos;
import pt.mashashi.javaroles.annotations.ObjRole;
import pt.mashashi.javaroles.annotations.Play;
import pt.mashashi.javaroles.annotations.Play.Place;
import pt.mashashi.javaroles.annotations.Player;
import pt.mashashi.javaroles.annotations.sprinkles.InheritAnnots;
import pt.mashashi.javaroles.composition.TestInjectionMethod.AnimalRoles;
import pt.mashashi.javaroles.composition.TestInjectionMethod.Portuguese;
import pt.mashashi.javaroles.composition.TestPlay.Human;
import pt.mashashi.javaroles.composition.TestPlay.Monkey;

import static org.junit.Assert.*;



public class TestInheritAnnotation {
	
	public static class Portuguese implements Human{
		
		@InjObjRigid public AnimalRoles animalRoles;
		
		public String flag = null;
		
		@InheritAnnots
		@InjObjRigidPos
		public void callback(AnimalRoles animalRoles){
			flag = "set";
		}
		
		@Override public String hello() { return "Hello buddy"; }
		
	}
	
	public static class Bonobo implements Monkey{
		
		@InjObjRigid public AnimalRoles animalRoles;
		
		
		@InjObjRigidPos
		public void callBack(AnimalRoles animalRoles){}
		
		@Override  public String hello() { return "Ugauga"; } 
		
	}
	
	public interface AnimalRoles{}
	
	public static class AnimalRoles1 implements AnimalRoles, Human, Monkey{
		
		@ObjRole public Human human;
		@ObjRole public Monkey monkey;
		
		@Player
		public AnimalRoles1(){
			this.human = new Portuguese();
			this.monkey = new Bonobo();
		}
		
		@Override
		public String hello() { return "Default hello "+this.getClass().getName(); }
		
	}
	
	public static class Lisboeta extends Portuguese{
		
		@Override
		public void callback(AnimalRoles animalRoles) {
			super.callback(animalRoles);
			super.flag+="extended";
		}
		
	}

	public static void test(){
		
		AnimalRoles1 a = new AnimalRoles1();
		assertEquals("set",((Portuguese)a.human).flag);
		
		
		
		
	}
	
}
