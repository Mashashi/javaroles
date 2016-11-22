package pt.mashashi.javaroles.test.composition;

import pt.mashashi.javaroles.annotations.InjObjRigid;
import pt.mashashi.javaroles.annotations.ObjRole;
import pt.mashashi.javaroles.annotations.Player;
import static org.junit.Assert.assertEquals;

public class TestStaticRole {
	
	interface Human{ String hello(String[] a); String hello2(); }
	interface Monkey{ String hello(String[] a); }
	
	static class Bonobo implements Monkey{
		@InjObjRigid Object obj;
		@Override public String hello(String[] a) { return obj.toString(); } 
	}
	
	static class Bonobo2 extends Bonobo{
		@Override public String hello(String[] a) { return obj.toString(); } 
	}
	
	@Player
	static class AnimalRolesSwaped implements Monkey{
		@ObjRole public Monkey monkey;
		public AnimalRolesSwaped(Monkey monkey){
			this.monkey = monkey; 
		}
		@Override
		public String hello(String[] a) { return "Default hello "+this.getClass().getName(); }
	}
	
	@Player
	static class AnimalRolesSwaped2 implements Monkey{
		@ObjRole private Monkey monkey;
		public AnimalRolesSwaped2(Monkey monkey){
			this.monkey = monkey; 
		}
		@Override
		public String hello(String[] a) { return "Default hello "+this.getClass().getName(); }
	}
	
	//@Player
	static class AnimalRolesSwaped3 extends AnimalRolesSwaped2{

		public AnimalRolesSwaped3(Monkey monkey) {
			super(monkey);
		}
		
	}
	
	@Player
	static class AnimalRolesSwaped4 implements Monkey{
		@ObjRole private static Monkey monkey;
		public AnimalRolesSwaped4(Monkey monkey){
			this.monkey = monkey; 
		}
		@Override
		public String hello(String[] a) { return "Default hello "+this.getClass().getName(); }
	}
	
	public static void test(){
		
		Bonobo bonobo = new Bonobo();
		
		AnimalRolesSwaped a = new AnimalRolesSwaped(bonobo);
		String vala = a.hello(null);
		assertEquals(vala, a.toString());
		AnimalRolesSwaped b = new AnimalRolesSwaped(bonobo);
		String valb = b.hello(null);
		assertEquals(valb, a.toString());
		
		bonobo = new Bonobo();
		AnimalRolesSwaped2 a2 = new AnimalRolesSwaped2(bonobo);
		String vala2 = a2.hello(null);
		assertEquals(vala2, a2.toString());
		AnimalRolesSwaped2 b2 = new AnimalRolesSwaped2(bonobo);
		String valb2 = b2.hello(null);
		assertEquals(valb2, a2.toString());
		
		bonobo = new Bonobo2();
		AnimalRolesSwaped2 a3 = new AnimalRolesSwaped2(bonobo);
		String vala3 = a3.hello(null);
		assertEquals(vala3, a3.toString());
		AnimalRolesSwaped2 b3 = new AnimalRolesSwaped2(bonobo);
		String valb3 = b3.hello(null);
		assertEquals(valb3, a3.toString());
		
		
		bonobo = new Bonobo2();
		AnimalRolesSwaped3 a4 = new AnimalRolesSwaped3(bonobo);
		String vala4 = a4.hello(null);
		assertEquals(vala4, a4.toString());
		AnimalRolesSwaped3 b4 = new AnimalRolesSwaped3(bonobo);
		String valb4 = b4.hello(null);
		assertEquals(valb4, a4.toString());
		
		bonobo = new Bonobo();
		AnimalRolesSwaped4 a5 = new AnimalRolesSwaped4(bonobo);
		String vala5 = a5.hello(null);
		assertEquals(vala5, a5.toString());
		AnimalRolesSwaped4 b5 = new AnimalRolesSwaped4(bonobo);
		String valb5 = b5.hello(null);
		assertEquals(valb5, b5.toString());
		
	}
	
}
