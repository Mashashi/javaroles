package pt.mashashi.javaroles.test.composition;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import pt.mashashi.javaroles.annotations.ObjRole;
import pt.mashashi.javaroles.impl.composition.RoleRegisterComposition;
import pt.mashashi.javaroles.register.RoleRegisterAssembler;

public class TestObjectRoleDefinitionOnAnnotation {
	
	
	
	
	interface Human{ String hello(String[] a); String hello2(); }
	interface Monkey{ String hello(String[] a); }
	
	static class Portuguese implements Human{
		@Override
		public String hello(String[] a) {
			return "Hello buddy";
		}

		@Override
		public String hello2() {
			return "Hey man";
		}
	}
	static class Bonobo implements Monkey{
		@Override public String hello(String[] a) { return "Ugauga"; } 
	}
	
	
	
	
	
	
	
	
	
	
	static class AnimalRoles implements Human, Monkey{
		@ObjRole({Human.class}) private Portuguese human; // Human
		@ObjRole private Monkey monkey; // Monkey
		public AnimalRoles(Portuguese human, Bonobo monkey){
			this.human = human; 
			this.monkey = monkey;
		}
		@Override
		public String hello(String[] a) { return "Default hello "+this.getClass().getName(); }
		@Override
		public String hello2() {
			// TODO Auto-generated method stub
			return null;
		}
	}
	static class NotImplementedInterfaceOnRigid implements Human{
		@ObjRole({Monkey.class}) public Bonobo monkey;
		@ObjRole public Human human;
		public NotImplementedInterfaceOnRigid(){
			human = new Portuguese(); 
			monkey = new Bonobo(); 
		}
		@Override 
		public String hello(String[] a) { return "Default hello "+this.getClass().getName(); }
		@Override
		public String hello2() {
			// TODO Auto-generated method stub
			return null;
		}
	}
	static class NotImplementedInterfaceOnRole1 implements Human{
		@ObjRole({Monkey.class}) public Portuguese monkey;
		@ObjRole public Human human;
		public NotImplementedInterfaceOnRole1(){
			human = new Portuguese(); 
			monkey = new Portuguese(); 
		}
		@Override 
		public String hello(String[] a) { return "Default hello "+this.getClass().getName(); }
		@Override
		public String hello2() {
			// TODO Auto-generated method stub
			return null;
		}
	}
	static class NotImplementedInterfaceOnRole2 implements Human{
		@ObjRole public Portuguese monkey;
		@ObjRole({Monkey.class}) public Human human;
		public NotImplementedInterfaceOnRole2(){
			human = new Portuguese(); 
			monkey = new Portuguese(); 
		}
		@Override 
		public String hello(String[] a) { return "Default hello "+this.getClass().getName(); }
		@Override
		public String hello2() {
			// TODO Auto-generated method stub
			return null;
		}
	}
	static class TwoTimeDefined implements Human, Monkey{
		@ObjRole(Human.class) private Human human;
		@ObjRole(Monkey.class) public Monkey monkey;
		public TwoTimeDefined(){
			human = new Portuguese();
			monkey = new Bonobo();
		}
		@Override 
		public String hello(String[] a) { return "Default hello "+this.getClass().getName(); }
		@Override
		public String hello2() {
			// TODO Auto-generated method stub
			return null;
		}
	}
	
	
	
	public static void test(){
		
		// If we wanted to register the rigid here we would have to use the string representation or else a duplicate definition exception would be raised
		/*new RoleRegisterAssembler(new RoleRegisterComposition())
			.includeGivenRaw("pt.mashashi.javaroles.test.composition.TestObjectRoleDefinitionOnAnnotation$AnimalRoles")
			.get()
			.registerRoles();*/
		
		AnimalRoles a = new AnimalRoles(new Portuguese(), null);
		assertEquals("Yap", "Hello buddy", a.hello(null));
		
		AnimalRoles b = new AnimalRoles(null, new Bonobo());
		assertEquals("Yap", "Ugauga", b.hello(null));
		
		try{
			new RoleRegisterAssembler(new RoleRegisterComposition())
				.includeGiven(NotImplementedInterfaceOnRigid.class)
				.get()
				.registerRoles();
			fail("Should throw an exception");
		}catch(RuntimeException e){
			assertEquals(e.getMessage(), "The annotation @ObjRole was used incorrectly.\n The class \""+NotImplementedInterfaceOnRigid.class.getName()+"\" has to implement the interfaces \""+Bonobo.class.getName()+"\" of the field \""+Monkey.class.getName()+"\".");
		}
		
		try{
			new RoleRegisterAssembler(new RoleRegisterComposition())
				.includeGiven(NotImplementedInterfaceOnRole1.class)
				.get()
				.registerRoles();
			fail("Should throw an exception");
		}catch(RuntimeException e){
			assertEquals(e.getMessage(), "The annotation @ObjRole was used incorrectly.\n The class role \""+NotImplementedInterfaceOnRole1.class.getName()+".monkey\" of type \""+Portuguese.class.getName()+"\" has to implement the interfaces \""+Monkey.class.getName()+"\" that are present on the @ObjRole annotation.");
		}
		
		try{
			new RoleRegisterAssembler(new RoleRegisterComposition())
				.includeGiven(NotImplementedInterfaceOnRole2.class)
				.get()
				.registerRoles();
			fail("Should throw an exception");
		}catch(RuntimeException e){
			assertEquals(e.getMessage(), "The annotation @ObjRole was used incorrectly.\n The class role \""+NotImplementedInterfaceOnRole2.class.getName()+".human\" of type \""+Human.class.getName()+"\" has to implement the interfaces \""+Monkey.class.getName()+"\" that are present on the @ObjRole annotation.");
		}
		
		
		
		// If we wanted to register the rigid here we would have to use the string representation or else a duplicate definition exception would be raised 
		/*new RoleRegisterAssembler(new RoleRegisterComposition())
			.includeGivenRaw("pt.mashashi.javaroles.test.composition.TestObjectRoleDefinitionOnAnnotation$TwoTimeDefined")	
			//.includeGiven(TwoTimeDefined.class) 
			.get()
			.registerRoles();*/
		
		TwoTimeDefined h = new TwoTimeDefined();
		assertEquals(h.hello(null), "Ugauga");
		assertEquals(h.hello2(), "Hey man");
		
		
	}
	
}
