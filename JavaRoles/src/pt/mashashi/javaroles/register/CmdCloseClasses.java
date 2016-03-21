package pt.mashashi.javaroles.register;

import java.io.IOException;
import java.util.Collection;

import javassist.CannotCompileException;
import javassist.CtClass;

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