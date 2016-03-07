package pt.mashashi.javaroles.composition;

import static org.junit.Assert.fail;

import pt.mashashi.javaroles.annotations.ObjRole;

public class TestRigidCallMadeWrong {
	
	public interface Human{ String hello(); }
	public interface Monkey{ String hello(); }
	
	public static class Portuguese implements Human{
		@Override
		public String hello() {
			return "Hello buddy";
		}
	}
	public static class Bonobo implements Monkey{
		public AnimalRoles rigid;
		@Override public String hello() { 
			return rigid.hello(); 
		} 
	}
	public static class AnimalRoles implements Human, Monkey{
		@ObjRole public Human human;
		@ObjRole public Monkey monkey;
		
		public AnimalRoles(){
			Bonobo b = new Bonobo();
			b.rigid = this;
			this.monkey = b;
		}
		
		@Override
		public String hello() { return "Default hello "+this.getClass().getName(); }
	}
	
	public static void test(){
		AnimalRoles a = new AnimalRoles();
		try{
			System.out.println(a.hello());
			fail(StackOverflowError.class.getName()+" should be thrown.");
		}catch(StackOverflowError e){}
	}
	
}
