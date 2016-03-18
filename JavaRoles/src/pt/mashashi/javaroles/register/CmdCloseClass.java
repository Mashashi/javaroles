package pt.mashashi.javaroles.register;

import java.io.IOException;
import java.util.Iterator;
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
		private List<Cmd> dependencies;
		private boolean executed;
		
		private CmdCloseClass(){}
		private CmdCloseClass(CtClass clazz) {
			this.clazz = clazz;
			this.executed = false;
			this.dependencies = new LinkedList<Cmd>();
		}
		
		public static CmdCloseClass neu(RoleRegister roleRegister, CtClass clazz){
			Iterator<CmdCloseClass> i = closes.iterator();
			CmdCloseClass n = null;
			
			List<CmdCloseClass> dependencies = new LinkedList<>();
			
			findCommand: {
				while(i.hasNext()){
					CmdCloseClass close = i.next();
					
					if(close.clazz.equals(clazz)){
						n = close;
						break findCommand;
					}
					 
					if(close.clazz.subclassOf(clazz)){
						// BLOCK Write only the deeper class the other will be written on the class hierarchy to
						dependencies.add(close);
					}
				}
				n = new CmdCloseClass(clazz);
				closes.add(n);
			}
			for(CmdCloseClass d : dependencies){
				d.dependencies.add(n);
			}
			return n;
		}
		
		public void cmd(){
			if(!executed){
				
				{ // BLOCK Execute dependencies
					for(Cmd d : dependencies){
						d.cmd();
					}
				}
				
				String clazzName = clazz.getName();
				try {
					if(classesDir!=null){
						clazz.writeFile(classesDir);
					}
					clazz.toClass();
					
					// Sometimes when saving classes it happens that we want to save a class that was saved previously indirectly 
					// by saving one that extends the current.
					
				} catch (CannotCompileException|IOException e) {
					Logger.getLogger(RoleBus.class.getName()).debug("error processing class: "+clazzName+" "+e.getMessage());
					e.printStackTrace();
					throw new RuntimeException();
					
				}
				executed = true;
			}
		}
	}