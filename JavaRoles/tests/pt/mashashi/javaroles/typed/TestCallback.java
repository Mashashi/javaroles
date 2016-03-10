package pt.mashashi.javaroles.typed;

import javassist.CtMethod;
import pt.mashashi.javaroles.impl.typed.TurnOnRole;
import static org.junit.Assert.assertEquals;

public class TestCallback {
	
	public interface Human {
		String hello(); 
	}
	
	public interface Monkey {
		String hello(); 
	}
	
	public static class Animal implements Human, Monkey{
		
		public String ret;
		
		public Animal(){
			ret = "Init";
		}
		
		/**
		 * When a method of a role is called the pre is always invoked first
		 * 
		 * @param role
		 * @param method
		 */
		public void Pre(String role, CtMethod method){
			//System.out.println("Pre "+role+", method "+method.getName());
			this.ret = role;
		}
		
		@Override
		@TurnOnRole
		public String hello() {
			return ret;
		}
		
	}
	
	public static void test(){
		
		Animal a = new Animal();
		Human h = a; 
		String t = null;
		t = h.hello();
		assertEquals("Human", t);
		t = h.hello();
		assertEquals("Human", t);
		Monkey m = a;
		t = m.hello();
		assertEquals("Monkey", t);
		t = m.hello();
		assertEquals("Monkey", t);
		//System.out.println(a.ret);
		
	}
}
