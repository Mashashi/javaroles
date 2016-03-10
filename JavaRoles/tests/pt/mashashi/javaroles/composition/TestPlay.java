package pt.mashashi.javaroles.composition;

import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import static org.junit.Assert.*;

import pt.mashashi.javaroles.LoggerTarget;
import pt.mashashi.javaroles.annotations.InjObjRigid;
import pt.mashashi.javaroles.annotations.ObjRole;
import pt.mashashi.javaroles.annotations.Rigid;
import pt.mashashi.javaroles.impl.composition.RoleRegisterComposition;

public class TestPlay {
	public interface Human{ String hello(); }
	public interface Monkey{ String hello(); }
	
	public static class Portuguese implements Human{
		
		@InjObjRigid public AnimalRoles animalRoles;
		
		@Override
		public String hello() { return "Hello buddy"; }
	}
	public static class Bonobo implements Monkey{
		@Override public String hello() { return "Ugauga"; } 
	}
	
	public interface AnimalRoles{}
	
	public static class AnimalRoles1 implements AnimalRoles, Human, Monkey{
		
		@ObjRole public Human human;
		@ObjRole public Monkey monkey;
		
		@Rigid
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
		
		@Rigid
		public void set(){}
		
	}
	
	@Rigid
	public static class AnimalRoles3 implements AnimalRoles, Human, Monkey{
		
		@ObjRole public Human human;
		@ObjRole public Monkey monkey;
		
		@Rigid
		public AnimalRoles3(){
			this.human = new Portuguese();
			this.monkey = new Bonobo();
		}
		
		@Override
		public String hello() { return "Default hello "+this.getClass().getName(); }
		
	}
	
	public static void test(){
		
		RoleRegisterComposition rrc = new RoleRegisterComposition("pt.mashashi.javaroles.composition.TestPlay");
		
		Logger.getRootLogger().setLevel(Level.ALL);
		rrc.registerRools();
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
		
	}
	
}
