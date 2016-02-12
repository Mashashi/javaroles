package pt.mashashi.javaroles.composition;

import static org.junit.Assert.assertEquals;

import pt.mashashi.javaroles.MissProcessingException;
import pt.mashashi.javaroles.ObjectForRole;

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
			// TODO Make parameters thrown here somehow available
			/*
			 On the method that will be called on the rigid class
			 */
			throw new MissProcessingException(null); 
			//this.a.out="Role Portuguese";
		}
	}
	public static class Bonobo implements Monkey{
		private AnimalRoles a;
		public boolean missIt;
		public Bonobo(AnimalRoles a){
			this.a=a;
			missIt = false;
		}
		@Override public void hello() {
			if(missIt){
				throw new MissProcessingException(null); 
			}
			this.a.out="Role Bonobo";
		} 
	}
	public static class AnimalRoles implements Human, Monkey{		
		
		@ObjectForRole public Human human;
		@ObjectForRole public Monkey monkey;
		@OriginalRigid public Human original;
		
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
		a.hello();
		assertEquals("Yap", "Role Bonobo", a.out);
		((Bonobo)a.monkey).missIt = true;
		a.hello();
		assertEquals("Yap", "Rigid", a.out);
	}
	
}