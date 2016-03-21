package pt.mashashi.javaroles.register;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ClassScheduler {
	
	private List<ICmd> actual;
	private List<ICmd> next;
	private List<ICmd> finalize;
	
	public ClassScheduler(){
		
		actual = new LinkedList<>();
		next = new LinkedList<>();
		finalize = new LinkedList<>();
		
	}
	/*public void execInTerm(List<Cmd> l){
		List<Cmd> hideNext = new LinkedList<Cmd>(next); 
		for(Cmd c : l){
			next.clear();
			next.add(c);
			execSchedule();
		}
		next = hideNext;
	}*/
	public void scheduleNextCmd(ICmd cmd){
		next.add(cmd);
	}
	public void scheduleFinalCmd(ICmd cmd){
		finalize.add(cmd);
	}
	
	public void execSchedule(){
		while(next.size()!=0){
			actual = next;
			next = new LinkedList<>();
			Iterator<ICmd> ite = actual.iterator();
			while(ite.hasNext()){
				ICmd cmd = ite.next();
				cmd.cmd();
			}
		}
		actual.clear();
	}
	
	public void finalize(){
		for(ICmd f : finalize){
			f.cmd();
		}
		finalize.clear();
	}
	
}