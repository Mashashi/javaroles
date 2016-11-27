package pt.mashashi.javaroles.test.typed;

import static org.junit.Assert.assertEquals;

import java.util.LinkedList;
import java.util.List;

import javassist.CtMethod;
import pt.mashashi.javaroles.impl.typed.TurnOnRole;

public class TestRoleInitializers {
	
	public interface Monkey {
		List<String> hello();
	}
	public static class Animal implements Monkey{
		public List<String> order;
		public Animal(){
			order = new LinkedList<String>();
			order.add("Init");
		}
		public void Monkey(){
			order.add("Role Constructor");
		}
		public void nullStop(String newRole){
			order.add(newRole);
			//throw new RuntimeException("tips");
		}
		public void MonkeyStart(String oldRole){
			order.add(oldRole);
		}
		public void MonkeyPre(CtMethod invokedMethod){
			order.add(invokedMethod.getName());
		}
		public void Pre(String role, CtMethod method){
			order.add(role);
			order.add(method.getName());
			throw new RuntimeException("Expected message");
		}
		@Override
		@TurnOnRole
		public List<String> hello() {
			return order;
		}
	}
	
	public static void test(){
		
		
		Animal a = new Animal();
		try{
			((Monkey) a).hello();
		}catch(RuntimeException e){
			// Do nothing...
			assertEquals(e.getMessage(), "Expected message");
		}
		assertEquals("Init", a.order.get(0));
		assertEquals("Role Constructor", a.order.get(1));
		assertEquals("Monkey", a.order.get(2));
		assertEquals(null, a.order.get(3));
		assertEquals("hello", a.order.get(4));
		assertEquals("Monkey", a.order.get(5));
		assertEquals("hello", a.order.get(6));
		
	}
}
