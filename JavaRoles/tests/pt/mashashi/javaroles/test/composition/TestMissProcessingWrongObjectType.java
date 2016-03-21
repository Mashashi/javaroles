package pt.mashashi.javaroles.test.composition;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;

import pt.mashashi.javaroles.MissProcessingException;
import pt.mashashi.javaroles.annotations.MissMsgReceptor;
import pt.mashashi.javaroles.annotations.ObjRigid;
import pt.mashashi.javaroles.annotations.ObjRole;

public class TestMissProcessingWrongObjectType {
	
	public interface Human{ void hello(); }
	public interface Monkey{ void hello(); }
	
	public static class Portuguese implements Human{
		public Portuguese(){
		}
		@Override
		public void hello(){
			throw new MissProcessingException("test");
		}
	}
	public static class Bonobo implements Monkey{
		private AnimalRoles a;
		public boolean missIt;
		@MissMsgReceptor public HashMap<String, Object> receptor;
		public Bonobo(AnimalRoles a){
			this.a=a;
			missIt = false;
		}
		@Override public void hello() {
			assertEquals("Yap", "test",receptor.get("0"));
			if(missIt){
				throw new MissProcessingException(receptor); 
			}
			this.a.out="Role Bonobo";
		} 
	}
	public static class AnimalRoles implements Human, Monkey{		
		
		@ObjRole public Human human;
		@ObjRole public Monkey monkey;
		@ObjRigid public Human original;
		@MissMsgReceptor public int receptor;
		
		public String out;
		public AnimalRoles(){
			human = new Portuguese();
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