package pt.mashashi.javaroles.composition;

import pt.mashashi.javaroles.ObjRole;

public class TestRigidObjectExceptions {
	
	public interface Human{ String hello(); }
	public interface Monkey{ String hello(); }
	public interface Subject{}
	
	public static class Portuguese implements Human{
		@Override
		public String hello() { return "Hello buddy"; }
	}
	public static class Bonobo implements Monkey{
		@Override public String hello() { return "Ugauga"; } 
	}
	public static class AnimalRoles implements Human, Monkey{
		@ObjRole public Human human;
		@ObjRole public Monkey monkey;
		@ObjRigid public Object original;
		public AnimalRoles(){}
		@Override
		public String hello() { return "Default hello "+this.getClass().getName(); }
		public String notInRole(){ return "Not in role";}
	}
	public static class AnimalRoles2 implements Human, Monkey{
		@ObjRole public Human human;
		@ObjRole public Monkey monkey;
		@ObjRigid public Subject original;
		public AnimalRoles2(){}
		@Override
		public String hello() { return "Default hello "+this.getClass().getName(); }
		public String notInRole(){ return "Not in role";}
	}
	
}
