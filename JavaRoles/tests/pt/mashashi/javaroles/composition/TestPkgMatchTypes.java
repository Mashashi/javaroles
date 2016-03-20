package pt.mashashi.javaroles.composition;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import pt.mashashi.javaroles.annotations.ObjRole;
import pt.mashashi.javaroles.annotations.Player;
import pt.mashashi.javaroles.impl.composition.RoleRegisterComposition;
import pt.mashashi.javaroles.logging.LoggerTarget;
import pt.mashashi.javaroles.register.RoleRegister;
import pt.mashashi.javaroles.register.RoleRegisterAssembler;
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
		
		boolean  r;
		List<String> report;
		final String preId = "Test:"+TestPkgMatchTypes.class.getName()+"-";
		
		rr = new RoleRegisterAssembler(new RoleRegisterComposition())
				.includeGiven(AnimalRolesStartsImplicit.class)
				.get();
		Logger.getRootLogger().setLevel(Level.ALL);
		rr.registerRoles();
		report = LoggerTarget.string(preId);
		r = report.contains(AnimalRolesStartsImplicit.AnimalRolesStartsImplicit1.class.getName()) && 
			report.contains(AnimalRolesStartsImplicit.AnimalRolesStartsImplicit2.class.getName());
		assertEquals(true, r);
		
		LoggerTarget.clear();
		
		rr = new RoleRegisterAssembler(new RoleRegisterComposition())
				.includeGiven(AnimalRolesStartsExplicit.class)
				.setMatchType(MatchType.STARTS_WITH)
				.get();
		Logger.getRootLogger().setLevel(Level.ALL);
		rr.registerRoles();
		report = LoggerTarget.string(preId);
		r = report.contains(AnimalRolesStartsExplicit.AnimalRolesStartsExplicit1.class.getName()) &&
			report.contains(AnimalRolesStartsExplicit.AnimalRolesStartsExplicit2.class.getName());
		assertEquals(true, r && report.size()==2);
		
		LoggerTarget.clear();
		
		rr = new RoleRegisterAssembler(new RoleRegisterComposition())
				.includeGiven(AnimalRolesExact.class)
				.setMatchType(MatchType.EXACT)
				.get();
		Logger.getRootLogger().setLevel(Level.ALL);
		rr.registerRoles();
		report = LoggerTarget.string(preId);
		assertEquals(true, report.size()==0);
		
		LoggerTarget.clear();
		
		rr = new RoleRegisterAssembler(new RoleRegisterComposition())
				.includeGivenRaw("pt.mashashi.javaroles.composition.TestPkgMatchTypes$AnimalRolesExact$AnimalRolesExact1")
				.setMatchType(MatchType.EXACT)
				.get();
		Logger.getRootLogger().setLevel(Level.ALL);
		rr.registerRoles();
		report = LoggerTarget.string(preId);
		r = report.contains(AnimalRolesExact.AnimalRolesExact1.class.getName());		
		assertEquals(true, r && report.size()==1);
		
		LoggerTarget.clear();
		
		
		/*
		rr = new RoleRegisterComposition()
				.includeGivenRawPkg("^pt\\.mashashi\\.javaroles\\.composition\\.TestPkgMatchTypes\\$AnimalRolesRegex\\d$")
				.setMatchType(MatchType.REGEX);
		rr.registerRoles();
		r = rr.getClassReport().contains("pt.mashashi.javaroles.composition.TestPkgMatchTypes$AnimalRolesRegex1") && rr.getClassReport().contains("pt.mashashi.javaroles.composition.TestPkgMatchTypes$AnimalRolesRegex2");		
		assertEquals(true, r && rr.getClassReport().size()==2);
		*/
		
		rr = new RoleRegisterAssembler(new RoleRegisterComposition())	
				.includeGivenRaw("pt.mashashi.javaroles.composition.TestPkgMatchTypes$AnimalRolesExactSelective")
				.setMatchType(MatchType.EXACT)
				.get();
		Logger.getRootLogger().setLevel(Level.ALL);
		rr.registerRoles();
		report = LoggerTarget.string(preId);
		r = report.contains(AnimalRolesExactSelective.class.getName());		
		assertEquals(true, r && report.size()==1);
		
	}
	
}
