package pt.mashashi.javaroles.register;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import pt.mashashi.javaroles.ClassUtils;

public class CmdExtendAnnotation implements ICmd{
	
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
				//removeCmdAnnotation(anots);
			}
			
			/*private static void removeCmdAnnotation(List<Object> anots) {
				Iterator<Object> ite = anots.iterator();
				while(ite.hasNext()){
					Object a = ite.next();
					if(a instanceof InheritAnnots){
						ite.remove();
					}
				}
			}*/
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
						
						CtClass ctClazz = null;
						
						try {
							ctClazz = pool.get(clazz);
						} catch (NotFoundException e) {
							throw new RuntimeException(e);
						}
						
						for(ProcessUnit pu : pus){
							
							/*
							List<CtClass> extendz = ClassUtils.extendz(pu.c,ctClazz); 
							for(CtClass extend : extendz){
								try{
									CtMethod extendMethod = extend.getDeclaredMethod(pu.m.getName(), pu.m.getParameterTypes()); 
									for(Object a: pu.anots){
										final Annotation annotType = ((Annotation)a);
										Annotation ag = (Annotation) extendMethod.getAnnotation(annotType.annotationType());
										if(ag==null){
											ClassUtils.addAnnotation(extendMethod, annotType);
											roleRegister.classScheduler.scheduleFinalCmd(CmdCloseClass.neu(roleRegister, extend));
											//System.out.println(extendMethod.getLongName());
											//System.out.println("true");
										}
									}
								}catch(NotFoundException e){
									// Normal method not found in the class beacause it was not overriden
								}
								System.out.println("-->"+ctClazz.getName());
							}*/
							
							if(ctClazz.subclassOf(pu.c) && !ctClazz.equals(pu.c)){
								try{
									for(Object a: pu.anots){
										CtMethod extendMethod = ctClazz.getDeclaredMethod(pu.m.getName(), pu.m.getParameterTypes());
										final Annotation annotType = ((Annotation)a);
										Annotation ag = (Annotation) extendMethod.getAnnotation(annotType.annotationType());
										if(ag==null){
											ClassUtils.addAnnotation(extendMethod, annotType, null);
											roleRegister.classScheduler.scheduleFinalCmd(CmdCloseClass.neu(roleRegister, ctClazz));
										}
									}
								}catch(NotFoundException e){
									// Normal method not found in the class beacause it was not overriden
								}
							}
							
						}
						
					}
				} catch (ClassNotFoundException e) {
					throw new RuntimeException(e);
				}
				executed = true;
			}
		}
		
	}