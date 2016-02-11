package pt.mashashi.javaroles.composition;

import static org.junit.Assert.assertEquals;

import pt.mashashi.javaroles.ObjectForRole;

public class TestCallMultipleInput {
	
	public interface Human{ String hello(); String stuffing(String str, Object[] input); }
	public interface Monkey{ String hello(); }
	
	public static class Portuguese implements Human{
		@Override
		public String hello() { return "Hello buddy"; }
		@Override
		public String stuffing(String str, Object[] input) {
			return "Portuguese role type";
		}
	}
	public static class Bonobo implements Monkey{
		@Override public String hello() { return "Ugauga"; } 
	}
	public static class AnimalRoles implements Human, Monkey{
		@ObjectForRole public Human human;
		@ObjectForRole public Monkey monkey;
		public AnimalRoles(){
			human = new Portuguese();
			monkey = new Bonobo();
		}
		@Override
		public String hello() { return "Default hello "+this.getClass().getName(); }
		@Override
		public String stuffing(String str, Object[] input) {return "Rigid role type";}
	}
	
	public static void test(){
		AnimalRoles a = new AnimalRoles();
		assertEquals("Yap", "Portuguese role type", a.stuffing("test", new Object[]{}));
	}
	
}
