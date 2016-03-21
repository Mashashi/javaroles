package pt.mashashi.javaroles.test.typed;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import javassist.CtMethod;
import pt.mashashi.javaroles.impl.typed.ConditionalExprNotSupportedByRoleSystemException;
import pt.mashashi.javaroles.impl.typed.TurnOnRole;

public class TestTernary {
	
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
	
	@SuppressWarnings("unused")
	public static void test(){
		try{
			Animal a = new Animal();
			Animal b = new Animal();
			Human h = ((Human) a);
			Monkey m = ((Monkey) b);
			for(int i = 0;i<1;i++){
				System.out.println((true?h.hello():h.hello())+m.hello());
			}
			/*assertEquals("Yap", "Human", a.ret);
			assertEquals("Yap", "Monkey", b.ret);*/
			fail();
		}catch(ConditionalExprNotSupportedByRoleSystemException e){
			String expectedMsg = "Conditional expressions are not supported by the role system. Exception on code: true ? h.hello() : h.hello()";
			assertEquals("Yap", expectedMsg, e.getMessage());
		}
	}
}
