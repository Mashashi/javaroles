package pt.mashashi.javaroles.register;

import java.util.LinkedList;

import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import pt.mashashi.javaroles.annotations.sprinkles.CallSuper;
import pt.mashashi.javaroles.annotations.sprinkles.InheritAnnots;
import pt.mashashi.javaroles.annotations.sprinkles.NotNullParams;
import pt.mashashi.javaroles.annotations.sprinkles.Seal;
import pt.mashashi.javaroles.injection.InjectionStrategy;

/**
 * Encapsulates the algorithm methods for constructing a role register.
 * 
 * @author Rafael
 *
 */
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
		rr.cmdSearch.addFinder(new IFindCmd() {
			@Override
			public void analyze(CtClass clazz, RoleRegister roleRegister) {
				try {
					for(CtMethod m : clazz.getDeclaredMethods()){
						if(m.getAnnotation(InheritAnnots.class)!=null){
							roleRegister.classScheduler.scheduleNextCmd(CmdExtendAnnotation.neu(roleRegister, m));
						}					
					}
				} catch (ClassNotFoundException e) {
					throw new RuntimeException(e);
				}
			}
		});
		return this;
	}
	
	public RoleRegisterAssembler callSuperAnnots(){
		rr.cmdSearch.addFinder(new IFindCmd() {
			@Override
			public void analyze(CtClass clazz, RoleRegister roleRegister) {
				try {
					for(CtMethod m : clazz.getDeclaredMethods()){
						if(m.getAnnotation(CallSuper.class)!=null){
							roleRegister.classScheduler.scheduleNextCmd(CmdSuperAnnotation.neu(roleRegister, m));
						}
					}
				} catch (ClassNotFoundException e) {
					throw new RuntimeException(e);
				}
			}
		});
		return this;
	}
	
	public RoleRegisterAssembler sealClasses(){
		rr.cmdSearch.addFinder(new IFindCmd() {
			@Override
			public void analyze(CtClass clazz, RoleRegister roleRegister) {
				try {
					for(CtField f : clazz.getDeclaredFields()){
						Seal s = (Seal) f.getAnnotation(Seal.class);
						if(s!=null){
							roleRegister.classScheduler.scheduleNextCmd(CmdSealAnnotation.neu(roleRegister, f,  s));
						}
					}
				} catch (ClassNotFoundException e) {
					throw new RuntimeException(e);
				}
			}
		});
		return this;
	}
	
	public RoleRegisterAssembler notNullParams(){
		rr.cmdSearch.addFinder(new IFindCmd() {
			@Override
			public void analyze(CtClass clazz, RoleRegister roleRegister) {
				try {
					NotNullParams s = (NotNullParams) clazz.getAnnotation(NotNullParams.class);
					if(s!=null){
						roleRegister.classScheduler.scheduleNextCmd(CmdNotNullParams.neu(roleRegister, clazz,  s));
					}
				} catch (ClassNotFoundException e) {
					throw new RuntimeException(e);
				}
			}
		});
		return this;
	}
	
	/**
	 * After this method is called the object is considered built and calls to this same object 
	 * will fail by throwing a null pointer exception.
	 * 
	 * @return The built role register
	 */
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
		
		rr.built = true;
		RoleRegister ret = rr;
		rr = null;
		
		return ret;
	}
}
