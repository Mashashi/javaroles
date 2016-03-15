package pt.mashashi.javaroles.register;

import java.io.IOException;
import java.util.Collection;

import org.apache.log4j.Logger;

import javassist.CannotCompileException;
import javassist.CtClass;
import pt.mashashi.javaroles.RoleBus;

public class CmdCloseClasses implements Cmd{
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
				String clazzName = o.getName();
				try {
					if(classesDir!=null){
						o.writeFile(classesDir);
					}
					o.toClass();
				} catch (CannotCompileException|IOException e) {
					Logger.getLogger(RoleBus.class.getName()).debug("error processing class: "+clazzName+" "+e.getMessage());
					e.printStackTrace();
					throw new RuntimeException();
				}
			}
		}
	}