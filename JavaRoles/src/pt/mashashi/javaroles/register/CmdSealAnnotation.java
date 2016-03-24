package pt.mashashi.javaroles.register;

import java.util.LinkedList;
import java.util.List;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;

public class CmdSealAnnotation implements Cmd{
	
		private static class ProcessUnit{
			
			public CtField f;
			public CtClass c;
			
			public ProcessUnit(CtField f) {
				this.f = f;
				this.c = f.getDeclaringClass();
			}
			
		} 
	
		private static List<ProcessUnit> pus = new LinkedList<>();
		
		
		private static RoleRegister roleRegister;
		private static boolean executed = false;
		
		private CmdSealAnnotation(){}
		
		public static CmdSealAnnotation neu(RoleRegister roleRegister, CtField f) {
			if(executed){
				pus.clear();
				roleRegister = null;
				executed = false;
			}
			pus.add(new ProcessUnit(f));
			CmdSealAnnotation.roleRegister = roleRegister;
			return new CmdSealAnnotation();
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
							
							if(ctClazz.subclassOf(pu.c)){
								for(CtMethod m : ctClazz.getDeclaredMethods()){
									if(Modifier.isPublic(m.getModifiers())){
										
										String code = 
										"{"+
											"if("+pu.f.getName()+"){"+
												"throw new RuntimeException(\"This class is sealed\");"+
											"}"
										+"}";
										m.insertBefore(code);
										roleRegister.classScheduler.scheduleFinalCmd(CmdCloseClass.neu(roleRegister, ctClazz));
										
									}
								}
							}
							
						}
						
					}
				} catch (CannotCompileException e) {
					throw new RuntimeException(e);
				}
				executed = true;
			}
		}
		
		
	}