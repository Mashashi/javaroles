package pt.mashashi.javaroles.register;

import javassist.CtClass;

public interface IFindCmd {
	
	public void analyze(CtClass clazz, RoleRegister roleRegister);
	
}
