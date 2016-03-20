package pt.mashashi.javaroles.register;

import java.util.LinkedList;

import pt.mashashi.javaroles.injection.InjectionStrategy;
import pt.mashashi.javaroles.register.RoleRegister.MatchType;

public class RoleRegisterAssembler {
	
	private RoleRegister rr;
	
	public RoleRegisterAssembler(RoleRegister roleRegister){
		this.rr = roleRegister;
		rr.injRigStrategy = InjectionStrategy.getInstanceSingle();
	}
	
	public RoleRegisterAssembler setMatchType(MatchType matchT){
		if(rr.pkgs!=null){
			for(String pkg : rr.pkgs){ 
				rr.matchType.put(pkg, matchT); 
			}
		}
		if(rr.onlyFor!=null){
			for(String clazz : rr.onlyFor){ 
				rr.matchType.put(clazz, matchT); 
			}
		}
		return this;
	}
	
	public RoleRegisterAssembler writeClasses(String dir){
		rr.classesDir = dir;
		return this;
	}
	
	public RoleRegisterAssembler setRigidInjectionStrategy(InjectionStrategy injRigStrategy){
		rr.injRigStrategy = injRigStrategy;
		return this;
	}
	
	public RoleRegisterAssembler includeGiven(Class<?>... clazzes){
		//this.onlyFor = new LinkedList<String>();
		if(rr.onlyFor==null){
			rr.onlyFor = new LinkedList<>();
		}
		for(Class<?> clazz : clazzes){
			rr.onlyFor.add(clazz.getName());
		}
		setMatchType(MatchType.STARTS_WITH);
		return this;
	}
	
	/**
	 * Sometimes when using classes some problems may arise to to the class loader. When so try to use a string identifying the
	 * class to workaround the problem.
	 * 
	 * @param clazzes
	 * @return
	 */
	public RoleRegisterAssembler includeGivenRaw(String... clazzes){
		//this.onlyFor = new LinkedList<String>();
		if(rr.onlyFor==null){
			rr.onlyFor = new LinkedList<>();
		}
		for(String clazz : clazzes){
			rr.onlyFor.add(clazz);
		}
		setMatchType(MatchType.STARTS_WITH);
		return this;
	}
	
	public RoleRegisterAssembler excludeGiven(Class<?>... clazzes){	
		//excludeGiven.clear();
		if(clazzes!=null){
			for(Class<?> clazz : clazzes){
				rr.excludeGiven.add(clazz.getName());
			}
		}
		return this;
	}
	
	public RoleRegisterAssembler excludeGivenRaw(String... clazzes){	
		//excludeGiven.clear();
		if(clazzes!=null){
			for(String clazz : clazzes){
				rr.excludeGiven.add(clazz);
			}
		}
		return this;
	}
	
	public RoleRegisterAssembler includeGivenPkg(Class<?>... pkgs){	
		if(pkgs!=null){
			rr.pkgs = new LinkedList<>();
			for(Class<?> pkg : pkgs){
				rr.pkgs.add(pkg.getPackage().getName());
			}
		}
		setMatchType(MatchType.STARTS_WITH);
		return this;
	}
	
	public RoleRegisterAssembler excludeGivenPkg(Class<?>... pkgs){	
		//excludeGiven.clear();
		if(pkgs!=null){
			for(Class<?> pkg : pkgs){
				rr.excludeGiven.add(pkg.getPackage().getName());
			}
		}
		return this;
	}
	
	public RoleRegisterAssembler inheritAnnots(){
		rr.execInTermBeforeRegister.add(new CmdExtendAnnotationFind(rr));
		return this;
	}
	
	public RoleRegisterAssembler callSuperAnnots(){
		rr.execInTermBeforeRegister.add(new CmdSuperAnnotationFind(rr));
		return this;
	}
	
	public RoleRegisterAssembler sealClasses(){
		rr.execInTermBeforeRegister.add(new CmdSealAnnotationFind(rr));
		return this;
	}
	
	public RoleRegister get(){
		{ // TODO This code is not elegant
			if(rr.pkgs==null || rr.pkgs.size()==0){ 
				//this.pkgs = new String[0]; 
				rr.pkgs = new LinkedList<String>();
				rr.pkgs.add("");
			}
			if(rr.pkgs.size()==0){ 
				throw new IllegalArgumentException("Supply at least one package perfix."); 
			}
			//setMatchType(MatchType.STARTS_WITH);
		}
		
		if(rr.onlyFor!=null){ // BLOCK Remove classes to exclude
			rr.computedOnlyFor.addAll(rr.onlyFor);
			for(String o : rr.onlyFor){
				for(String e : rr.excludeGiven){
					if(o.startsWith(e)){
						 rr.computedOnlyFor.remove(o);
					}
				}
			}
		}
		
		return rr;
	}
}
