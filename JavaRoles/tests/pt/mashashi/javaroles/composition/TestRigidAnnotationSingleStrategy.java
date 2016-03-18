package pt.mashashi.javaroles.composition;

import static org.junit.Assert.assertEquals;

import java.util.List;


import pt.mashashi.javaroles.annotations.InjObjRigid;
import pt.mashashi.javaroles.annotations.ObjRole;
import pt.mashashi.javaroles.annotations.Player;
import pt.mashashi.javaroles.impl.composition.RoleRegisterComposition;

public class TestRigidAnnotationSingleStrategy {
	
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
	/*@Rigid
	public static class AnimalRoles2 implements Human, Monkey{
		@ObjRole public Portuguese human = new Portuguese();
		@ObjRole public Monkey monkey;
		
		public AnimalRoles2(){
//			System.out.println("-->"+human);
 * 
//			if(human.rigid2==null){
//				fail("Was not initialized");
//			}
		}
		
		@Override
		public String hello1() { return "Default hello "+this.getClass().getName(); }
		public String hello2() { return "Default hello "+this.getClass().getName(); }
		public String hello3() { return "Default hello "+this.getClass().getName(); }
		public String hello4() { return "Default hello "+this.getClass().getName(); }
	}*/
	public static void test(){
		new RoleRegisterComposition()
			.includeGiven(TestRigidAnnotationSingleStrategy.class)
			.registerRoles();
		
		Portuguese p = new Portuguese();
		
		AnimalRoles a = new AnimalRoles(p, null);
		assertEquals("Yap", "Was setup", a.hello1());
		//assertEquals("Yap", "Was setup", a.hello2());
		assertEquals("Yap", "Fails", a.hello2());
		assertEquals("Yap", "Fails", a.hello3());
		//assertEquals("Yap", "Was setup", a.hello4());
		assertEquals("Yap", "Fails", a.hello4());
		
		AnimalRoles b = new AnimalRoles(p, null);
		assertEquals("Yap", "Was setup", b.hello1());
		assertEquals("Yap", "Was setup", b.hello2());
		assertEquals("Yap", "Fails", b.hello3());
		assertEquals("Yap", "Fails", b.hello4());
		
		AnimalRoles c = new AnimalRoles(p, null);
		assertEquals("Yap", "Was setup", c.hello1());
		assertEquals("Yap", "Was setup", c.hello2());
		assertEquals("Yap", "Fails", c.hello3());
		assertEquals("Yap", "Was setup", c.hello4());
		
		/*
		 // This feature was removed
		 a.human = new Portuguese();
		 assertEquals("Yap", "Was setup", a.hello1());
		 */
		
		// Inject before construct
		/*AnimalRoles2 b = new AnimalRoles2();
		System.out.println(b.human.rigid2);*/
	}
	
}
