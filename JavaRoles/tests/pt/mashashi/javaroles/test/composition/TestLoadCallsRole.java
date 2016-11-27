package pt.mashashi.javaroles.test.composition;

import pt.mashashi.javaroles.annotations.InjObjRigid;
import pt.mashashi.javaroles.annotations.ObjRole;
import pt.mashashi.javaroles.annotations.Player;
import pt.mashashi.javaroles.annotations.ThreadSafeRole;

import static org.junit.Assert.assertEquals;

import java.util.Scanner;

public class TestLoadCallsRole {
	
	interface Monkey{ String hello(); }
	
	@ThreadSafeRole
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
	
	
	
	
	
	
	@Player
	static class AnimalRoles2 implements Monkey{
		@ObjRole private Monkey monkey;
		public AnimalRoles2(Monkey monkey){
			this.monkey = monkey; 
		}
		@Override
		public String hello() { return "Default hello "+this.getClass().getName(); }
		
		public String dummy(){return "dummy";}
	}
	
	@ThreadSafeRole
	static class Gorila implements Monkey{
		private long token;
		@InjObjRigid Object obj;
		@Override 
		public String hello() {
			
			long times = Math.round(Math.random()*100);
			//System.out.println("Times: "+times);
			while(times-->0){
				long token = Math.round(Math.random()*10000);
				this.token = token;
				try {
					long milis = Math.round(Math.random()*100);
					Thread.sleep(milis);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				try{
					assertEquals(obj, this.obj);
					assertEquals(token, this.token);
				}catch(AssertionError e){
					return e.getMessage();
				}
			}
			return null; 
		} 
	}
	
	static class MyRunnable implements Runnable{
		Monkey ar;
		String failedMsg;
		public MyRunnable(Monkey ar){
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
		
		//
		
		Gorila gorila = new Gorila();
		
		final AnimalRoles2 animalRoles4 = new AnimalRoles2(gorila);
		final AnimalRoles2 animalRoles5 = new AnimalRoles2(gorila);
		final AnimalRoles2 animalRoles6 = new AnimalRoles2(gorila);
		
		MyRunnable r4 = new MyRunnable(animalRoles4);
		Thread t4 = new Thread(r4);
		t4.start();
		
		MyRunnable r5 = new MyRunnable(animalRoles5);
		Thread t5 = new Thread(r5);
		t5.start();
		
		MyRunnable r6 = new MyRunnable(animalRoles6);
		Thread t6 = new Thread(r6);
		t6.start();
		
		t4.join();
		t5.join();
		t6.join();
		
		if(r4.failedMsg!=null){
			throw new AssertionError(r4.failedMsg);
		}
		
		if(r5.failedMsg!=null){
			throw new AssertionError(r5.failedMsg);
		}
		
		if(r6.failedMsg!=null){
			throw new AssertionError(r6.failedMsg);
		}

		
	}
	
}
