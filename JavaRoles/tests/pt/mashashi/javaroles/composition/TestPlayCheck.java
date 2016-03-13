package pt.mashashi.javaroles.composition;

import static org.junit.Assert.*;

import pt.mashashi.javaroles.annotations.InjObjRigid;
import pt.mashashi.javaroles.annotations.ObjRole;
import pt.mashashi.javaroles.annotations.Player;
import pt.mashashi.javaroles.impl.composition.RoleRegisterComposition;
import pt.mashashi.javaroles.injection.InjectionStrategy;

public class TestPlayCheck {
	public interface Human{ String hello(); }
	public interface Monkey{ String hello(); }
	
	
	public static class Bonobo implements Monkey{
		@Override public String hello() { return "Ugauga"; } 
	}
	
	public interface AnimalRoles {}
	
	
	public static class Portuguese1 implements Human{
		
		@InjObjRigid(required=true) public AnimalRoles animalRoles;
		
		@Override
		public String hello() { return "Hello buddy"; }
	}
	public static class AnimalRoles1 implements AnimalRoles, Human, Monkey{
		
		@ObjRole public Human human;
		@ObjRole public Monkey monkey;
		
		public AnimalRoles1(){
			this.human = new Portuguese1();
			this.monkey = new Bonobo();
		}
		
		@Override
		public String hello() {
			return "Default hello "+this.getClass().getName(); 
		}
		
	}
	
	
	public static class Portuguese2 implements Human{
		
		@InjObjRigid public AnimalRoles animalRoles;
		
		@Override
		public String hello() { return "Hello buddy"; }
	}
	public static class AnimalRoles2 implements AnimalRoles, Human, Monkey{
		
		@ObjRole public Human human;
		@ObjRole public Monkey monkey;
		
		public AnimalRoles2(){
			this.human = new Portuguese2();
			this.monkey = new Bonobo();
		}
		
		@Override
		public String hello() {
			return "Default hello "+this.getClass().getName(); 
		}
		
	}
	
	
	
	public static class Portuguese3 implements Human{
		
		@InjObjRigid(required=true) public AnimalRoles animalRoles;
		@InjObjRigid public AnimalRoles animalRoles_2;
		@InjObjRigid(required=true) public AnimalRoles animalRoles_3;
		@InjObjRigid(required=true) public AnimalRoles31 animalRoles31;
		
		
		@Override
		public String hello() { return "Hello buddy"; }
	}
	public static class AnimalRoles3 implements AnimalRoles, Human, Monkey{
		
		@ObjRole public Human human;
		@ObjRole public Monkey monkey;
		
		@Player
		public AnimalRoles3(){
			this.human = new Portuguese3();
			this.monkey = new Bonobo();
		}
		
		@Override
		public String hello() {
			return "Default hello "+this.getClass().getName(); 
		}
		
	}

	public static class AnimalRoles31 implements AnimalRoles, Human, Monkey{
		
		@ObjRole public Human human;
		@ObjRole public Monkey monkey;
		
		public AnimalRoles31(){
			this.human = new Portuguese3();
			this.monkey = new Bonobo();
		}
		
		@Override
		public String hello() {
			return "Default hello "+this.getClass().getName(); 
		}
		
	}

	public static void test(){
		
		new RoleRegisterComposition("pt.mashashi.javaroles.composition.TestPlayCheck")
			.setRigidInjectionStrategy(InjectionStrategy.getInstanceMultiple())
			.registerRoles();
		
		//System.out.println(LoggerTarget.string("Test:pt.mashashi.javaroles.composition.TestPlay-TestPlay$AnimalRoles3"));
		
		AnimalRoles1 a1 = new AnimalRoles1();
		assertEquals(null, ((Portuguese1)a1.human).animalRoles);
		assertEquals("Ugauga", a1.hello());
		
		AnimalRoles2 a2 = new AnimalRoles2();
		assertEquals(null, ((Portuguese2)a2.human).animalRoles);
		assertEquals("Hello buddy", a2.hello());
		
		AnimalRoles3 a3 = new AnimalRoles3();
		((Portuguese3)a3.human).animalRoles31 = null;
		assertNotEquals(null, ((Portuguese3)a3.human).animalRoles);
		assertEquals("Hello buddy", a3.hello());
		
		a3 = new AnimalRoles3();
		((Portuguese3)a3.human).animalRoles31 = null;
		((Portuguese3)a3.human).animalRoles_2 = null;
		assertNotEquals(null, ((Portuguese3)a3.human).animalRoles);
		assertEquals("Hello buddy", a3.hello());
		
		a3 = new AnimalRoles3();
		((Portuguese3)a3.human).animalRoles_2 = null;
		((Portuguese3)a3.human).animalRoles_3 = null;
		((Portuguese3)a3.human).animalRoles31 = null;
		assertNotEquals(null, ((Portuguese3)a3.human).animalRoles);
		assertEquals("Ugauga", a3.hello());
		
		
	}
	
}
