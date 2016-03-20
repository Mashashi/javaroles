package pt.mashashi.javaroles.register;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ClassScheduler {
	
	private List<Cmd> actual;
	private List<Cmd> next;
	private List<Cmd> finalize;
	
	public ClassScheduler(){
		
		actual = new LinkedList<>();
		next = new LinkedList<>();
		finalize = new LinkedList<>();
		
	}
	public void execInTerm(List<Cmd> l){
		List<Cmd> hideNext = new LinkedList<Cmd>(next); 
		for(Cmd c : l){
			next.clear();
			next.add(c);
			execSchedule();
		}
		next = hideNext;
	}
	public void scheduleNextCmd(Cmd cmd){
		next.add(cmd);
	}
	public void scheduleFinalCmd(Cmd cmd){
		finalize.add(cmd);
	}
	
	public void execSchedule(){
		while(next.size()!=0){
			actual = next;
			next = new LinkedList<>();
			Iterator<Cmd> ite = actual.iterator();
			while(ite.hasNext()){
				Cmd cmd = ite.next();
				cmd.cmd();
			}
		}
		actual.clear();
	}
	
	public void finalize(){
		for(Cmd f : finalize){
			f.cmd();
		}
		finalize.clear();
	}
	
}