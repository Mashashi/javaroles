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
	
	protected CtMethod injectRoleDependency(CtClass cn, CtMethod method, CtField roleObjectClass) throws CannotCompileException, NotFoundException {
		
		CtMethod methodCreated =null;
		
		//int lineNumberStart = method.getMethodInfo().getLineNumber(0);
		final String clazzName = cn.getName();
		final String name = method.getMethodInfo().getName();
		final String sig = method.getSignature();
		final String uuid = name+UUID.randomUUID().toString();
		//final String varAnot = ClassUtils.generateIdentifier();
		final String varM = ClassUtils.generateIdentifier();
		
		methodCreated = CtNewMethod.copy(method, uuid, cn, null);
		cn.addMethod(methodCreated);
		
		method.setBody(
			"{"+
					CtMethod.class.getName()+" "+varM+" = "+ClassUtils.class.getName()+".getExecutingMethod("+
																						"\""+clazzName+"\","+
																						"\""+name+"\","+
																						"\""+sig+"\");"+	
					
					// Old core call
					/*RoleObject.class.getName()+" "+varAnot+" = Class.forName("+ClassUtils.class.getName()+".getExecutingClass(3)).getAnnotation("+RoleObject.class.getName()+".class);" +
					"if("+varAnot+"!=null){"+
						"for(int i = 0; i<"+varAnot+".types().length; i++){"+
						//"System.out.println(fieldName+\" - \"+anot.types()[i].getName());"+
						"if("+varAnot+".types()[i].getName().equals(\""+cn.getName()+"\"))"+
								"return ($r) "+ClassUtils.class.getName()+".invokeWithNativeTypes("+
															"this,"+
															"\""+uuid+"\","+
															varM+".getParameterTypes(),"+
															"$args);"+
						"}"+
					"}"+*/
									
					"try {"+
						"return ($r) "+roleBusVarName+".resolve("+varM+", $args);"+
					"} catch("+MissProcessingException.class.getName()+" e) {};"+
					
					"return ($r) "+ClassUtils.class.getName()+".invokeWithNativeTypes("+
												"this,"+
												"\""+uuid+"\","+
												varM+".getParameterTypes(),"+
												"$args);"
			+"}"
		);
		
		return methodCreated;
		
	}

	@Override
	protected String getRoleBusDeclaration() {
		return "private "+RoleBusComposition.class.getName()+" "+roleBusVarName+" = new "+RoleBusComposition.class.getName()+"(this);";
	}
	
}