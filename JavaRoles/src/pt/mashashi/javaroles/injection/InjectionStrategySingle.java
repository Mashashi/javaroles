package pt.mashashi.javaroles.injection;

import java.lang.reflect.Field;
import java.util.List;

import org.apache.commons.lang3.reflect.FieldUtils;

import pt.mashashi.javaroles.ClassUtils;
import pt.mashashi.javaroles.annotations.InjObjRigid;

public class InjectionStrategySingle extends InjectionStrategy {

	private String posConstructor;
	
	InjectionStrategySingle() {
		StringBuffer injectionCode = new StringBuffer("");
		
		injectionCode.append(Field.class.getName()+"[] fs=this.getClass().getFields();");
		injectionCode.append("for(int i=0;i<fs.length;i++){");
		
			injectionCode.append("Object o = "+FieldUtils.class.getName()+".readField(fs[i], this, true);");
			injectionCode.append("if(o!=null){");
				injectionCode.append(List.class.getName()+" l = "+ClassUtils.class.getName()+".getListFieldAnotated(");
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
							injectionCode.append("/*Do nothing - Just a cast error*/");
						injectionCode.append("}finally{");
							injectionCode.append("f.setAccessible(accesibilityOriginal);");
						injectionCode.append("}");
						
					injectionCode.append("}");
					
				injectionCode.append("}");
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
		
		posConstructor = injectionCode.toString(); 
	}
	
	@Override
	public String posConstructor() {
		return posConstructor;
	}

}
