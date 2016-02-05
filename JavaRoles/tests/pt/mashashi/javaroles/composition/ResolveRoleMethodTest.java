package pt.mashashi.javaroles.composition;

import static org.junit.Assert.*;

import org.junit.BeforeClass;

import org.junit.Test;
/*
import org.junit.Rule;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
*/

import pt.mashashi.javaroles.typed.rolemethod.Bonobo;
import pt.mashashi.javaroles.typed.rolemethod.Portuguese;

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
	
	/*
	public class SingleTestRule implements MethodRule {
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
	@Rule public SingleTestRule test = new SingleTestRule("");
	*/
	
	@BeforeClass
	public static void setup(){
		new RoleRegisterComposition().registerRools();
	}
	
	
	@Test
	public void testMethodRoleObjectMethodCall() {
		AnimalRoles a = new AnimalRoles();
		assertEquals("Yap", Portuguese.HALLO, a.hello());
		AnimalRolesSwaped b = new AnimalRolesSwaped();
		assertEquals("Yap", Bonobo.HALLO, b.hello());
	}
	
}
