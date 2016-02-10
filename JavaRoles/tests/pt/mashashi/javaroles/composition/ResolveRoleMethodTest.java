package pt.mashashi.javaroles.composition;

import static org.junit.Assert.*;

import org.junit.BeforeClass;

import org.junit.Test;

import pt.mashashi.javaroles.Human;
/*
import org.junit.Rule;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
*/



/*
import org.junit.Rule;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
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
	@Rule public SingleTestRule test = new SingleTestRule("testCallMultiInput");*/

	
	@BeforeClass
	public static void setup(){
		new RoleRegisterComposition().registerRools();
	}
	
	
	@Test
	public void testMethodRoleObjectMethodCall() {
		AnimalRoles a = new AnimalRoles(new Portuguese(), new Bonobo());
		assertEquals("Yap", Portuguese.HALLO, a.hello());
		AnimalRolesSwaped b = new AnimalRolesSwaped();
		assertEquals("Yap", Bonobo.HALLO, b.hello());
	}
	
	@Test
	public void testWithNullRoleObjects() {
		AnimalRoles a = new AnimalRoles(null, null);
		assertEquals("Yap", AnimalRoles.HALLO, a.hello());
	}
	
	@Test
	public void testRigidCall() {
		AnimalRoles a = new AnimalRoles(new Portuguese(), null);
		assertEquals("Yap", "Just dance modified!", a.dance());
	}
	
	@Test
	public void testNotInRole() {
		AnimalRoles a = new AnimalRoles(null, null);
		assertEquals("Yap", "Oh oh", a.notInRole());
	}
	
	@Test
	public void testToSecondRoleFirstNull() {
		AnimalRoles a = new AnimalRoles(null, new Bonobo());
		assertEquals("Yap", Bonobo.HALLO, a.hello());
	}
	
	@Test
	public void testRoleSurpressed() {
		AnimalRoles a = new AnimalRoles(new Portuguese(), new Bonobo());
		assertEquals("Yap", AnimalRoles.DIE+"35", a.die("35"));
	}
	
	@Test
	public void testCallWrongStackOverflow() {
		AnimalRoles a = new AnimalRoles(null, new Bonobo());
		try{
			System.out.println(a.eat());
			fail("The ProbablyRigidTypeNotDeclaredException should be thrown.");
		}catch(StackOverflowError e){}
	}
	
	@Test
	public void testCallMultiInput() {
		AnimalRoles a = new AnimalRoles(new Portuguese(), new Bonobo());
		a.stuffing("test", null);
	}
	
	@Test
	public void testCallCoreVoid() {
		AnimalRoles a = new AnimalRoles(new Portuguese(), new Bonobo());
		System.out.println(a.originalHuman.eat());
		a.born();
		a.originalHuman.born();
	}
}