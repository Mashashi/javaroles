package pt.mashashi.javaroles.register;

import java.util.LinkedList;
import java.util.List;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import pt.mashashi.javaroles.ClassUtils;

public class CmdSuperAnnotation implements Cmd{
	
		private static class ProcessUnit{
			
			public CtMethod m;
			public CtClass c;
			
			public ProcessUnit(CtMethod m) {
				this.m = m;
				this.c = m.getDeclaringClass();
			}
			
		} 
	
		private static List<ProcessUnit> pus = new LinkedList<>();
		private static List<String> methodsProcessed = new LinkedList<>();
		
		private static RoleRegister roleRegister;
		private static boolean executed = false;
		
		private CmdSuperAnnotation(){}
		
		public static CmdSuperAnnotation neu(RoleRegister roleRegister, CtMethod m) {
			if(executed){
				pus.clear();
				methodsProcessed.clear();
				roleRegister = null;
				executed = false;
			}
			pus.add(new ProcessUnit(m));
			CmdSuperAnnotation.roleRegister = roleRegister;
			return new CmdSuperAnnotation();
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
								if(!methodsProcessed.contains(extendMethod.getLongName())){
									methodsProcessed.add(extendMethod.getLongName());
									String code = 
									"{"+
										ClassUtils.getMethodCall("super."+extendMethod.getName(), extendMethod.getParameterTypes(), "$args")
									+"}";
									extendMethod.insertBefore(code);
								}
							}
							
						}
						
					}
				} catch (NotFoundException | CannotCompileException e) {
					throw new RuntimeException(e.getMessage());
				}
				executed = true;
			}
		}
		
		
	}