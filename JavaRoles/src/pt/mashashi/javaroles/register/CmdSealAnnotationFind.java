package pt.mashashi.javaroles.register;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.NotFoundException;
import pt.mashashi.javaroles.annotations.sprinkles.Seal;

public class CmdSealAnnotationFind implements Cmd{
	private RoleRegister roleRegister;
	public CmdSealAnnotationFind(RoleRegister roleRegister) {
		this.roleRegister = roleRegister;
	}
	@Override
	public void cmd() {
		try {
			ClassPool pool = ClassPool.getDefault();
			for(String clazz : roleRegister.getAllClassesForPkgs()){
				CtClass ctClazz = pool.get(clazz);
				
				for(CtField f : ctClazz.getDeclaredFields()){
					Seal s = (Seal) f.getAnnotation(Seal.class);
					if(s!=null){
						roleRegister.classScheduler.scheduleNextCmd(CmdSealAnnotation.neu(roleRegister, f,  s));
					}
				}
				
			}
			
		} catch (NotFoundException | ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

}
