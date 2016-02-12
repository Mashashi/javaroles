package pt.mashashi.javaroles.composition;

import static org.junit.Assert.*;

import pt.mashashi.javaroles.ObjectForRole;

public class TestOriginalRigidVoidMethodException {
	
	
	
	public interface Human{ void hello() throws SpecificException; }
	public interface Monkey{ void hello() throws SpecificException; }
	
	@SuppressWarnings("serial")
	public static class SpecificException extends Exception {
		public SpecificException(String msg){
			super(msg);
		}
	}
	
	public static class Portuguese implements Human{
		public Portuguese(){}
		@Override
		public void hello() throws SpecificException{
			throw new SpecificException("test");
		}
	}
	public static class Bonobo implements Monkey{
		public Bonobo(){}
		@Override public void hello() {} 
	}
	public static class AnimalRoles implements Human, Monkey{		
		@ObjectForRole public Human human;
		@ObjectForRole public Monkey monkey;
		@OriginalRigid public Human original;
		public String out;
		public AnimalRoles(){
			human = new Portuguese();
			monkey = new Bonobo();
		}
		@Override
		public void hello() {
			out = "Rigid";
		}
	}
	public static void test() throws SpecificException{
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