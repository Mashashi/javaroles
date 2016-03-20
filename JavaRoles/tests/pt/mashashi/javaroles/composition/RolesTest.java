package pt.mashashi.javaroles.composition;

import static org.junit.Assert.*;

import org.junit.BeforeClass;

import org.junit.Test;

import pt.mashashi.javaroles.annotations.AnnotationException;
import pt.mashashi.javaroles.annotations.MissMsgReceptor;
import pt.mashashi.javaroles.annotations.MissUseAnnotationExceptionException;
import pt.mashashi.javaroles.annotations.ObjRigid;
import pt.mashashi.javaroles.impl.composition.RoleRegisterComposition;
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
		
		Class<?>[] exclude = {
				TestRigidObjectExceptions.class,
				TestOriginalRigidTurnOffRoleMethod.class,
				TestMissProcessingWrongObjectType.class,
				TestWriteClasses.class,
				TestRigidAnnotationSingleStrategy.class,
				TestRigidAnnotationMultipleStrategy.class,
				TestPkgMatchTypes.class,
				TestPlay.class,
				TestInheritAnnotation.class,
				TestCallSuper.class
		};
		
		
		new RoleRegisterAssembler(new RoleRegisterComposition())
			.includeGivenPkg(RolesTest.class)
			.excludeGiven(exclude)
			.get()
			.registerRoles();
		
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
			new RoleRegisterAssembler(new RoleRegisterComposition())
				.includeGiven(TestRigidObjectExceptions.AnimalRoles.class)
				.get()
				.registerRoles();
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
			new RoleRegisterAssembler(new RoleRegisterComposition())
				.includeGiven(TestRigidObjectExceptions.AnimalRoles2.class)
				.get()
				.registerRoles();
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
		new RoleRegisterAssembler(new RoleRegisterComposition())
			.includeGiven(TestOriginalRigidTurnOffRoleMethod.class)
			.get()
			.registerRoles();
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
			new RoleRegisterAssembler(new RoleRegisterComposition())
				.includeGiven(TestMissProcessingWrongObjectType.class)
				.get()
				.registerRoles();
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
	public void testRigidAnnotationSingleStrategy() {
		TestRigidAnnotationSingleStrategy.test();
	}
	
	@Test
	public void testRigidAnnotationMultipleStrategy() {
		TestRigidAnnotationMultipleStrategy.test();
	}
	
	@Test
	public void testWriteClasses() {
		TestWriteClasses.test();
	}
	
	
	@Test
	public void testPkgMatchTypes() {
		TestPkgMatchTypes.test();
	}
	
	@Test
	public void testPlay() {
		TestPlay.test();
	}
	
	@Test
	public void testPlayCheck() {
		TestPlayCheck.test();
	}
	
	@Test
	public void testInjectionMethod() {
		TestInjectionMethod.test();
	}
	
	@Test
	public void testInheritAnnotation() {
		TestInheritAnnotation.test();
	}
	
	@Test
	public void testCallSuper() {
		TestCallSuper.test();
	}
	
	@Test
	public void testSeal() {
		TestSeal.test();
	}
	
}