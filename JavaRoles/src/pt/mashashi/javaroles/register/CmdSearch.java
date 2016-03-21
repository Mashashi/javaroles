package pt.mashashi.javaroles.register;

import java.util.LinkedList;
import java.util.List;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

public class CmdSearch implements ICmd{
	private RoleRegister roleRegister;
	private List<IFindCmd> finders;
	
	public CmdSearch(RoleRegister roleRegister) {
		this.roleRegister = roleRegister;
		this.finders = new LinkedList<>();
	}
	public void addFinder(IFindCmd finder){
		this.finders.add(finder);
	}
	
	@Override
	public void cmd() {
		if(finders.size()!=0){
			try {
				
				ClassPool pool = ClassPool.getDefault();
				for(String clazz : roleRegister.getAllClassesForPkgs()){
					CtClass ctClazz = pool.get(clazz);
					for(IFindCmd f : finders){
						f.analyze(ctClazz, roleRegister);
					}	
				}
				
			} catch (NotFoundException e) {
				throw new RuntimeException(e);
			}
		}
	}

}
