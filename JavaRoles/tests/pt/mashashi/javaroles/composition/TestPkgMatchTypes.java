package pt.mashashi.javaroles.composition;

import static org.junit.Assert.assertEquals;

import pt.mashashi.javaroles.annotations.ObjRole;
import pt.mashashi.javaroles.annotations.Player;
import pt.mashashi.javaroles.impl.composition.RoleRegisterComposition;
import pt.mashashi.javaroles.register.RoleRegister;
import pt.mashashi.javaroles.register.RoleRegister.MatchType;

public class TestPkgMatchTypes {
	
	public interface Human{String hello1();}
	public interface Monkey{String hello1();}
	
	public static class Portuguese implements Human{
		@Override public String hello1() { return "Hey 1"; }
	}
	public static class Bonobo implements Monkey{
		@Override public String hello1() { return "Ugauga"; }
	}
	
	
	
	
	public static class AnimalRolesStartsImplicit{
		
		@Player
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
		
		@Player
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
		
	}
	
	
	public static class AnimalRolesStartsExplicit {
		
		@Player
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
		
		
		@Player
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
		
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class AnimalRolesExact {
		
		@Player
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
		
		
		@Player
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
		
	}
	
	
	
	
	
	
	
	
	
	
	@Player
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
	
	
	@Player
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
	
	@Player
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
	
	
	
	
	
	
	
	
	
	
	
	@Player
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
	
	
	@Player
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
	@Player
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
		
		rr = new RoleRegisterComposition()
				.includeGiven(AnimalRolesStartsImplicit.class);
		rr.registerRoles();
		r = rr.getClassReport().contains(AnimalRolesStartsImplicit.AnimalRolesStartsImplicit1.class.getName()) && 
			rr.getClassReport().contains(AnimalRolesStartsImplicit.AnimalRolesStartsImplicit2.class.getName());
		assertEquals(true, r);
		
		rr = new RoleRegisterComposition()
				.includeGiven(AnimalRolesStartsExplicit.class)
				.setMatchType(MatchType.STARTS_WITH);
		rr.registerRoles();
		r = rr.getClassReport().contains(AnimalRolesStartsExplicit.AnimalRolesStartsExplicit1.class.getName()) &&
			rr.getClassReport().contains(AnimalRolesStartsExplicit.AnimalRolesStartsExplicit2.class.getName());
		assertEquals(true, r && rr.getClassReport().size()==2);
		
		rr = new RoleRegisterComposition()
				.includeGiven(AnimalRolesExact.class)
				.setMatchType(MatchType.EXACT);
		rr.registerRoles();
		assertEquals(true, rr.getClassReport().size()==0);
		
		rr =new RoleRegisterComposition()
				.includeGivenRaw("pt.mashashi.javaroles.composition.TestPkgMatchTypes$AnimalRolesExact$AnimalRolesExact1")
				.setMatchType(MatchType.EXACT);
		rr.registerRoles();
		rr.registerRoles();
		r = rr.getClassReport().contains(AnimalRolesExact.AnimalRolesExact1.class.getName());		
		assertEquals(true, r && rr.getClassReport().size()==1);
		
		
		
		
//		rr = new RoleRegisterComposition(
//				"^pt\\.mashashi\\.javaroles\\.composition\\.TestPkgMatchTypes\\$AnimalRolesRegex\\d$"
//		).setPkgMatchType(MATCH_TYPE_PKG.REGEX);
//		rr.registerRoles();
//		r = rr.getClassReport().contains("pt.mashashi.javaroles.composition.TestPkgMatchTypes$AnimalRolesRegex1") && rr.getClassReport().contains("pt.mashashi.javaroles.composition.TestPkgMatchTypes$AnimalRolesRegex2");		
//		assertEquals(true, r && rr.getClassReport().size()==2);
		
		rr = new RoleRegisterComposition()		
				.includeGivenRaw("pt.mashashi.javaroles.composition.TestPkgMatchTypes$AnimalRolesExactSelective")
				.setMatchType(MatchType.EXACT);
		rr.registerRoles();
		r = rr.getClassReport().contains(AnimalRolesExactSelective.class.getName());		
		assertEquals(true, r && rr.getClassReport().size()==1);
		
	}
	
}
