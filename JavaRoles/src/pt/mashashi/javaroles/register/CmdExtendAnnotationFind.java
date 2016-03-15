package pt.mashashi.javaroles.register;

import java.util.List;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import pt.mashashi.javaroles.annotations.sprinkles.InheritAnnots;

public class CmdExtendAnnotationFind implements Cmd{
	private RoleRegister roleRegister;
	public CmdExtendAnnotationFind(RoleRegister roleRegister) {
		this.roleRegister = roleRegister;
	}
	@Override
	public void cmd() {
		try {
			List<String> classes = roleRegister.getAllClassesForPkgs();
			ClassPool pool = ClassPool.getDefault();
			for(String clazz : classes){
				CtClass ctClazz = pool.get(clazz);
				
				for(CtMethod m : ctClazz.getDeclaredMethods()){
					if(m.getAnnotation(InheritAnnots.class)!=null){
						roleRegister.classScheduler.scheduleNextCmd(CmdExtendAnnotation.neu(roleRegister, m));
					}
				}
				
			}
		} catch (NotFoundException | ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

}
