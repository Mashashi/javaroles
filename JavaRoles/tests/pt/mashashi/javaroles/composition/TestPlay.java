package pt.mashashi.javaroles.composition;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import static org.junit.Assert.*;

import java.util.List;

import pt.mashashi.javaroles.annotations.InjObjRigid;
import pt.mashashi.javaroles.annotations.ObjRole;
import pt.mashashi.javaroles.annotations.Play;
import pt.mashashi.javaroles.annotations.Play.Place;
import pt.mashashi.javaroles.annotations.Player;
import pt.mashashi.javaroles.impl.composition.RoleRegisterComposition;
import pt.mashashi.javaroles.logging.LoggerTarget;
import pt.mashashi.javaroles.register.RoleRegister;

public class TestPlay {
	public interface Human{ String hello(); }
	public interface Monkey{ String hello(); }
	
	public static class Portuguese implements Human{
		
		@InjObjRigid public AnimalRoles animalRoles;
		
		@Override
		public String hello() { return "Hello buddy"; }
	}
	public static class Bonobo implements Monkey{
		
		@InjObjRigid public AnimalRoles animalRoles;
		
		@Override 
		public String hello() { return "Ugauga"; } 
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
	
	public static class AnimalRoles2 implements AnimalRoles, Human, Monkey{
		
		@ObjRole public Human human;
		@ObjRole public Monkey monkey;
		
		
		public AnimalRoles2(){
			this.human = new Portuguese();
			this.monkey = new Bonobo();
		}
		
		@Override
		public String hello() { return "Default hello "+this.getClass().getName(); }
		
		@Play
		public void set(){}
		
	}
	
	@Player
	public static class AnimalRoles3 implements AnimalRoles, Human, Monkey{
		
		@ObjRole public Human human;
		@ObjRole public Monkey monkey;
		
		@Player
		public AnimalRoles3(){
			this.human = new Portuguese();
			this.monkey = new Bonobo();
		}
		
		@Override
		public String hello() { return "Default hello "+this.getClass().getName(); }
		
	}
	
	public static class AnimalRoles4 implements AnimalRoles, Human, Monkey{
		
		@ObjRole public Human human;
		@ObjRole public Monkey monkey;
		
		public AnimalRoles4(){
			this.human = new Portuguese();
			this.monkey = new Bonobo();
		}
		
		@Override
		public String hello() { return "Default hello "+this.getClass().getName(); }
		
		@Play
		private void setRigidHuman(Human human){
			// Code gets injected hear
		}
		
		public void playHuman(){
			setRigidHuman(human);
		}
		
	}
	
	public static class AnimalRoles5 implements AnimalRoles, Human, Monkey{
		
		@ObjRole public Human human;
		@ObjRole public Monkey monkey;
		
		public AnimalRoles5(){
			this.human = new Portuguese();
			this.monkey = new Bonobo();
		}
		
		@Override
		public String hello() { return "Default hello "+this.getClass().getName(); }
		
		@Play
		private void setRigidHuman(Human human){
			// Code gets injected hear
			human = this.human;
		}
		
		@Play(order=Place.BEFORE)
		private void setRigidHuman2(Human human){
			// Code gets injected hear
			human = this.human;
		}
		
		@Play(order=Place.AFTER)
		private void setRigidHuman3(Human human){
			// Code gets injected hear
			human = this.human;
		}
		
		public void playHuman(){
			setRigidHuman(null);
		}
		
	}
	
	public static void test(){
		
		RoleRegister rrc = new RoleRegisterComposition()
					.includeGiven(TestPlay.class);
		
		Logger.getRootLogger().setLevel(Level.ALL);
		rrc.registerRoles();
		Logger.getRootLogger().setLevel(Level.OFF);
		
		//System.out.println(LoggerTarget.string("Test:pt.mashashi.javaroles.composition.TestPlay-TestPlay$AnimalRoles3"));
		
		AnimalRoles1 a1 = new AnimalRoles1();
		assertNotEquals(null, ((Portuguese)a1.human).animalRoles);
		
		
		AnimalRoles2 a2 = new AnimalRoles2();
		assertEquals(null, ((Portuguese)a2.human).animalRoles);
		a2.set();
		assertNotEquals(null, ((Portuguese)a2.human).animalRoles);
		
		List<String> t3r = LoggerTarget.string("Test:pt.mashashi.javaroles.composition.TestPlay-TestPlay$AnimalRoles3");
		assertEquals(1, t3r.size());
		assertEquals("-rc", t3r.get(0));
		
		
		AnimalRoles4 a4 = new AnimalRoles4();
		assertEquals(null, ((Portuguese)a4.human).animalRoles);
		a4.playHuman();
		assertNotEquals(null, ((Portuguese)a4.human).animalRoles);
		assertEquals(null, ((Bonobo)a4.monkey).animalRoles);
		
		AnimalRoles5 a5 = new AnimalRoles5();
		assertEquals(null, ((Portuguese)a5.human).animalRoles);
		a5.setRigidHuman(null);
		assertNotEquals(null, ((Portuguese)a5.human).animalRoles);
		assertEquals(null, ((Bonobo)a5.monkey).animalRoles);
		
		a5 = new AnimalRoles5();
		assertEquals(null, ((Portuguese)a5.human).animalRoles);
		a5.setRigidHuman2(null);
		assertEquals(null, ((Portuguese)a5.human).animalRoles);
		a5.setRigidHuman3(null);
		assertNotEquals(null, ((Portuguese)a5.human).animalRoles);
		
		
	}
	
}
