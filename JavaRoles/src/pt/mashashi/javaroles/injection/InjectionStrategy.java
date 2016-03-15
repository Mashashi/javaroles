package pt.mashashi.javaroles.injection;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.apache.commons.lang3.reflect.FieldUtils;

import pt.mashashi.javaroles.ClassUtils;
import pt.mashashi.javaroles.annotations.InjObjRigidPos;

public abstract class InjectionStrategy {
	
	protected String all;
	protected String params;
	
	
	
	InjectionStrategy(){
		
		{
			StringBuffer injectionCode = new StringBuffer("");
			
			injectionCode.append(Field.class.getName()+"[] fs=this.getClass().getFields();");
			injectionCode.append("for(int i=0;i<fs.length;i++){");
			
				injectionCode.append("Object o = "+FieldUtils.class.getName()+".readField(fs[i], this, true);");
				injectionCode.append("if(o!=null){");
				
					referenceSetCode(injectionCode);
					getCallbackInvokeCode(injectionCode);
				
				injectionCode.append("}");
			injectionCode.append("}");
			
			// OLDFEAT - Instantiate roles automatically
			/*boolean instanciateObjRoles = c.getAnnotation(DefaultInitObjRole.class)!=null;
			if(instanciateObjRoles){
				injectionCode.append(Field.class.getName()+"[] fs=this.getClass().getFields();");
				injectionCode.append("for(int i=0;i<fs.length;i++){");
					injectionCode.append("Object o = "+FieldUtils.class.getName()+".readField(fs[i], this, true);");
					injectionCode.append("if(o==null){");
					
						injectionCode.append(Class.class.getName()+" c = Class.forName(o.getClass().getName());");
						injectionCode.append(Constructor.class.getName()+" c = FileUtils.class.getConstructor(this.getClass());");
						injectionCode.append("fs[i].set(this, c.newInstance(this));");
						
	//						injectionCode.append("try{");
	//							injectionCode.append("fs[i].set(this, c.newInstance(this));");
	//						injectionCode.append("}catch("+IllegalArgumentException.class.getName()+" e){");
	//								injectionCode.append("try{");
	//									injectionCode.append("fs[i].set(this, c.newInstance());");
	//								injectionCode.append("}catch("+IllegalArgumentException.class.getName()+" e){");	
	//						injectionCode.append("}");
						
					injectionCode.append("}");
				injectionCode.append("}");		
			}*/
			
			all = injectionCode.toString();
		}
		
		{ // Methods
			StringBuffer injectionCode = new StringBuffer("");
			injectionCode.append(Object.class.getName()+"[] fs=$args;");
			
			//injectionCode.append("System.out.println($args[0]);");
			
			injectionCode.append("for(int i=0;i<fs.length;i++){");
			
				injectionCode.append("Object o = fs[i];");
				injectionCode.append("if(o!=null){");
				
					referenceSetCode(injectionCode);
					getCallbackInvokeCode(injectionCode);
					
				injectionCode.append("}");
			injectionCode.append("}");
			
			params = injectionCode.toString(); 
		}
		
	}
	
	
	protected abstract void referenceSetCode(StringBuffer injectionCode);
	
	
	
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
	
	protected void getCallbackInvokeCode(StringBuffer injectionCode){
		injectionCode.append(List.class.getName()+" l = "+ClassUtils.class.getName()+".getListMethodAnotated(");
			injectionCode.append("o.getClass(), "+InjObjRigidPos.class.getName()+".class");
		injectionCode.append(");");
		
		injectionCode.append("for(int i=0;i<l.size();i++){");
		
			injectionCode.append(Method.class.getName()+" m = (("+Method.class.getName()+")l.get(i));");
			
			
			
			injectionCode.append("boolean isAccessible = m.isAccessible();");
			injectionCode.append("m.setAccessible(true);");
			
			injectionCode.append("try{");
				injectionCode.append("m.invoke(o, new Object[]{this});");
			injectionCode.append("}catch("+InvocationTargetException.class.getName()+" e){");
				//injectionCode.append("//Do nothing - This method is just not compatible");
				//injectionCode.append("e.printStackTrace();");
				injectionCode.append("throw e.getCause();");
			injectionCode.append("}catch("+IllegalArgumentException.class.getName()+" e){");
				//injectionCode.append("//Do nothing - This method is not accessible");
				//injectionCode.append("e.printStackTrace();");
			injectionCode.append("}finally{");
				injectionCode.append("m.setAccessible(isAccessible);");
			injectionCode.append("}");
			
		injectionCode.append("}");
	}
	
	
}
