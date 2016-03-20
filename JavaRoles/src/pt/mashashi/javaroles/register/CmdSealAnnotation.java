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
import pt.mashashi.javaroles.annotations.sprinkles.Seal;

public class CmdSealAnnotation implements Cmd{
	
		private static class ProcessUnit{
			
			public CtField f;
			public CtClass c;
			public Seal s;
			
			public ProcessUnit(CtField f, Seal s) {
				this.f = f;
				this.c = f.getDeclaringClass();
				this.s = s;
			}
			
		} 
	
		private static List<ProcessUnit> pus = new LinkedList<>();
		
		
		private static RoleRegister roleRegister;
		private static boolean executed = false;
		
		private CmdSealAnnotation(){}
		
		public static CmdSealAnnotation neu(RoleRegister roleRegister, CtField f, Seal s) {
			if(executed){
				pus.clear();
				roleRegister = null;
				executed = false;
			}
			pus.add(new ProcessUnit(f, s));
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
												"throw new RuntimeException(\""+pu.s.msgSeal()+"\");"+
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