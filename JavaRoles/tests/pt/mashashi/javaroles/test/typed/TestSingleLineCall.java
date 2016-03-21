package pt.mashashi.javaroles.test.typed;

import static org.junit.Assert.assertEquals;

import javassist.CtMethod;
import pt.mashashi.javaroles.impl.typed.TurnOnRole;

public class TestSingleLineCall {
	
	
	
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
		Animal b = new Animal();
		
		Human h = ((Human) a);
		Monkey m = ((Monkey) b);
		String ht = h.hello(); String mt = m.hello(); // important to be this way here
		
		assertEquals("Yap", "Human", ht);
		assertEquals("Yap", "Monkey", mt);
		
		assertEquals("Yap", "Human", a.ret);
		assertEquals("Yap", "Monkey", b.ret);
	}
}
