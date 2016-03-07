package pt.mashashi.javaroles.impl.typed;

import java.io.FileInputStream;

import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.log4j.Logger;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import pt.mashashi.javaroles.ClassUtils;
import pt.mashashi.javaroles.MissProcessingException;
import pt.mashashi.javaroles.RoleBus;
import pt.mashashi.javaroles.annotations.ObjRole;
import pt.mashashi.javaroles.typed.role.ResolveRoleTest;

/**
 * Uses the interface on which the rigid object is assigned to to resolve the role type method. To do this
 * the source code of the original code has to be parsed. See the test battery {@link ResolveRoleTest} to check
 * the calls on rigid types that will succeed and those that will not.
 * 
 * Implements life cycle call backs 
 * 
 * Note that this implementation is computational expensive
 * 
 * @author Rafael
 *
 */
public class RoleBusTyped extends RoleBus{
	
	
	private static Logger log = Logger.getLogger(RoleBus.class.getName());
	
	// Key - File name
	private static HashMap<String, CompilationUnit> cus = new HashMap<>();
	
	// Key - Filename + Line + Method name + Occurence
	// Value - Number of times parsed
	private static HashMap<String, Integer> ite = new HashMap<>();
	
	private String srcFolder; 
	
	@SuppressWarnings("unused")
	private RoleBusTyped() {}
	
	public RoleBusTyped(Object target, String srcFolder) {
		this.target = target;
		this.srcFolder = srcFolder;
	}
	
	@SuppressWarnings("unchecked")
	public Object resolve(CtMethod methodInvoked, Object[] params) throws MissProcessingException{
		
		log.debug("resolve");
		
		Object returnByRole = null;
		
		StackTraceElement[] st = Thread.currentThread().getStackTrace();
		String methodResolve = ClassUtils.getMethodNameFromStack(st, 2);
		
		String lineRef = st[3].toString();
		Pattern p = Pattern.compile("^(.*)\\.(.*)\\(.*:([0-9]+)\\)$");
		Matcher m = p.matcher(lineRef);
		m.matches();
		@SuppressWarnings("unused")
		String clazz = m.group(1), method = m.group(2), line = m.group(3);
		
		
		String roleName = null;
		
	    try {
	    	
	    	ClassPool pool = ClassPool.getDefault();
			CtClass cc = pool.get(clazz);
			
			final String pkg = cc.getPackageName();
			final String pathPrefix = pkg == null ? "" : pkg.replace(".", "/") + "/";
			String sourceFile = pathPrefix+cc.getClassFile().getSourceFile();
			
			CompilationUnit cu = getCompilationUnit(sourceFile);
			
			MethodVisitor analyser = new MethodVisitor(Integer.parseInt(line), methodResolve);
			analyser.visit(cu, null);
			
			String idCall = sourceFile+methodResolve+line;
			roleName = computeSeveralLineRoleCalls(analyser, idCall);
			
			if(roleName==null){
		    	throw new RoleNotFoundExpcetion(sourceFile+"@"+line);
		    }
			
			invokeLifeCycleCallbacks(roleName, methodInvoked);
			
			returnByRole = invokeRoleMethod(methodInvoked, params, roleName);
			
			
		} catch (ParseException | FileNotFoundException | NotFoundException e) {
			e.printStackTrace();
		}
	    	    
	    return returnByRole;
	}
	
	private String computeSeveralLineRoleCalls(
					MethodVisitor analyser,
					String idCall) {
		String roleName = null;
		List<String> roles = analyser.getRoleNames();
		if(roles.size() != 0){
			Integer round = ite.get(idCall);
			round = round == null ? 0 : round + 1;
			
			if(roles.size()<=round){
				ite.put(idCall, round);
				round = 0;
			}
			ite.put(idCall, round);
			roleName = roles.get(round);
		}
		return roleName;
	}

	private CompilationUnit getCompilationUnit(String sourceFile) throws FileNotFoundException, ParseException {
		CompilationUnit cu = cus.get(sourceFile);
		if(cu==null){
			FileInputStream f = new FileInputStream(srcFolder+sourceFile);
			cu = JavaParser.parse(f);
			cus.put(sourceFile, cu);
		}
		return cu;
	}
	
	private Object invokeRoleMethod(
			CtMethod methodInvoked,
			Object[] params, 
			String roleName) throws NotFoundException {
		
		
		
		Object roleReturned = null;
		
		HashMap<String, Field> roleObjects = ClassUtils.getTypeFieldAnotatedNative(target, ObjRole.class);
		
		{
			Field objectRole = roleObjects.get(roleName);
			if(objectRole!=null){
				try {
					Object o = FieldUtils.readField(objectRole, target, true);
					Class<?>[] paramsObjectRole = ClassUtils.getNativeTypes(methodInvoked.getParameterTypes());
					roleReturned = ClassUtils.invokeWithNativeTypes(o, methodInvoked.getName(), paramsObjectRole, params);
				}catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
					// TODO
					if(e.getCause().getClass().equals(MissProcessingException.class)){
						throw (MissProcessingException) e.getCause();
					}else{
						e.printStackTrace();
					}
					
				}
			} else {
				throw new MissProcessingException(roleName, target.getClass().getName(), MissProcessingException.WhyMiss.NOT_FOUND_ROLE);
			}
		}
		
		return roleReturned;
	}

}