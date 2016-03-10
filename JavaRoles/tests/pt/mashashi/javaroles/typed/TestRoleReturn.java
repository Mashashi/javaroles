package pt.mashashi.javaroles.typed;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;

import pt.mashashi.javaroles.MissProcessingException;
import pt.mashashi.javaroles.annotations.ObjRole;
import pt.mashashi.javaroles.impl.composition.TurnOffRole;
import pt.mashashi.javaroles.impl.typed.TurnOnRole;

public class TestRoleReturn {
	
	public interface Human {
		String hello(); 
		String die(String age);  
		String eat();
	}
	
	public interface Monkey {
		String hello(); 
		String eat();
	}
	
	public static class Portuguese implements Human{
	
		public static final String HALLO = "Olá moço";
		public static final String DIE = "Ai matarem-me...";
		public static final String EAT = "Estou cheio dred";
		
		public Portuguese() {}
		
		@Override
		public String hello() {
			return HALLO;
		}

		@Override
		public String die(String age) {
			return DIE+age;
		}

		@Override
		public String eat() {
			return EAT;
		}

	}
	
	public static class Bonobo implements Monkey{

		public static final String HALLO = "Ugauga";
		public static final String EAT = "Nhamnham";
		
		public Bonobo() {}
		
		@Override
		public String hello(){
			return "Ugauga";
		}

		@Override
		public String eat() {
			return EAT;
		}

	}

	public static class MaleBonobo extends Bonobo{
		
		public MaleBonobo() {}
		
		@Override
		public String hello() {
			HashMap<String, Object> map = new HashMap<>();
			map.put("msg", "Not processing because monkey doesn't say hello");
			throw new MissProcessingException(map);
		}
		
	}
	
	public static class AnimalRoles implements Human, Monkey{
		
		public static final String HALLO = "Default hallo";
		public static final String DIE = "Default they kill me...";
		public static final String EAT = "Default eat...";
		
		@ObjRole public Human human;
		
		@ObjRole public Monkey monkey;
		
		public AnimalRoles(){
			human = new Portuguese();
			monkey = new Bonobo();
		}
		
		@Override
		@TurnOnRole
		public String hello() {
			return HALLO;
		}
		
		@Override
		public String die(String age) {
			return DIE+age;
		}

		@Override
		@TurnOffRole
		public String eat() {
			return EAT;
		}
		
	}
	
	public static class MaleAnimalRoles extends AnimalRoles{
		
		public MaleAnimalRoles() {
			super();
			super.monkey = new MaleBonobo();
		}
		
		public void hello_MissProcessing(String role, HashMap<String,Object> args) {
			System.out.println("Process missing from role: "+role+". Argument count: "+args.size()+".");
		}
	}
	
	public static void test(){
		AnimalRoles a = new AnimalRoles();
		Human h = a;
		assertEquals("Yap", Portuguese.HALLO, h.hello());
		Monkey m = a;
		assertEquals("Yap", Bonobo.HALLO, m.hello());
	}
}
