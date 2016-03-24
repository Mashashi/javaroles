package pt.mashashi.javaroles.register;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


import javassist.CannotCompileException;
import javassist.CtClass;

public class CmdCloseClass implements Cmd{
		
		private final static List<CmdCloseClass> closes = new LinkedList<>();
		private static RoleRegister roleRegister;
		
		private CtClass clazz;
		private Set<Cmd> dependencies;
		private boolean executed;
		
		private CmdCloseClass(){}
		private CmdCloseClass(CtClass clazz) {
			this.clazz = clazz;
			this.executed = false;
			this.dependencies = new HashSet<Cmd>();
		}
		
		public static CmdCloseClass neu(RoleRegister roleRegister, CtClass clazz){
			CmdCloseClass.roleRegister = roleRegister;
			
			Iterator<CmdCloseClass> i = closes.iterator();
			CmdCloseClass n = null;
			
			List<CmdCloseClass> dependencies = new LinkedList<>();
			List<CmdCloseClass> depends = new LinkedList<>();
			
			findCommand: {
				while(i.hasNext()){
					CmdCloseClass close = i.next();
					
					if(close.clazz.equals(clazz)){
						n = close;
						break findCommand;
					}
					 
					if(close.clazz.subclassOf(clazz)){
						// BLOCK Write only the deeper class the other will be written on the class hierarchy to
						//System.out.println("first: "+clazz.getName()+" after:"+close.clazz.getName());
						dependencies.add(close);
					}else{
						depends.add(close);
					}
				}
				n = new CmdCloseClass(clazz);
				closes.add(n);
			}
			for(CmdCloseClass d : dependencies){
				d.dependencies.add(n);
			}
			for(CmdCloseClass d : depends){
				n.dependencies.add(d);
			}
			return n;
		}
		
		public void cmd(){
			if(!executed){
				
				execDependencies();
				
				//System.out.println(clazz.getName());
				
				try {
					if(roleRegister.classesDir!=null){
						clazz.writeFile(roleRegister.classesDir);
					}
					clazz.toClass();
					
					// Sometimes when saving classes it happens that we want to save a class that was saved previously indirectly 
					// by saving one that extends the current.
					
				} catch (CannotCompileException|IOException e) {
					throw new RuntimeException(e);
				}
				executed = true;
			}
		}
		
		private void execDependencies(){
			for(Cmd d : dependencies){
				d.cmd();
			}
		}
		
	}