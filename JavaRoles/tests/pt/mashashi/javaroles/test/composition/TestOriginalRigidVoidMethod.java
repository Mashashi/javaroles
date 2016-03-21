package pt.mashashi.javaroles.test.composition;

import static org.junit.Assert.*;

import pt.mashashi.javaroles.annotations.ObjRigid;
import pt.mashashi.javaroles.annotations.ObjRole;

public class TestOriginalRigidVoidMethod {
	
	public interface Human{ void hello(); }
	public interface Monkey{ void hello(); }
	
	public static class Portuguese implements Human{
		private AnimalRoles a;
		public Portuguese(AnimalRoles a){
			this.a=a;
		}
		@Override
		public void hello(){
			this.a.out="Role Portuguese";
		}
	}
	public static class Bonobo implements Monkey{
		private AnimalRoles a;
		public Bonobo(AnimalRoles a){
			this.a=a;
		}
		@Override public void hello() {
			this.a.out="Role Bonobo";
		} 
	}
	public static class AnimalRoles implements Human, Monkey{		
		@ObjRole public Human human;
		@ObjRole public Monkey monkey;
		@ObjRigid public Human original;
		public String out;
		public AnimalRoles(){
			human = new Portuguese(this);
			monkey = new Bonobo(this);
		}
		@Override
		public void hello() {
			out = "Rigid";
		}
	}
	public static void test(){
		AnimalRoles a = new AnimalRoles();
		a.original.hello();
		assertEquals("Yap", "Rigid", a.out);
		a.hello();
		assertEquals("Yap", "Role Portuguese", a.out);
		a.human = null;
		a.hello();
		assertEquals("Yap", "Role Bonobo", a.out);
	}
	
}