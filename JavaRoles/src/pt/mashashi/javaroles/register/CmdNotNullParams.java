package pt.mashashi.javaroles.register;

import java.util.LinkedList;
import java.util.List;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.NotFoundException;
import pt.mashashi.javaroles.annotations.sprinkles.NotNullParams;

/**
 * Applies to every constructor and method of the class to which is applied and subclasses on the reach.
 * 
 * @author Rafael
 *
 */
public class CmdNotNullParams implements ICmd{
		
		private static class ProcessUnit{
			
			public CtClass c;
			@SuppressWarnings("unused")
			public NotNullParams nnp;
			
			public ProcessUnit(CtClass c, NotNullParams nnp) {
				this.c = c;
				this.nnp = nnp;
			}
			
		} 
	
		private static List<ProcessUnit> pus = new LinkedList<>();
		
		private static RoleRegister roleRegister;
		private static boolean executed = false;
		
		private CmdNotNullParams(){}
		
		public static CmdNotNullParams neu(RoleRegister roleRegister, CtClass f, NotNullParams nnp) {
			if(executed){
				pus.clear();
				roleRegister = null;
				executed = false;
			}
			pus.add(new ProcessUnit(f, nnp));
			CmdNotNullParams.roleRegister = roleRegister;
			return new CmdNotNullParams();
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
									
									String code = 
											"{"+
												"for(int i=0;i<$args.length;i++){"+
													"Object arg = $args[i];"+
													"if(arg==null){"+
														"throw new "+RuntimeException.class.getName()+"(\"Param number \"+i+\" on method "+m.getLongName()+" can not be null.\");"+
													"}"+
												"}"+
											"}";
									m.insertBefore(code);
									roleRegister.classScheduler.scheduleFinalCmd(CmdCloseClass.neu(roleRegister, ctClazz));
									
								}
								
								for(CtConstructor c : ctClazz.getDeclaredConstructors()){
									String code = 
											"{"+
												"for(int i=0;i<$args.length;i++){"+
													"Object arg = $args[i];"+
													"if(arg==null){"+
														"throw new "+RuntimeException.class.getName()+"(\"Param number \"+i+\" on method "+c.getLongName()+" can not be null.\");"+
													"}"+
												"}"+
											"}";
									c.insertBeforeBody(code);
									roleRegister.classScheduler.scheduleFinalCmd(CmdCloseClass.neu(roleRegister, ctClazz));
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