package pt.mashashi.javaroles.test.composition;

import pt.mashashi.javaroles.annotations.InjObjRigid;
import pt.mashashi.javaroles.annotations.ObjRole;
import pt.mashashi.javaroles.annotations.Player;
import static org.junit.Assert.assertEquals;

import java.util.Scanner;

public class TestLoadCallsStaticRole {
	
	interface Monkey{ String hello(); }
	
	static class Bonobo implements Monkey{
		@InjObjRigid Object obj;
		@Override 
		public String hello() {
			Object obj = this.obj;
			long times = Math.round(Math.random()*100);
			//System.out.println("Times: "+times);
			while(times-->0){
				try {
					long milis = Math.round(Math.random()*100);
					Thread.sleep(milis);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				try{
					assertEquals(obj, this.obj);
				}catch(AssertionError e){
					return e.getMessage();
				}
			}
			return null; 
		} 
	}
	
	
	
	@Player
	static class AnimalRoles implements Monkey{
		@ObjRole private static Monkey monkey;
		public AnimalRoles(Monkey monkey){
			this.monkey = monkey; 
		}
		@Override
		public String hello() { return "Default hello "+this.getClass().getName(); }
		
		public String dummy(){return "dummy";}
	}
	
	
	static class MyRunnable implements Runnable{
		AnimalRoles ar;
		String failedMsg;
		public MyRunnable(AnimalRoles ar){
			this.ar = ar;
		}
		@Override
		public void run() {
			failedMsg = ar.hello();
		}
	}
	public static void test() throws InterruptedException{
		
		Bonobo bonobo = new Bonobo();
		
		final AnimalRoles animalRoles1 = new AnimalRoles(bonobo);
		final AnimalRoles animalRoles2 = new AnimalRoles(bonobo);
		final AnimalRoles animalRoles3 = new AnimalRoles(bonobo);
		
		MyRunnable r1 = new MyRunnable(animalRoles1);
		Thread t1 = new Thread(r1);
		/*t1.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				System.out.println("hey");
			}
		});*/
		t1.start();
		
		MyRunnable r2 = new MyRunnable(animalRoles2);
		Thread t2 = new Thread(r2);
		t2.start();
		
		MyRunnable r3 = new MyRunnable(animalRoles3);
		Thread t3 = new Thread(r3);
		t3.start();
		
		t1.join();
		t2.join();
		t3.join();
		
		if(r1.failedMsg!=null){
			throw new AssertionError(r1.failedMsg);
		}
		
		if(r2.failedMsg!=null){
			throw new AssertionError(r2.failedMsg);
		}
		
		if(r3.failedMsg!=null){
			throw new AssertionError(r3.failedMsg);
		}
		
		/*while(true){
			new Scanner(System.in).nextLine();
			System.out.println(t1.getState());
			t1.start();
		}*/
		
		
	}
	
}
