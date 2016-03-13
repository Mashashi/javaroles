package pt.mashashi.javaroles.register;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import pt.mashashi.javaroles.RoleBus;

public class ClassScheduler {
	
	public static interface Command{
		public void cmd();
	};
	public static class CloseClass implements Command{
		private CtClass clazz; 
		private String classesDir;
		@SuppressWarnings("unused")
		private CloseClass(){}
		public CloseClass(CtClass clazz, String classesDir) {
			this.clazz = clazz;
			this.classesDir = classesDir;
		}
		public void cmd(){
			String clazzName = clazz.getName();
			try {
				if(classesDir!=null){
					clazz.writeFile(classesDir);
				}
				clazz.toClass();
			} catch (CannotCompileException|IOException e) {
				Logger.getLogger(RoleBus.class.getName()).debug("error processing class: "+clazzName+" "+e.getMessage());
				e.printStackTrace();
				throw new RuntimeException();
			}
		}
	}
	public static class CloseClasses implements Command{
		private Collection<CtClass> clazzes; 
		private String classesDir;
		@SuppressWarnings("unused")
		private CloseClasses(){}
		public CloseClasses(Collection<CtClass> clazzes, String classesDir) {
			this.clazzes = clazzes;
			this.classesDir = classesDir;
		}
		public void cmd(){
			for(CtClass o : clazzes){
				String clazzName = o.getName();
				try {
					if(classesDir!=null){
						o.writeFile(classesDir);
					}
					o.toClass();
				} catch (CannotCompileException|IOException e) {
					Logger.getLogger(RoleBus.class.getName()).debug("error processing class: "+clazzName+" "+e.getMessage());
					e.printStackTrace();
					throw new RuntimeException();
				}
			}
		}
	}
	
	public static class ExtendAnnotations implements Command{
		private CtMethod m;
		private CtClass c;
		private RoleRegister roleRegister;
		@SuppressWarnings("unused")
		private ExtendAnnotations(){}
		public ExtendAnnotations(RoleRegister roleRegister, CtMethod m) {
			this.m = m;
			this.c = m.getDeclaringClass();
			this.roleRegister = roleRegister;
		}
		public void cmd(){
			try {
				Object[] anots = m.getAnnotations();
				List<String> classes = roleRegister.getAllClassesForPkgs();
				ClassPool pool = ClassPool.getDefault();
				for(String clazz : classes){
					CtClass ctClazz = pool.get(clazz);
					if(isExtends(c,ctClazz)){
						System.out.println(ctClazz.getName());
					}
				}
			} catch (ClassNotFoundException | NotFoundException e) {
				throw new RuntimeException(e.getMessage());
			}
			
		}
		public boolean isExtends(CtClass clazz, CtClass possibleExtends){
			ClassPool pool = ClassPool.getDefault();
			
			if(clazz.equals(possibleExtends)) return false;
			
			
			
			try {
				final Object objectCt = pool.get(Object.class.getName());
				do{
					possibleExtends = possibleExtends.getSuperclass();
					if(clazz.equals(possibleExtends)){
						return true;
					}
				}while(!possibleExtends.equals(objectCt));
			} catch (NotFoundException e) {
				throw new RuntimeException(e);
			}
			
			return false;
		}
	}
	
	
	
	private LinkedList<Command> actual;
	private LinkedList<Command> next;
	private LinkedList<Command> finalize;
	
	public ClassScheduler(){
		actual = new LinkedList<>();
		next = new LinkedList<>();
		finalize = new LinkedList<>();
	}
	public enum Order {NEXT, FINAL};
	
	public void scheduleCommand(Command cmd, Order order){
		switch(order){
			case NEXT:
				next.add(cmd);
			break;
			case FINAL:
				finalize.add(cmd);
			break;
		}
	}
	
	
	public void executeSchedule(){
		while(next.size()!=0){
			actual = next;
			next = new LinkedList<>();
			Iterator<Command> ite = actual.iterator();
			while(ite.hasNext()){
				Command cmd = ite.next();
				cmd.cmd();
			}
		}
		actual.clear();
		for(Command f : finalize){
			f.cmd();
		}
		finalize.clear();
	}
	
}
/*
if(classesDir!=null){
	cn.writeFile(classesDir);
}
cn.toClass();
*/