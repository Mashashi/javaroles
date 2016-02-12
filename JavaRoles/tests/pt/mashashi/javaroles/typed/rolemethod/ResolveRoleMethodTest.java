package pt.mashashi.javaroles.typed.rolemethod;

import static org.junit.Assert.*;


import org.junit.BeforeClass;

import org.junit.Test;

/*
import org.junit.Rule;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
*/

import pt.mashashi.javaroles.Human;
import pt.mashashi.javaroles.Monkey;
import pt.mashashi.javaroles.composition.TestMissProcessingWrongObjectType;
import pt.mashashi.javaroles.composition.TestRigidObjectExceptions;
import pt.mashashi.javaroles.typed.RoleRegisterTyped;

/*
import pt.mashashi.javaroles.RoleBus;
import org.apache.log4j.Logger;
*/

/**
 * 
 * @author Rafael
 *
 */
public class ResolveRoleMethodTest {
	
	/*public class SingleTestRule implements MethodRule {
	    private String applyMethod;
	    public SingleTestRule(String applyMethod) {
	        this.applyMethod = applyMethod;
	    }
	    @Override
	    public Statement apply(final Statement statement, final FrameworkMethod method, final Object target) {
	        return new Statement() {
	            @Override
	            public void evaluate() throws Throwable {
	                if (applyMethod.equals(method.getName())) {
	                    statement.evaluate();
	                }
	            }
	        };
	    }
	}
	@Rule public SingleTestRule test = new SingleTestRule("testRoleReturnExtension");*/
	
	
	
	@BeforeClass
	public static void setup(){
		new RoleRegisterTyped("tests/").registerRoolsExcludeGiven(TestRigidObjectExceptions.class, TestMissProcessingWrongObjectType.class);
	}
	
	
	/*@Test
	public void testMultiroles() {
		AnimalRoles a = new AnimalRoles();
		System.out.println(a.hello());
		System.out.println(a.die("34"));
		System.out.println(a.eat());
		fail("Not done");
	}*/
	
	@Test
	public void testRoleOverrideSupress() {
		AnimalRoles a = new MaleAnimalRoles();
		Human h = a;
		assertEquals("Yap", AnimalRoles.EAT, h.eat());
		Monkey m = a;
		assertEquals("Yap", AnimalRoles.EAT, m.eat());
	}
	
	@Test
	public void testRoleReturnExtension() {		
		//Logger.getLogger(RoleBus.class.getName()).debug("testRoleReturnExtension");
		/*AnimalRoles a = new MaleAnimalRoles();
		Human h = a;
		assertEquals("Yap", Portuguese.HALLO, h.hello());
		Monkey m = a;
		assertEquals("Yap", AnimalRoles.HALLO, m.hello());*/
	}
	
	@Test
	public void testRoleReturn() {
		AnimalRoles a = new AnimalRoles();
		Human h = a;
		assertEquals("Yap", Portuguese.HALLO, h.hello());
		Monkey m = a;
		assertEquals("Yap", Bonobo.HALLO, m.hello());
	}
	
}
