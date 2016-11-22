package pt.mashashi.javaroles.injection;

import java.lang.reflect.Field;
import java.util.List;

import pt.mashashi.javaroles.ClassUtils;
import pt.mashashi.javaroles.annotations.InjObjRigid;

public class InjectionStrategyMultiple extends InjectionStrategy {
	
	InjectionStrategyMultiple() {}

	@Override
	protected void referenceSetCode(StringBuffer injectionCode) {
		/*injectionCode.append(List.class.getName()+" l = "+ClassUtils.class.getName()+".getListFieldAnnotated(");
			injectionCode.append("o.getClass(), "+InjObjRigid.class.getName()+".class");
		injectionCode.append(");");
		injectionCode.append("for(int i2=0;i2<l.size();i2++){");
			
			injectionCode.append(Field.class.getName()+" f = (("+Field.class.getName()+")l.get(i2));");
			
				injectionCode.append("boolean accesibilityOriginal = f.isAccessible();");
				injectionCode.append("f.setAccessible(true);");
				//injectionCode.append("String rigidName = f.getName();");//
				injectionCode.append("try{");
					injectionCode.append("f.set(o, this);");
					//injectionCode.append("System.out.println(\"-->\"+rigidName);");//
				injectionCode.append("}catch("+IllegalArgumentException.class.getName()+" e){");
					//Do nothing - Just a cast error
				injectionCode.append("}finally{");
					injectionCode.append("f.setAccessible(accesibilityOriginal);");
				injectionCode.append("}");
			
		injectionCode.append("}");*/
		injectionCode.append(InjectionStrategyMultiple.class.getName()+".staticDoIt(o, this, false);");
	}
	
	public static void staticDoIt(Object o, Object thiz, boolean rewrite) throws ClassNotFoundException, IllegalAccessException{
		List<Field> l = ClassUtils.getListFieldAnnotated(o.getClass(), InjObjRigid.class);
		for(int i2=0;i2<l.size();i2++){
			
			Field f = l.get(i2);
			
			boolean accesibilityOriginal = f.isAccessible();
			f.setAccessible(true);
			//"String rigidName = f.getName();")
			try{
				f.set(o, thiz);
				//"System.out.println(\"-->\"+rigidName);")
			}catch(IllegalArgumentException e){
				/*Do nothing - Just a cast error*/
			}finally{
				f.setAccessible(accesibilityOriginal);
			}

		}
	}
	
	@Override
	public void doIt(Object o, Object thiz, boolean rewrite) throws ClassNotFoundException, IllegalAccessException{
		staticDoIt(o, thiz, rewrite);
	}
	
}
