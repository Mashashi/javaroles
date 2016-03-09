package pt.mashashi.javaroles.composition;

import static org.junit.Assert.*;

import org.junit.BeforeClass;

import org.junit.Test;

import pt.mashashi.javaroles.annotations.AnnotationException;
import pt.mashashi.javaroles.annotations.MissMsgReceptor;
import pt.mashashi.javaroles.annotations.MissUseAnnotationExceptionException;
import pt.mashashi.javaroles.annotations.ObjRigid;
import pt.mashashi.javaroles.impl.composition.RoleRegisterComposition;

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
		//new RoleRegisterComposition().registerRools();
		/*
		new RoleRegisterComposition().registerRoolsExcludeGiven(
				"pt.mashashi.javaroles.composition.TestRigidObjectExceptions",
				"pt.mashashi.javaroles.composition.TestOriginalRigidTurnOffRoleMethod"
		);
		*/
		new RoleRegisterComposition(new String[]{"pt.mashashi.javaroles"}).registerRoolsExcludeGiven(
				TestRigidObjectExceptions.class,
				TestOriginalRigidTurnOffRoleMethod.class,
				TestMissProcessingWrongObjectType.class,
				TestWriteClasses.class,
				TestRigidAnnotationSingleStrategy.class,
				TestRigidAnnotationMultipleStrategy.class
		);
	}
	
	
	@Test
	public void testMethodRoleObjectMethodCall() {
		TestMethodRoleObjectMethodCall.test();
	}
	
	@Test
	public void testWithNullRoleObjects() {
		TestWithNullRoleObjects.test();
	}
	
	@Test
	public void testWithNotAccessibleClasses() {
		TestWithNotAccessibleClasses.test();
	}
	
	@Test
	public void testRigidCall() {
		TestRigidCall.test();
	}
	
	@Test
	public void testNotInRole() {
		TestNotInRole.test();
	}
	
	@Test
	public void testToSecondRoleFirstNull() {
		TestToSecondRoleFirstNull.test();
	}
	
	@Test
	public void testRoleSurpressed() {
		TestRoleSurpressed.test();
	}
	
	@Test
	public void testRigidCallMadeWrong() {
		TestRigidCallMadeWrong.test();
	}
	
	@Test
	public void testCallMultiInput() {
		TestCallMultipleInput.test();
	}
	
	
	@Test 
	public void testRigidObjectExceptions(){
		
		try{
			new RoleRegisterComposition(new String[]{""},new Class[]{TestRigidObjectExceptions.AnimalRoles.class}).registerRools();
			fail("no exception thrown");
		}catch(MissUseAnnotationExceptionException e){
			if(	
					!e.getAnotation().equals(ObjRigid.class)
					||
					e.getAnotationException() != AnnotationException.MISS_USE
					){
				fail("exception was not thrown correctly");
			}
		}
		
		try{
			new RoleRegisterComposition(new String[]{""},new Class[]{TestRigidObjectExceptions.AnimalRoles2.class}).registerRools();
			fail("no exception thrown");
		}catch(MissUseAnnotationExceptionException e){
			if(		
					!e.getAnotation().equals(ObjRigid.class) 
					|| 
					e.getAnotationException() != AnnotationException.NOT_IMPLEMENTED
					){
				fail("exception was not thrown correctly");
			}
		}
	
	}
	
	@Test
	public void testOriginalRigidTurnOffRoleMethod() {
		new RoleRegisterComposition(new String[]{""},new Class[]{TestOriginalRigidTurnOffRoleMethod.class}).registerRools();
		TestOriginalRigidTurnOffRoleMethod.test();
	}
	
	
	@Test
	public void testOriginalRigidVoidMethod() {
		TestOriginalRigidVoidMethod.test();
		
	}
	
	@Test
	public void testOriginalRigidVoidMethodException() {
		try {
			TestOriginalRigidVoidMethodException.test();
			fail("no exception thrown");
		} catch (TestOriginalRigidVoidMethodException.SpecificException e) {
			if(!e.getMessage().equals("test")){
				fail("Error message is not what is supposed");
			}
			return;
		}
		fail("Exception was not caught");
	}
	
	@Test
	public void testMissProcessing() {
		TestMissProcessing.test();
	}
	
	@Test
	public void testMissProcessingWrongObjectType() {
		try {
			new RoleRegisterComposition(new String[]{""},new Class[]{TestMissProcessingWrongObjectType.class}).registerRools();
			fail("no exception thrown");
		} catch (MissUseAnnotationExceptionException e) {
			if(
					!e.getAnotation().equals(MissMsgReceptor.class) 
					|| 
					e.getAnotationException() != AnnotationException.BAD_TYPE
					){
				fail("exception was not thrown correctly");
			}
		}
	}
	
	@Test
	public void TestRigidAnnotationSingleStrategy() {
		TestRigidAnnotationSingleStrategy.test();
	}
	
	@Test
	public void TestRigidAnnotationMultipleStrategy() {
		TestRigidAnnotationMultipleStrategy.test();
	}
	
	@Test
	public void testWriteClasses() {
		TestWriteClasses.test();
	}
	
}