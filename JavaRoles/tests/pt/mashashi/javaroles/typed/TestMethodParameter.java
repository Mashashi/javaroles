package pt.mashashi.javaroles.typed;

import static org.junit.Assert.assertEquals;

import javassist.CtMethod;
import pt.mashashi.javaroles.impl.typed.TurnOnRole;

public class TestMethodParameter {
	public interface Monkey {
		String hello();
	}
	public static class Animal implements Monkey{
		
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
		class AnimalShelter{
			public String test(Monkey k){
				return k.hello();
			}
		}
		String c = new AnimalShelter().test(new Animal());
		assertEquals("Yap", "Monkey", c);
	}
}
