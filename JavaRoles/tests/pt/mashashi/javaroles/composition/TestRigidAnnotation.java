package pt.mashashi.javaroles.composition;

import static org.junit.Assert.assertEquals;

import java.util.List;

import pt.mashashi.javaroles.InjectRigidType;
import pt.mashashi.javaroles.ObjectForRole;
import pt.mashashi.javaroles.RigidType;

public class TestRigidAnnotation {
	
	public interface Human{String hello1();String hello2();String hello3();String hello4();}
	public interface Monkey{String hello1();String hello2();String hello3();String hello4();}
	
	public static class Portuguese implements Human{
		@InjectRigidType public AnimalRoles rigid1;
		@InjectRigidType public Object rigid2;
		@InjectRigidType public List rigid3;
		@InjectRigidType private AnimalRoles rigid4;
		@Override public String hello1() { return rigid1!=null?"Was setup":"Fails"; }
		@Override public String hello2() { return rigid2!=null?"Was setup":"Fails"; }
		@Override public String hello3() { return rigid3!=null?"Was setup":"Fails"; }
		@Override public String hello4() { return rigid4!=null?"Was setup":"Fails"; }
	}
	public static class Bonobo implements Monkey{
		@Override public String hello1() { return "Ugauga"; }
		@Override public String hello2() { return "Ugauga"; }
		@Override public String hello3() { return "Ugauga"; }
		@Override public String hello4() { return "Ugauga"; }
	}
	
	@RigidType
	public static class AnimalRoles implements Human, Monkey{
		@ObjectForRole public Human human = new Portuguese();
		@ObjectForRole public Monkey monkey;
		
		public AnimalRoles(){}
		
		@Override
		public String hello1() { return "Default hello "+this.getClass().getName(); }
		public String hello2() { return "Default hello "+this.getClass().getName(); }
		public String hello3() { return "Default hello "+this.getClass().getName(); }
		public String hello4() { return "Default hello "+this.getClass().getName(); }
	}
	public static void test(){
		AnimalRoles a = new AnimalRoles();
		assertEquals("Yap", "Was setup", a.hello1());
		assertEquals("Yap", "Was setup", a.hello2());
		assertEquals("Yap", "Fails", a.hello3());
		assertEquals("Yap", "Was setup", a.hello4());
		a.human = new Portuguese();
		assertEquals("Yap", "Was setup", a.hello1());
	}
	
}
