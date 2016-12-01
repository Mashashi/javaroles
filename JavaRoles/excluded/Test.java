package pt.mashashi.javaroles.test;

public class Test {
	
	public enum Type{ RELAXED, AGGRESSIVE }
	
	public static boolean doTest(Type testType){
		Type value = null;
		
		try{
			value = Type.valueOf(System.getProperty("testType"));
		}catch(IllegalArgumentException|NullPointerException e){
			value = Type.AGGRESSIVE;
		}
		
		return testType.equals(value);
	}
	
}
