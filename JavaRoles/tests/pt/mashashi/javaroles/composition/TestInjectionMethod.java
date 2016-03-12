package pt.mashashi.javaroles.composition;

import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import pt.mashashi.javaroles.annotations.InjObjRigid;
import pt.mashashi.javaroles.annotations.ObjRole;
import pt.mashashi.javaroles.annotations.Player;
import pt.mashashi.javaroles.composition.TestPlay.Human;
import pt.mashashi.javaroles.composition.TestPlay.Monkey;

import static org.junit.Assert.*;



public class TestInjectionMethod {
	
	public static class Portuguese implements Human{
		
		@InjObjRigid public AnimalRoles animalRoles;
		
		@Override
		public String hello() { return "Hello buddy"; }
	}
	public static class Bonobo implements Monkey{
		
		@InjObjRigid public AnimalRoles animalRoles;
		
		public void callBack(AnimalRoles animalRoles){}
		
		@Override 
		public String hello() { return "Ugauga"; } 
	}
	
	public interface AnimalRoles{}
	
	public static class AnimalRolesClass implements AnimalRoles, Human, Monkey{
		
		@ObjRole public Human human;
		@ObjRole public Monkey monkey;
		
		@Player
		public AnimalRolesClass(){
			this.human = new Portuguese();
			this.monkey = new Bonobo();
		}
		
		@Override
		public String hello() { return "Default hello "+this.getClass().getName(); }
		
	}
	
	public static void test(){
		
		//fail("Just do it");
		
	}
	
}
