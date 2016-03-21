package pt.mashashi.javaroles.test.composition;

import static org.junit.Assert.assertEquals;

import java.util.List;


import pt.mashashi.javaroles.annotations.InjObjRigid;
import pt.mashashi.javaroles.annotations.ObjRole;
import pt.mashashi.javaroles.annotations.Player;
import pt.mashashi.javaroles.impl.composition.RoleRegisterComposition;
import pt.mashashi.javaroles.injection.InjectionStrategy;
import pt.mashashi.javaroles.register.RoleRegisterAssembler;

public class TestRigidAnnotationMultipleStrategy {
	
	public interface Human{String hello1();String hello2();String hello3();String hello4();}
	public interface Monkey{String hello1();String hello2();String hello3();String hello4();}
	
	public static class Portuguese implements Human{
		@InjObjRigid public AnimalRoles rigid1;
		@InjObjRigid public Object rigid2;
		@SuppressWarnings("rawtypes")
		@InjObjRigid public List rigid3;
		@InjObjRigid private AnimalRoles rigid4;
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
	
	@Player
	public static class AnimalRoles implements Human, Monkey{
		@ObjRole public Human human;
		@ObjRole public Monkey monkey;
		
		public AnimalRoles(Human human, Monkey monkey){
			this.human = human;
			this.monkey = monkey;
		}
		
		@Override
		public String hello1() { return "Default hello "+this.getClass().getName(); }
		public String hello2() { return "Default hello "+this.getClass().getName(); }
		public String hello3() { return "Default hello "+this.getClass().getName(); }
		public String hello4() { return "Default hello "+this.getClass().getName(); }
	}
	
	public static void test(){
		
		new RoleRegisterAssembler(new RoleRegisterComposition())
				.includeGiven(TestRigidAnnotationMultipleStrategy.class)
				.setRigidInjectionStrategy(InjectionStrategy.getInstanceMultiple())
				.get()
				.registerRoles();
		
		Portuguese p = new Portuguese();
		
		AnimalRoles a = new AnimalRoles(p, null);
		assertEquals("Yap", "Was setup", a.hello1());
		assertEquals("Yap", "Was setup", a.hello2());
		assertEquals("Yap", "Fails", a.hello3());
		assertEquals("Yap", "Was setup", a.hello4());
		
	}
	
}
