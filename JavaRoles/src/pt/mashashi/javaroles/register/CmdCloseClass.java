package pt.mashashi.javaroles.register;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import javassist.CannotCompileException;
import javassist.CtClass;
import pt.mashashi.javaroles.RoleBus;

public class CmdCloseClass implements Cmd{
		
		private final static List<CmdCloseClass> closes = new LinkedList<>();
		
		private CtClass clazz; 
		private String classesDir;
		private boolean executed;
		
		private CmdCloseClass(){}
		private CmdCloseClass(CtClass clazz, String classesDir) {
			this.clazz = clazz;
			this.classesDir = classesDir;
			this.executed = false;
		}
		
		public static CmdCloseClass neu(CtClass clazz, String classesDir){
			
			for(CmdCloseClass close : closes){
				if(close.clazz.equals(clazz)){
					return close;
				}
			}
			CmdCloseClass n = new CmdCloseClass(clazz, classesDir);
			closes.add(n);
			return n;
		}
		
		public void cmd(){
			if(!executed){
				String clazzName = clazz.getName();
				try {
					if(classesDir!=null){
						clazz.writeFile(classesDir);
					}
					clazz.toClass();
				} catch (CannotCompileException|IOException e) {
					Logger.getLogger(RoleBus.class.getName()).debug("error processing class: "+clazzName+" "+e.getMessage());
					e.printStackTrace();
					throw new RuntimeException();
					
				}
				executed = true;
			}
		}
	}