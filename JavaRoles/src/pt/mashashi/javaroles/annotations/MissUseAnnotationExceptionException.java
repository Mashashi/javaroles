package pt.mashashi.javaroles.annotations;

import java.lang.annotation.Annotation;
import java.util.HashMap;

@SuppressWarnings("serial")
public class MissUseAnnotationExceptionException extends RuntimeException{
	
	private static HashMap<String, String> messages;
	
	static{
		messages = new HashMap<>();
		messages.put(ObjRigid.class.getName()+AnnotationException.NOT_IMPLEMENTED_BY_RIGID, "The type interface of \"%s.%s\" is not implemented in the class %s.\n Remove the @"+ObjRigid.class.getSimpleName()+" annotation or implement it.");
		messages.put(ObjRigid.class.getName()+AnnotationException.MISS_USE, "The annotation @"+ObjRigid.class.getSimpleName()+" was used incorrectly.\n The field \"%s.%s\" should be an interface and implemented on the class %s.");
		messages.put(MissMsgReceptor.class.getName()+AnnotationException.BAD_TYPE, "The annotation @"+MissMsgReceptor.class.getSimpleName()+" was used incorrectly.\n The field \"%s.%s\" should be of type HashTable<String, Object>.");
		messages.put(ObjRole.class.getName()+AnnotationException.NOT_IMPLEMENTED_BY_RIGID, "The annotation @"+ObjRole.class.getSimpleName()+" was used incorrectly.\n The class \"%s\" has to implement the interfaces \"%s\" of the field \"%s\".");
		messages.put(ObjRole.class.getName()+AnnotationException.NOT_INTERFACE, "The annotation @"+ObjRole.class.getSimpleName()+" was used incorrectly.\n The field \"%s.%s\" has to be declared as an interface.");
		messages.put(ObjRole.class.getName()+AnnotationException.NOT_IMPLEMENTED_BY_ROLE, "The annotation @"+ObjRole.class.getSimpleName()+" was used incorrectly.\n The class role \"%s\" of type \"%s\" has to implement the interfaces \"%s\" that are present on the @"+ObjRole.class.getSimpleName()+" annotation.");
	}
	
	private Class<? extends Annotation> a;
	private AnnotationException e;
	
	public MissUseAnnotationExceptionException(Class<? extends Annotation> annot, AnnotationException exception, Object... args){
		super(String.format(messages.get(annot.getName()+exception), args));
		a = annot;
		e = exception;
	}
	
	public Class<? extends Annotation> getAnotation(){
		return a;
	}
	
	public AnnotationException getAnotationException(){
		return e;
	}
	
	
}
