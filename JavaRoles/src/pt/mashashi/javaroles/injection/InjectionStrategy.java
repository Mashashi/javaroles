package pt.mashashi.javaroles.injection;

public abstract class InjectionStrategy {
	
	public abstract String posConstructor();
	
	private static InjectionStrategySingle instanceSingle = new InjectionStrategySingle();
	private static InjectionStrategyMultiple instanceMultiple = new InjectionStrategyMultiple();
	
	public static InjectionStrategy getInstanceSingle(){
		return instanceSingle;
	}
	
	public static InjectionStrategy getInstanceMultiple(){
		return instanceMultiple;
	}
	
}
