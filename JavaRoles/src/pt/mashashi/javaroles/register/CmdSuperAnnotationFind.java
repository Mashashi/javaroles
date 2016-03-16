package pt.mashashi.javaroles.register;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import pt.mashashi.javaroles.annotations.sprinkles.CallSuper;

public class CmdSuperAnnotationFind implements Cmd{
	private RoleRegister roleRegister;
	public CmdSuperAnnotationFind(RoleRegister roleRegister) {
		this.roleRegister = roleRegister;
	}
	@Override
	public void cmd() {
		try {
			ClassPool pool = ClassPool.getDefault();
			for(String clazz : roleRegister.getAllClassesForPkgs()){
				CtClass ctClazz = pool.get(clazz);
				
				for(CtMethod m : ctClazz.getDeclaredMethods()){
					if(m.getAnnotation(CallSuper.class)!=null){
						roleRegister.classScheduler.scheduleNextCmd(CmdSuperAnnotation.neu(roleRegister, m));
					}
				}
				
			}
		} catch (NotFoundException | ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

}
