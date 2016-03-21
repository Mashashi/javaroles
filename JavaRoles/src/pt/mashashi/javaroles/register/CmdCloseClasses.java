package pt.mashashi.javaroles.register;

import java.io.IOException;
import java.util.Collection;

import javassist.CannotCompileException;
import javassist.CtClass;

/**
 * 
 * WARNING: this class doesn't have implement a planner for saving class hierarchies if a subclass is saved first and later on
 * a super class is saved a exception will be thrown stating duplicate.
 * 
 * To use the planner instead of using this cmd use the one that acts on a single class.
 * 
 * @author Rafael
 */
public class CmdCloseClasses implements ICmd{
		private Collection<CtClass> clazzes; 
		private String classesDir;
		@SuppressWarnings("unused")
		private CmdCloseClasses(){}
		public CmdCloseClasses(Collection<CtClass> clazzes, String classesDir) {
			this.clazzes = clazzes;
			this.classesDir = classesDir;
		}
		public void cmd(){
			for(CtClass o : clazzes){
				try {
					if(classesDir!=null){
						o.writeFile(classesDir);
					}
					o.toClass();
				} catch (CannotCompileException|IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}