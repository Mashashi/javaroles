package pt.mashashi.javaroles.composition;

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
 * Calls the method on the object role if this method has not processed the call the method on the rigid type will be called
 * 
 * @author Rafael
 *
 */
public class RoleRegisterComposition extends RoleRegister{
	
	public RoleRegisterComposition() {}
	
	protected void injectRoleDependency(CtClass cn, CtMethod method, CtField roleObjectClass) throws CannotCompileException, NotFoundException {
		
		//int lineNumberStart = method.getMethodInfo().getLineNumber(0);
		final String clazzName = cn.getName();
		final String name = method.getMethodInfo().getName();
		final String sig = method.getSignature();
		final String uuid = name+UUID.randomUUID().toString();
		
		cn.addMethod(CtNewMethod.copy(method, uuid, cn, null));
		
		method.setBody(
			"{"+
					CtMethod.class.getName()+" m = "+ClassUtils.class.getName()+".getExecutingMethod("+
												"\""+clazzName+"\","+
												"\""+name+"\","+
												"\""+sig+"\");"+
												
					"try {"+
						"return ($r) "+roleBusVarName+".resolve(m, $args);"+
					"} catch("+MissProcessingException.class.getName()+" e1) {};"+
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
		return "private "+RoleBusComposition.class.getName()+" "+roleBusVarName+" = new "+RoleBusComposition.class.getName()+"(this);";
	}
	
}