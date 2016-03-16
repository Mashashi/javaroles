package pt.mashashi.javaroles.register;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import pt.mashashi.javaroles.ClassUtils;
import pt.mashashi.javaroles.annotations.sprinkles.InheritAnnots;

public class CmdExtendAnnotation implements Cmd{
	
		private static class ProcessUnit{
			
			public CtMethod m;
			public CtClass c;
			public List<Object> anots;
			
			public ProcessUnit(CtMethod m) {
				this.m = m;
				this.c = m.getDeclaringClass();
				try {
					anots = new LinkedList<>(Arrays.asList(m.getAnnotations()));
				} catch (ClassNotFoundException e) {
					throw new RuntimeException(e);
				}
				removeCmdAnnotation(anots);
			}
			
			private static void removeCmdAnnotation(List<Object> anots) {
				Iterator<Object> ite = anots.iterator();
				while(ite.hasNext()){
					Object a = ite.next();
					if(a instanceof InheritAnnots){
						ite.remove();
					}
				}
			}
		} 
	
		private static List<ProcessUnit> pus = new LinkedList<>();
		
		private static RoleRegister roleRegister;
		private static boolean executed = false;
		
		private CmdExtendAnnotation(){}
		
		public static CmdExtendAnnotation neu(RoleRegister roleRegister, CtMethod m) {
			if(executed){
				pus.clear();
				roleRegister = null;
				executed = false;
			}
			pus.add(new ProcessUnit(m));
			CmdExtendAnnotation.roleRegister = roleRegister;
			return new CmdExtendAnnotation();
		}
		
		public void cmd(){
			if(!executed){
				try {
					ClassPool pool = ClassPool.getDefault();
					for(String clazz : roleRegister.getAllClassesForPkgs()){
						CtClass ctClazz = pool.get(clazz);
						
						for(ProcessUnit pu : pus){
						
							List<CtClass> extendz = ClassUtils.extendz(pu.c,ctClazz); 
							for(CtClass extend : extendz){
								CtMethod extendMethod = extend.getDeclaredMethod(pu.m.getName(), pu.m.getParameterTypes()); 
								for(Object a: pu.anots){
									final Annotation annotType = ((Annotation)a);
									Annotation ag = (Annotation) extendMethod.getAnnotation(annotType.annotationType());
									if(ag==null){
										ClassUtils.addAnnotation(extendMethod, annotType);
										roleRegister.classScheduler.scheduleFinalCmd(CmdCloseClass.neu(extend, roleRegister.classesDir));
										//System.out.println(extendMethod.getLongName());
									}
								}
							}
							
						}
						
					}
				} catch (ClassNotFoundException | NotFoundException e) {
					throw new RuntimeException(e.getMessage());
				}
				executed = true;
			}
		}
		
	}