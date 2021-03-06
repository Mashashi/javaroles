package pt.mashashi.javaroles.test.typed;

import org.junit.BeforeClass;
import org.junit.Test;

import pt.mashashi.javaroles.impl.typed.RoleRegisterTyped;
import pt.mashashi.javaroles.register.RoleRegisterAssembler;

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
public class RolesTest {
	
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
	@Rule public SpecificTestRule specificTestRule = new SpecificTestRule();*/
	
	@BeforeClass
	public static void setup(){
		//Role.registerRool("Animal");
		new RoleRegisterAssembler(new RoleRegisterTyped("tests/"))
			.includeGivenPkg(RolesTest.class)
			.get()
			.registerRoles();
	}
	
	@Test
	public void testRoleInitilizers(){
		TestRoleInitializers.test();
	}
	
	@Test
	public void testSingleLineCall(){
		TestSingleLineCall.test();
	}
	
	@Test
	public void testSingleLineCast(){
		TestSingleLineCast.test();
	}
	
	@Test
	public void testSeveralLineMethodCall(){
		TestSeveralLineMethodCall.test();
	}
	
	
	
	
	
	@Test
	public void testCallback() {
		TestCallback.test();
	}
	
	@Test
	public void testMethodParameter() {
		TestMethodParameter.test();
	}
	
	
	@Test
	public void testConstructorParameter(){
		TestConstructorParameter.test();
	}
	
	@Test
	public void testCast(){
		TestCast.test();
	}
	
	@Test
	public void testCastAssignement(){
		TestCastAssignement.test();
	}
	
	@Test
	public void testBlock(){
		TestBlock.test();
	}
	
	@Test
	public void testBlockAssigment(){
		TestBlockAssigment.test();
	}
	
	@Test
	public void testNestedCallVariable(){
		TestNestedCallVariable.test();
	}
	
	@Test
	public void testNestedCallCast(){
		TestNestedCallCast.test();
	}
	
	@Test
	public void testNestedCallMix(){
		TestNestedCallMix.test();
	}
	
	@Test
	public void testMultiAssigmentCast(){
		TestMultiAssigmentCast.test();
	}
	
	@Test
	public void testMultiAssigmentVariable(){
		TestMultiAssigmentVariable.test();
	}
	
	@Test
	public void testTernary(){
		TestTernary.test();
	}
	
	@Test
	public void testRoleOverrideSupress() {
		TestRoleOverrideSupress.test();
	}
	
	@Test
	public void testRoleReturn() {
		TestRoleReturn.test();
	}
	
}