package pt.mashashi.javaroles.test.composition;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertEquals;

import pt.mashashi.javaroles.annotations.InjObjRigid;
import pt.mashashi.javaroles.annotations.ObjRole;
import pt.mashashi.javaroles.annotations.Player;

public class TestNotAbleToObtainPrivate {
	
	public interface Human{ String hello(); }
	
	public static class Portuguese implements Human{
		
		@InjObjRigid
		public Human portuguese;
		
		@Override
		public String hello() { return "Hello buddy"; }
		
		public Human getHuman(){
			return portuguese;
		}
		
	}
	
	@Player
	public static class Magnata implements Human{
		
		@ObjRole(value=Human.class)
		public Portuguese portuguese = new Portuguese();
		
		public Magnata(){
		}
		
		public Portuguese getPortuguese(){
			return portuguese;
		}

		@Override
		public String hello() {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	
	public static class Ze extends Magnata{
	}
	
	public static void test(){
		Ze ze = new Ze();
		
		assertEquals("Yap", "Hello buddy", ze.hello());
		assertNotEquals("Yap", null, ze.getPortuguese().getHuman());
		
	}
	
}
