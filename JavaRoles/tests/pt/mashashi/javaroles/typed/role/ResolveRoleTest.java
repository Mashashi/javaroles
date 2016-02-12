package pt.mashashi.javaroles.typed.role;


import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import pt.mashashi.javaroles.Human;
import pt.mashashi.javaroles.Monkey;
import pt.mashashi.javaroles.composition.TestMissProcessingWrongObjectType;
import pt.mashashi.javaroles.composition.TestRigidObjectExceptions;
import pt.mashashi.javaroles.typed.ConditionalExprNotSupportedByRoleSystemException;
import pt.mashashi.javaroles.typed.RoleRegisterTyped;

/*
import org.junit.Rule;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
*/


/**
 * 
 * 
 * @author Rafael
 *
 */
public class ResolveRoleTest {
	
	
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
	@Rule public SingleTestRule test = new SingleTestRule("");*/
	
	
	
	
	
	
	
	

	
	
	@BeforeClass
	public static void setup(){
		//Role.registerRool("Animal");
		new RoleRegisterTyped("tests/").registerRoolsExcludeGiven(TestRigidObjectExceptions.class, TestMissProcessingWrongObjectType.class);
	}
	
	
	/*@Test
	public void fails(){
		fail();
	}*/
	
	@Test
	public void testSingleLineCall(){
		Animal a = new Animal();
		Animal b = new Animal();
		
		Human h = ((Human) a);
		Monkey m = ((Monkey) b);
		h.hello(); m.hello(); // important to be this way here
		
		assertEquals("Yap", "Human", a.ret);
		assertEquals("Yap", "Monkey", b.ret);
	}
	
	@Test
	public void testSingleLineCast(){
		Animal a = new Animal();
		Animal b = new Animal();
		((Monkey) b).hello(); ((Human) a).hello(); // important to be this way here
		
		assertEquals("Yap", "Human", a.ret);
		assertEquals("Yap", "Monkey", b.ret);
	}
	
	
	@Test
	public void testSeveralLineMethodCall(){
		Animal a = new Animal();
		Animal b = new Animal();
		Human h = ((Human) a);
		Monkey m = ((Monkey) b);
		System.out.println(m.hello()+" "+h.hello());
		
		// This does not really work on every vm the single line version does seem to work
		/*System.out.println(
				m.hello()
				+" "+
				h.hello()
		);*/
		
		assertEquals("Yap", "Human", a.ret);
		assertEquals("Yap", "Monkey", b.ret);
	}
	
	
	
	@Test
	public void testCallback() {
		Animal a = new Animal();
		Human h = a; 
		h.hello();
		h.hello();
		Monkey m = a;
		m.hello();
		m.hello();
		//System.out.println(a.ret);
	}
	
	
	
	
	
	
	
	
	
	
	
	@Test
	public void testMethodParameter() {
		class AnimalShelter{
			public String test(Monkey k){
				return k.hello();
			}
		}
		String c = new AnimalShelter().test(new Animal());
		assertEquals("Yap", "Monkey", c);
	}
	
	@Test
	public void testConstructorParameter(){
		class AnimalShelter{
			public AnimalShelter(Monkey k){
				k.hello();
			}
		}
		Animal a = new Animal();
		new AnimalShelter((Monkey) a);
		assertEquals("Yap", "Monkey", a.ret);
	}
	
	@Test
	public void testCast(){
		Animal a = new Animal();
		((Monkey)a).hello();
		assertEquals("Yap", "Monkey", a.ret);
	}
	
	@Test
	public void testCastAssignement(){
		Animal a = new Animal();
		String t = ((Human)a).hello();
		assertEquals("Yap", "Human", t);
	}
	
	@Test
	public void testBlock(){
		Animal a = new Animal();
		Animal b = new Animal();
		{
			Human m = ((Human)a);
			m.hello();
		}
		{
			Monkey m = ((Monkey)b);
			m.hello();
		}
		assertEquals("Yap", "Monkey", b.ret);
		assertEquals("Yap", "Human", a.ret);
	}
	
	@Test
	public void testBlockAssigment(){
		Animal a = new Animal();
		Human m = ((Human)a);
		String l = m.hello();
		System.out.println(a.ret);
		assertEquals("Yap", "Human", l);
	}
	
	@Test
	public void testNestedCallVariable(){
		Animal a = new Animal();
		Animal b = new Animal();
		Human h = a;
		Monkey m = ((Monkey) b);
		for(int i = 0;i<2;i++){
			System.out.println(m.hello()+" "+h.hello());
		}
		assertEquals("Yap", "Human", a.ret);
		assertEquals("Yap", "Monkey", b.ret);
	}
	
	@Test
	public void testNestedCallCast(){
		Animal a = new Animal();
		Animal b = new Animal();
		for(int i = 0;i<2;i++){
			System.out.println(((Monkey) b).hello()+" "+((Human) a).hello());
		}
		assertEquals("Yap", "Human", a.ret);
		assertEquals("Yap", "Monkey", b.ret);
	}
	
	@Test
	public void testNestedCallMix(){
		Animal a = new Animal();
		Animal b = new Animal();
		Human h = ((Human) a);
		for(int i = 0;i<2;i++){
			System.out.println(((Monkey) b).hello()+" "+h.hello());
		}
		assertEquals("Yap", "Human", a.ret);
		assertEquals("Yap", "Monkey", b.ret);
	}
	
	@Test
	public void testMultiAssigmentCast(){
		Animal a = new Animal();
		Animal b = new Animal();
		String h = ((Human) a).hello(), m = ((Monkey) b).hello();
		assertEquals("Yap", "Human", a.ret);
		assertEquals("Yap", "Monkey", b.ret);
		assertEquals("Yap", "Human", h);
		assertEquals("Yap", "Monkey", m);
	}
	
	
	@Test
	public void testMultiAssigmentVariable(){
		Animal a = new Animal();
		Animal b = new Animal();
		Human o1 = ((Human) a);
		Monkey o2 = ((Monkey) b);
		String h = o1.hello(), m = o2.hello();
		assertEquals("Yap", "Human", h);
		assertEquals("Yap", "Monkey", m);
	}
	
	
	@SuppressWarnings("unused")
	@Test
	public void testTernary(){
		try{
			Animal a = new Animal();
			Animal b = new Animal();
			Human h = ((Human) a);
			Monkey m = ((Monkey) b);
			for(int i = 0;i<1;i++){
				System.out.println((true?h.hello():h.hello())+m.hello());
			}
			/*assertEquals("Yap", "Human", a.ret);
			assertEquals("Yap", "Monkey", b.ret);*/
			fail();
		}catch(ConditionalExprNotSupportedByRoleSystemException e){
			String expectedMsg = "Conditional expressions are not supported by the role system. Exception on code: true ? h.hello() : h.hello()";
			assertEquals("Yap", expectedMsg, e.getMessage());
		}
	}
	
}