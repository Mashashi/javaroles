package pt.mashashi.javaroles.injection;

import java.lang.reflect.Field;
import java.util.List;

import org.apache.commons.lang3.reflect.FieldUtils;

import pt.mashashi.javaroles.ClassUtils;
import pt.mashashi.javaroles.annotations.InjObjRigid;

public class InjectionStrategyMultiple extends InjectionStrategy {

	private String posConstructor;
	
	InjectionStrategyMultiple() {
		StringBuffer injectionCode = new StringBuffer("");
		
		injectionCode.append(Field.class.getName()+"[] fs=this.getClass().getFields();");
		injectionCode.append("for(int i=0;i<fs.length;i++){");
		
			injectionCode.append("Object o = "+FieldUtils.class.getName()+".readField(fs[i], this, true);");
			injectionCode.append("if(o!=null){");
				injectionCode.append(List.class.getName()+" l = "+ClassUtils.class.getName()+".getListFieldAnotated(");
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
							injectionCode.append("/*Do nothing - Just a cast error*/");
						injectionCode.append("}finally{");
							injectionCode.append("f.setAccessible(accesibilityOriginal);");
						injectionCode.append("}");
					
				injectionCode.append("}");
			injectionCode.append("}");
		injectionCode.append("}");
		
		posConstructor = injectionCode.toString(); 
	}
	
	@Override
	public String set() {
		return posConstructor;
	}

}
