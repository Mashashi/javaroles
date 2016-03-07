package pt.mashashi.javaroles.composition;

import static org.junit.Assert.fail;

import java.io.File;

import pt.mashashi.javaroles.annotations.ObjRigid;
import pt.mashashi.javaroles.annotations.ObjRole;
import pt.mashashi.javaroles.impl.composition.RoleRegisterComposition;

public class TestWriteClasses {
	
	public interface Human{ String hello(); }
	public interface Monkey{ String hello(); }
	
	public static class Portuguese implements Human{
		@Override
		public String hello() {
			return "Hello buddy";
		}
	}
	public static class Bonobo implements Monkey{
		@Override public String hello() { return "Ugauga"; } 
	}
	public static class AnimalRoles implements Human, Monkey{
		@ObjRole public Human human;
		@ObjRole public Monkey monkey;
		
		@ObjRigid public Monkey rigidMonkey1;
		@ObjRigid public Monkey rigidMonkey2;
		@ObjRigid public Human rigidHuman;
		
		public AnimalRoles(){
			this.human = new Portuguese();
			this.monkey = new Bonobo();
		}
		
		@Override
		public String hello() { return "Default hello "+this.getClass().getName(); }
	}
	
	public static void test(){
		String dir = "C:\\ouputDir";
		new RoleRegisterComposition(new String[]{"pt.mashashi.javaroles.composition.TestWriteClasses"}).writeClasses(dir).registerRools();
		String path = dir+"\\pt\\mashashi\\javaroles\\composition\\TestWriteClasses$AnimalRoles.class";
		File f = new File(path);
		if(!f.exists()){
			fail("Class file wasn't created");
		}
		f = new File(dir);
		f.delete();
	}
	
}
