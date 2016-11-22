package pt.mashashi.javaroles.injection;

import java.lang.reflect.Field;
import java.util.List;

import org.apache.commons.lang3.reflect.FieldUtils;

import pt.mashashi.javaroles.ClassUtils;
import pt.mashashi.javaroles.annotations.InjObjRigid;

public class InjectionStrategySingle extends InjectionStrategy {
	
	InjectionStrategySingle() {}
	
	@Override
	protected void referenceSetCode(StringBuffer injectionCode) {
		
		/*
		injectionCode.append(List.class.getName()+" l = "+ClassUtils.class.getName()+".getListFieldAnnotated(");
			injectionCode.append("o.getClass(), "+InjObjRigid.class.getName()+".class");
		injectionCode.append(");");
		injectionCode.append("boolean setIt = true;");
		
		injectionCode.append("for(int i2=0;i2<l.size() && setIt;i2++){");
		
			
			injectionCode.append(Field.class.getName()+" f = (("+Field.class.getName()+")l.get(i2));");
			
			injectionCode.append("Object of = "+FieldUtils.class.getName()+".readField(f, o, true);");
			injectionCode.append("if(of==null){");
			
				injectionCode.append("boolean accesibilityOriginal = f.isAccessible();");
				injectionCode.append("f.setAccessible(true);");
				//injectionCode.append("String rigidName = f.getName();");//
				injectionCode.append("try{");
					injectionCode.append("f.set(o, this);");
					injectionCode.append("setIt = false;");
					//injectionCode.append("System.out.println(\"-->\"+rigidName);");//
				injectionCode.append("}catch("+IllegalArgumentException.class.getName()+" e){");
					//Do nothing - Just a cast error
				injectionCode.append("}finally{");
					injectionCode.append("f.setAccessible(accesibilityOriginal);");
				injectionCode.append("}");
				
			injectionCode.append("}");
			
		injectionCode.append("}");
		
		*/
		
		injectionCode.append(InjectionStrategySingle.class.getName()+".staticDoIt(o, this, false);");
		
	}
	
	public static void staticDoIt(Object o, Object thiz, boolean rewrite) throws ClassNotFoundException, IllegalAccessException{
		List<Field> l = ClassUtils.getListFieldAnnotated(o.getClass(), InjObjRigid.class);
		boolean setIt = true;
		for(int i2=0;i2<l.size() && setIt;i2++){
			Field f = l.get(i2);
			Object of = FieldUtils.readField(f, o, true);
			if(of==null || rewrite){
				boolean accesibilityOriginal = f.isAccessible();
				f.setAccessible(true);
				try{
					f.set(o, thiz);
					setIt = false;
					//System.out.println(\"-->\"+rigidName);
				}catch(IllegalArgumentException e){
					/*Do nothing - Just a cast error*/
				}finally{
					f.setAccessible(accesibilityOriginal);
				}
			}
		}

	}
	
	@Override
	public void doIt(Object o, Object thiz, boolean rewrite) throws ClassNotFoundException, IllegalAccessException{
		staticDoIt(o, thiz, rewrite);
	}
	
}
