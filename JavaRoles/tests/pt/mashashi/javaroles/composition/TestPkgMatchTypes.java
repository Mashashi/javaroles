package pt.mashashi.javaroles.composition;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;

import pt.mashashi.javaroles.RoleRegister;
import pt.mashashi.javaroles.RoleRegister.MATCH_TYPE;
import pt.mashashi.javaroles.annotations.InjObjRigid;
import pt.mashashi.javaroles.annotations.ObjRole;
import pt.mashashi.javaroles.annotations.Rigid;
import pt.mashashi.javaroles.impl.composition.RoleRegisterComposition;

public class TestPkgMatchTypes {
	
	public interface Human{String hello1();}
	public interface Monkey{String hello1();}
	
	public static class Portuguese implements Human{
		@Override public String hello1() { return "Hey 1"; }
	}
	public static class Bonobo implements Monkey{
		@Override public String hello1() { return "Ugauga"; }
	}
	
	
	
	
	
	
	
	
	
	
	@Rigid
	public static class AnimalRolesStartsImplicit1 implements Human, Monkey{
		@ObjRole public Human human;
		@ObjRole public Monkey monkey;
		
		public AnimalRolesStartsImplicit1(Human human, Monkey monkey){
			this.human = human;
			this.monkey = monkey;
		}
		
		@Override
		public String hello1() { return "Default hello "+this.getClass().getName(); }
	}
	
	
	@Rigid
	public static class AnimalRolesStartsImplicit2 implements Human, Monkey{
		@ObjRole public Human human;
		@ObjRole public Monkey monkey;
		
		public AnimalRolesStartsImplicit2(Human human, Monkey monkey){
			this.human = human;
			this.monkey = monkey;
		}
		
		@Override
		public String hello1() { return "Default hello "+this.getClass().getName(); }
	}
	
	
	
	
	
	
	
	
	@Rigid
	public static class AnimalRolesStartsExplicit1 implements Human, Monkey{
		@ObjRole public Human human;
		@ObjRole public Monkey monkey;
		
		public AnimalRolesStartsExplicit1(Human human, Monkey monkey){
			this.human = human;
			this.monkey = monkey;
		}
		
		@Override
		public String hello1() { return "Default hello "+this.getClass().getName(); }
	}
	
	
	@Rigid
	public static class AnimalRolesStartsExplicit2 implements Human, Monkey{
		@ObjRole public Human human;
		@ObjRole public Monkey monkey;
		
		public AnimalRolesStartsExplicit2(Human human, Monkey monkey){
			this.human = human;
			this.monkey = monkey;
		}
		
		@Override
		public String hello1() { return "Default hello "+this.getClass().getName(); }
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	@Rigid
	public static class AnimalRolesExact1 implements Human, Monkey{
		@ObjRole public Human human;
		@ObjRole public Monkey monkey;
		
		public AnimalRolesExact1(Human human, Monkey monkey){
			this.human = human;
			this.monkey = monkey;
		}
		
		@Override
		public String hello1() { return "Default hello "+this.getClass().getName(); }
	}
	
	
	@Rigid
	public static class AnimalRolesExact2 implements Human, Monkey{
		@ObjRole public Human human;
		@ObjRole public Monkey monkey;
		
		public AnimalRolesExact2(Human human, Monkey monkey){
			this.human = human;
			this.monkey = monkey;
		}
		
		@Override
		public String hello1() { return "Default hello "+this.getClass().getName(); }
	}
	
	
	
	
	
	
	
	
	@Rigid
	public static class AnimalRolesRegex1 implements Human, Monkey{
		@ObjRole public Human human;
		@ObjRole public Monkey monkey;
		
		public AnimalRolesRegex1(Human human, Monkey monkey){
			this.human = human;
			this.monkey = monkey;
		}
		
		@Override
		public String hello1() { return "Default hello "+this.getClass().getName(); }
	}
	
	
	@Rigid
	public static class AnimalRolesRegex2 implements Human, Monkey{
		@ObjRole public Human human;
		@ObjRole public Monkey monkey;
		
		public AnimalRolesRegex2(Human human, Monkey monkey){
			this.human = human;
			this.monkey = monkey;
		}
		
		@Override
		public String hello1() { return "Default hello "+this.getClass().getName(); }
	}
	
	@Rigid
	public static class AnimalRolesRegex3d implements Human, Monkey{
		@ObjRole public Human human;
		@ObjRole public Monkey monkey;
		
		public AnimalRolesRegex3d(Human human, Monkey monkey){
			this.human = human;
			this.monkey = monkey;
		}
		
		@Override
		public String hello1() { return "Default hello "+this.getClass().getName(); }
	}
	
	
	
	
	
	
	
	
	
	
	
	@Rigid
	public static class AnimalRolesExactSelective implements Human, Monkey{
		@ObjRole public Human human;
		@ObjRole public Monkey monkey;
		
		public AnimalRolesExactSelective(Human human, Monkey monkey){
			this.human = human;
			this.monkey = monkey;
		}
		
		@Override
		public String hello1() { return "Default hello "+this.getClass().getName(); }
	}
	
	
	@Rigid
	public static class AnimalRolesExactSelective_i1 implements Human, Monkey{
		@ObjRole public Human human;
		@ObjRole public Monkey monkey;
		
		public AnimalRolesExactSelective_i1(Human human, Monkey monkey){
			this.human = human;
			this.monkey = monkey;
		}
		
		@Override
		public String hello1() { return "Default hello "+this.getClass().getName(); }
	}
	@Rigid
	public static class AnimalRolesExactSelective_i2 implements Human, Monkey{
		@ObjRole public Human human;
		@ObjRole public Monkey monkey;
		
		public AnimalRolesExactSelective_i2(Human human, Monkey monkey){
			this.human = human;
			this.monkey = monkey;
		}
		
		@Override
		public String hello1() { return "Default hello "+this.getClass().getName(); }
	}
	
	public static void test(){
		
		RoleRegister rr = null;
		boolean  r = false;
		
		rr = new RoleRegisterComposition(
				"pt.mashashi.javaroles.composition.TestPkgMatchTypes$AnimalRolesStartsImplicit"
		);
		rr.registerRools();
		r = rr.getClassReport().contains("pt.mashashi.javaroles.composition.TestPkgMatchTypes$AnimalRolesStartsImplicit1") && rr.getClassReport().contains("pt.mashashi.javaroles.composition.TestPkgMatchTypes$AnimalRolesStartsImplicit2");
		assertEquals(true, r);
		
		rr = new RoleRegisterComposition(
				"pt.mashashi.javaroles.composition.TestPkgMatchTypes$AnimalRolesStartsExplicit"
		).setPkgMatchType(MATCH_TYPE.STARTS_WITH);
		rr.registerRools();
		r = rr.getClassReport().contains("pt.mashashi.javaroles.composition.TestPkgMatchTypes$AnimalRolesStartsExplicit1") && rr.getClassReport().contains("pt.mashashi.javaroles.composition.TestPkgMatchTypes$AnimalRolesStartsExplicit2");
		assertEquals(true, r && rr.getClassReport().size()==2);
		
		rr = new RoleRegisterComposition(
				"pt.mashashi.javaroles.composition.TestPkgMatchTypes$AnimalRolesExact"
		).setPkgMatchType(MATCH_TYPE.EXACT);
		rr.registerRools();
		assertEquals(true, rr.getClassReport().size()==0);
		
		rr = new RoleRegisterComposition(
				"pt.mashashi.javaroles.composition.TestPkgMatchTypes$AnimalRolesExact1"
		).setPkgMatchType(MATCH_TYPE.EXACT);
		rr.registerRools();
		r = rr.getClassReport().contains("pt.mashashi.javaroles.composition.TestPkgMatchTypes$AnimalRolesExact1");		
		assertEquals(true, r && rr.getClassReport().size()==1);
		
		rr = new RoleRegisterComposition(
				"^pt\\.mashashi\\.javaroles\\.composition\\.TestPkgMatchTypes\\$AnimalRolesRegex\\d$"
		).setPkgMatchType(MATCH_TYPE.REGEX);
		rr.registerRools();
		r = rr.getClassReport().contains("pt.mashashi.javaroles.composition.TestPkgMatchTypes$AnimalRolesRegex1") && rr.getClassReport().contains("pt.mashashi.javaroles.composition.TestPkgMatchTypes$AnimalRolesRegex2");		
		assertEquals(true, r && rr.getClassReport().size()==2);
		
		
		rr = new RoleRegisterComposition(
				"pt.mashashi.javaroles.composition.TestPkgMatchTypes$AnimalRolesExactSelective"
		).setPkgMatchType(0, MATCH_TYPE.EXACT);
		rr.registerRools();
		r = rr.getClassReport().contains("pt.mashashi.javaroles.composition.TestPkgMatchTypes$AnimalRolesExactSelective");		
		assertEquals(true, r && rr.getClassReport().size()==1);
		
	}
	
}
