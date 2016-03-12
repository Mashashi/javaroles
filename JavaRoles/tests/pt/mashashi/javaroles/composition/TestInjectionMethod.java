package pt.mashashi.javaroles.composition;

import pt.mashashi.javaroles.annotations.InjObjRigid;
import pt.mashashi.javaroles.annotations.InjObjRigidPos;
import pt.mashashi.javaroles.annotations.ObjRole;
import pt.mashashi.javaroles.annotations.Play;
import pt.mashashi.javaroles.annotations.Play.Place;
import pt.mashashi.javaroles.annotations.Player;
import pt.mashashi.javaroles.composition.TestPlay.Human;
import pt.mashashi.javaroles.composition.TestPlay.Monkey;

import static org.junit.Assert.*;



public class TestInjectionMethod {
	
	public static class Portuguese implements Human{
		
		@InjObjRigid public AnimalRoles animalRoles;
		
		public String flag = null;
		
		@InjObjRigidPos
		public void callback(AnimalRoles animalRoles){
			flag = "set";
		}
		
		@Override public String hello() { return "Hello buddy"; }
		
	}
	
	public static class Bonobo implements Monkey{
		
		@InjObjRigid public AnimalRoles animalRoles;
		
		@InjObjRigidPos
		public void callBack(AnimalRoles animalRoles){}
		
		@Override  public String hello() { return "Ugauga"; } 
		
	}
	
	public interface AnimalRoles{}
	
	public static class AnimalRoles1 implements AnimalRoles, Human, Monkey{
		
		@ObjRole public Human human;
		@ObjRole public Monkey monkey;
		
		@Player
		public AnimalRoles1(){
			this.human = new Portuguese();
			this.monkey = new Bonobo();
		}
		
		@Override
		public String hello() { return "Default hello "+this.getClass().getName(); }
		
	}
	
	@Player
	public static class AnimalRoles2 implements AnimalRoles, Human, Monkey{
		
		@ObjRole public Human human;
		@ObjRole public Monkey monkey;
		
		
		public AnimalRoles2(){
			this.human = new Portuguese();
			this.monkey = new Bonobo();
		}
		
		@Override
		public String hello() { return "Default hello "+this.getClass().getName(); }
		
	}
	
	
	public static class AnimalRoles3 implements AnimalRoles, Human, Monkey{
		
		@ObjRole public Human human;
		@ObjRole public Monkey monkey;
		
		
		public AnimalRoles3(){
			this.human = new Portuguese();
			this.monkey = new Bonobo();
		}
		
		@Override
		public String hello() { return "Default hello "+this.getClass().getName(); }
		
		
		@Play
		public void setIt(Human human){}
		
	}
	
	public static class AnimalRoles4 implements AnimalRoles, Human, Monkey{
		
		@ObjRole public Human human;
		@ObjRole public Monkey monkey;
		
		
		public AnimalRoles4(){
			this.human = new Portuguese();
			this.monkey = new Bonobo();
		}
		
		@Override
		public String hello() { return "Default hello "+this.getClass().getName(); }
		
		
		@Play(order=Place.BEFORE)
		public void setIt(Human human){}
		
	}
	
	public static class AnimalRoles5 implements AnimalRoles, Human, Monkey{
		
		@ObjRole public Human human;
		@ObjRole public Monkey monkey;
		
		
		public AnimalRoles5(){
			this.human = new Portuguese();
			this.monkey = new Bonobo();
		}
		
		@Override
		public String hello() { return "Default hello "+this.getClass().getName(); }
		
		
		@Play(order=Place.AFTER)
		public void setIt(Human human){}
		
	}
	
	public static class Lisboeta1 extends Portuguese{}
	
	public static class AnimalRoles6 implements AnimalRoles, Human, Monkey{
		
		@ObjRole public Human human;
		@ObjRole public Monkey monkey;
		
		
		public AnimalRoles6(){
			this.human = new Lisboeta1();
			this.monkey = new Bonobo();
		}
		
		@Override
		public String hello() { return "Default hello "+this.getClass().getName(); }
		
		
		@Play(order=Place.AFTER)
		public void setIt(Human human){}
		
	}
	
	public static class Lisboeta2 extends Portuguese{
		
		@InjObjRigidPos
		@Override
		public void callback(AnimalRoles animalRoles) {
			super.callback(animalRoles);
			super.flag+="extended";
		}
		
	}
	
	public static class AnimalRoles7 implements AnimalRoles, Human, Monkey{
		
		@ObjRole public Human human;
		@ObjRole public Monkey monkey;
		
		
		public AnimalRoles7(){
			this.human = new Lisboeta2();
			this.monkey = new Bonobo();
		}
		
		@Override
		public String hello() { return "Default hello "+this.getClass().getName(); }
		
		
		@Play(order=Place.AFTER)
		public void setIt(Human human){}
		
	}
	
	public static void test(){
		
		AnimalRoles1 a = new AnimalRoles1();
		assertEquals("set",((Portuguese)a.human).flag);
		
		AnimalRoles2 b = new AnimalRoles2();
		assertEquals("set",((Portuguese)b.human).flag);
		
		AnimalRoles3 c = new AnimalRoles3();
		assertEquals(null,((Portuguese)c.human).flag);
		c.setIt(c.human);
		assertEquals("set",((Portuguese)c.human).flag);
		
		AnimalRoles4 d = new AnimalRoles4();
		assertEquals(null,((Portuguese)d.human).flag);
		d.setIt(d.human);
		assertEquals("set",((Portuguese)d.human).flag);
		
		AnimalRoles5 e = new AnimalRoles5();
		assertEquals(null,((Portuguese)e.human).flag);
		e.setIt(e.human);
		assertEquals("set",((Portuguese)e.human).flag);
		
		AnimalRoles6 f = new AnimalRoles6();
		assertEquals(null,((Portuguese)f.human).flag);
		f.setIt(f.human);
		assertEquals("set",((Portuguese)f.human).flag);
		
		/*for(Method m :Lisboeta2.class.getMethods()){
			System.out.println(m.getName()+" "+m.getDeclaringClass());
		}*/
		
		AnimalRoles7 g = new AnimalRoles7();
		assertEquals(null,((Portuguese)g.human).flag);
		g.setIt(g.human);
		assertEquals("setextended",((Portuguese)g.human).flag);
		
		
	}
	
}
