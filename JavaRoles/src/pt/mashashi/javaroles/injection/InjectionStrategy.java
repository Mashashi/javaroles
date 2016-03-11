package pt.mashashi.javaroles.injection;

public abstract class InjectionStrategy {
	
	protected String all;
	protected String params;
	
	public String setAll() {
		return all;
	}
	
	public String setParams() {
		return params;
	}
	
	private static InjectionStrategySingle instanceSingle = new InjectionStrategySingle();
	private static InjectionStrategyMultiple instanceMultiple = new InjectionStrategyMultiple();
	
	public static InjectionStrategy getInstanceSingle(){
		return instanceSingle;
	}
	
	public static InjectionStrategy getInstanceMultiple(){
		return instanceMultiple;
	}
	
}
