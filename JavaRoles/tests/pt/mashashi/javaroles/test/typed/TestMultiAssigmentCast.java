package pt.mashashi.javaroles.test.typed;

import static org.junit.Assert.assertEquals;

import javassist.CtMethod;
import pt.mashashi.javaroles.impl.typed.TurnOnRole;

public class TestMultiAssigmentCast {
	
	public interface Human {
		String hello();
	}
	public interface Monkey {
		String hello();
	}
	public static class Animal implements Human, Monkey{
		
		public String ret;
		
		public String getRet(){
			return ret;
		}
		
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
		String h = ((Human) a).hello(), m = ((Monkey) b).hello();
		assertEquals("Yap", "Human", a.ret);
		assertEquals("Yap", "Monkey", b.ret);
		assertEquals("Yap", "Human", h);
		assertEquals("Yap", "Monkey", m);
	}
}
