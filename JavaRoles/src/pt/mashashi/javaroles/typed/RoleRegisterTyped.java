package pt.mashashi.javaroles.typed;

import java.util.HashMap;
import java.util.UUID;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;
import pt.mashashi.javaroles.ClassUtils;
import pt.mashashi.javaroles.MissProcessingException;
import pt.mashashi.javaroles.RoleRegister;


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
		this.srcFolder = srcFolder;
	}
	
	protected void injectRoleDependency(CtClass cn, CtMethod method, CtField roleObjectClass) throws CannotCompileException, NotFoundException {
		
		//int lineNumberStart = method.getMethodInfo().getLineNumber(0);
		final String clazzName = cn.getName();
		final String name = method.getMethodInfo().getName();
		final String sig = method.getSignature();
		final String uuid = name+UUID.randomUUID().toString();
		
		String invokeMissProcessingCallback = "";
		
		if(roleObjectClass!=null){
			// We just want to invoke the method _MissProcessing if it was thrown by a role hence the guard
			invokeMissProcessingCallback =
			"try{"+
				"Object result = " + ClassUtils.class.getName()+".invokeWithNativeTypes("+
												"this,"+
												"\""+name+"_MissProcessing\","+
												"new Class[]{"+String.class.getName()+".class,"+HashMap.class.getName()+".class},"+
												"new Object[]{\""+roleObjectClass.getType().getSimpleName()+"\",e1.getDetails()});"+	
			"}catch("+NoSuchMethodException.class.getName()+" e2){"+
				"/*Method not implemented. No problem. Do nothing.*/"+
				//"System.out.println(e2.getMessage());"+ // Print the method that was no found
			"}";
		}
		
		cn.addMethod(CtNewMethod.copy(method, uuid, cn, null));
		method.setBody(
			"{"+
					CtMethod.class.getName()+" m = "+ClassUtils.class.getName()+".getExecutingMethod("+
												"\""+clazzName+"\","+
												"\""+name+"\","+
												"\""+sig+"\");"+
												
					"try {"+
						"return ($r) role.resolve(m, $args);"+
					"} catch("+MissProcessingException.class.getName()+" e1) {"+
						invokeMissProcessingCallback+
					"};"+
					"return ($r) "+ClassUtils.class.getName()+".invokeWithNativeTypes("+
												"this,"+
												"\""+uuid+"\","+
												"m.getParameterTypes(),"+
												"$args);"
			+"}"
		);
	}

	@Override
	protected String getRoleBusDeclaration() {
		return "private "+RoleBusTyped.class.getName()+" role = new "+RoleBusTyped.class.getName()+"(this,\""+srcFolder+"\");";
	}
	
}