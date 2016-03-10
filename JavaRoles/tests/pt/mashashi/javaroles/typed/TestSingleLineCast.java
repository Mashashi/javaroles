package pt.mashashi.javaroles.typed;

import static org.junit.Assert.assertEquals;

import javassist.CtMethod;
import pt.mashashi.javaroles.impl.typed.TurnOnRole;

public class TestSingleLineCast {
	
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
		String m = ((Monkey) b).hello(); String h = ((Human) a).hello(); // important to be this way here
		
		assertEquals("Yap", "Human", h);
		assertEquals("Yap", "Monkey", m);
		
		assertEquals("Yap", "Human", a.ret);
		assertEquals("Yap", "Monkey", b.ret);
		
	}
	
}
