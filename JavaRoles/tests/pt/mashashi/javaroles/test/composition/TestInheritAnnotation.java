package pt.mashashi.javaroles.test.composition;

import pt.mashashi.javaroles.annotations.InjObjRigid;
import pt.mashashi.javaroles.annotations.InjObjRigidPos;
import pt.mashashi.javaroles.annotations.ObjRole;
import pt.mashashi.javaroles.annotations.Player;
import pt.mashashi.javaroles.annotations.sprinkles.InheritAnnots;
import pt.mashashi.javaroles.impl.composition.RoleRegisterComposition;
import pt.mashashi.javaroles.register.RoleRegisterAssembler;

import static org.junit.Assert.*;



public class TestInheritAnnotation {
	
	public interface Human{ String hello(); }
	public interface Monkey{ String hello(); }
	
	
	
	
	
	
	public static class Portuguese implements Human{
		
		@InjObjRigid public AnimalRoles animalRoles;
		
		public String flag = null;
		
		@InheritAnnots
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
	
	public interface AnimalRoles{}
	
	
	
	
	
	
	
	
	public static class AnimalRoles1 implements AnimalRoles, Human, Monkey{
		
		@ObjRole public Human human;
		@ObjRole public Monkey monkey;
		
		@Player
		public AnimalRoles1(){
			this.human = new Lisboeta1();
			this.monkey = new Mike();
		}
		
		@Override
		public String hello() { return "Default hello "+this.getClass().getName(); }
		
	}
	
	public static class Lisboeta1 extends Portuguese{
		
		@Override
		public void callback(AnimalRoles animalRoles) {
			super.callback(animalRoles);
			super.flag+="extended";
		}
		
	}
	
	public static class Mike extends Bonobo{
		
		@Override
		public void callback(AnimalRoles animalRoles) {
			super.callback(animalRoles);
			super.flag+="extended";
		}
		
	}
	
	public static class AnimalRoles2 implements AnimalRoles, Human, Monkey{
		
		@ObjRole public Human human;
		@ObjRole public Monkey monkey;
		
		@Player
		public AnimalRoles2(){
			this.human = new Lisboeta2();
			this.monkey = new Bonobo();
		}
		
		@Override
		public String hello() { return "Default hello "+this.getClass().getName(); }
		
	}
	
	public static class Lisboeta2 extends Portuguese{
		
		@Override
		public void callback(AnimalRoles animalRoles) {
			super.callback(animalRoles);
			super.flag+="extended";
		}
		
	}

	public static void test(){
		
		new RoleRegisterAssembler(new RoleRegisterComposition())
				.includeGiven(TestInheritAnnotation.class)
				.excludeGiven(Lisboeta2.class)
				.inheritAnnots()
				.get()
				.registerRoles();
		
		AnimalRoles1 a = new AnimalRoles1();
		assertNotNull(((Portuguese)a.human).animalRoles);
		assertNotNull(((Bonobo)a.monkey).animalRoles);
		assertEquals("setextended",((Portuguese)a.human).flag);
		assertEquals("yupextended",((Bonobo)a.monkey).flag);
		
		
		
		
		
		AnimalRoles2 b = new AnimalRoles2();
		assertNull(((Portuguese)b.human).flag);
		
		
		InjObjRigidPos anot = null;
		try {
			anot = ((Lisboeta1)a.human).getClass().getDeclaredMethod("callback", AnimalRoles.class).getAnnotation(InjObjRigidPos.class);
		} catch (NoSuchMethodException|SecurityException e) {
			// Something wrong...
			e.printStackTrace();
		}
		assertNotNull(anot);
		
		
		anot = null;
		try {
			anot = ((Lisboeta2)b.human).getClass().getDeclaredMethod("callback", AnimalRoles.class).getAnnotation(InjObjRigidPos.class);
		} catch (NoSuchMethodException|SecurityException e) {
			// Something wrong...
			e.printStackTrace();
		}
		assertNull(anot);
		
		
	}
	
}
