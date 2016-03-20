package pt.mashashi.javaroles.impl.typed;

import java.util.HashMap;


import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;
import pt.mashashi.javaroles.ClassUtils;
import pt.mashashi.javaroles.MissProcessingException;
import pt.mashashi.javaroles.register.RoleRegister;


/**
 * 
 * Calls the method on the object role if this method has not processed the call the method on the rigid type will be called
 * 
 * Before the method on the rigid type is called an optional method that might be implemented with the suffix _MissProcessing is called
 * when the role object did not wish to process this method.
 * 
 * @author Rafael
 *
 */
public class RoleRegisterTyped extends RoleRegister{
	
	private String srcFolder;
	
	public RoleRegisterTyped(String srcFolder) {
		//System.out.println("Working Directory = " + System.getProperty("user.dir"));
		this.srcFolder = srcFolder;
	}
	
	protected CtMethod injectRoleDependency(CtClass cn, CtMethod method) throws CannotCompileException, NotFoundException {
		
		//int lineNumberStart = method.getMethodInfo().getLineNumber(0);
		final String clazzName = cn.getName();
		final String name = method.getMethodInfo().getName();
		final String sig = method.getSignature();
		final String uuid = ClassUtils.generateIdentifier();
		final String varM = ClassUtils.generateIdentifier();
		
		String invokeMissProcessingCallback = "";
		
		// OLDFEAT [TYPED] #1 This feature was dropped because it contained a bug
		/*
		 When a rigid object implemented several interfaces we could not really be sure if the one received as the parameter
		 roleObjectClass was the one that missed the processing
		 */
		/*if(roleObjectClass!=null){
			// We just want to invoke the method _MissProcessing if it was thrown by a role hence the guard
			invokeMissProcessingCallback =
			"try{"+
				"Object result = " + ClassUtils.class.getName()+".invokeWithNativeTypes("+
												"this,"+
												"\""+name+"_MissProcessing\","+
												"new Class[]{"+String.class.getName()+".class,"+HashMap.class.getName()+".class},"+
												"new Object[]{\""+roleObjectClass.getType().getSimpleName()+"\",e1.getDetails()});"+	
			"}catch("+NoSuchMethodException.class.getName()+" e2){"+
				//Method not implemented. No problem. Do nothing.
				//"System.out.println(e2.getMessage());"+ // Print the method that was no found
			"}";
		}*/
		
		CtMethod newMethod = CtNewMethod.copy(method, uuid, cn, null);
		cn.addMethod(newMethod);
		
		method.setBody(
			"{"+	
					CtMethod.class.getName()+" "+varM+" = "+ClassUtils.class.getName()+".getExecutingMethod("+
												"\""+clazzName+"\","+
												"\""+name+"\","+
												"\""+sig+"\");"+
												
					"try {"+
						"return ($r) "+roleBusVarName+".resolve("+varM+", $args);"+
					"} catch("+MissProcessingException.class.getName()+" e1) {"+
						invokeMissProcessingCallback+
					"};"+
					"return ($r) "+ClassUtils.class.getName()+".invokeWithNativeTypes("+
												"this,"+
												"\""+uuid+"\","+
												varM+".getParameterTypes(),"+
												"$args);"
			+"}"
		);
		
		return newMethod;
	}

	@Override
	protected String getRoleBusDeclaration() {
		return "private "+RoleBusTyped.class.getName()+" "+roleBusVarName+" = new "+RoleBusTyped.class.getName()+"(this,\""+srcFolder+"\");";
	}

	@Override
	protected boolean isToInject(CtMethod method, HashMap<String,CtField> objectRoles) throws ClassNotFoundException {
		return method.getAnnotation(TurnOnRole.class)!=null;
	}
	
}