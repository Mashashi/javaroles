package pt.mashashi.javaroles;

import java.lang.annotation.Annotation;
import java.util.HashMap;

import pt.mashashi.javaroles.composition.OriginalRigid;

@SuppressWarnings("serial")
public class MissUseAnnotationExceptionException extends RuntimeException{
	
	private static HashMap<String, String> messages;
	
	static{
		messages = new HashMap<>();
		messages.put(OriginalRigid.class.getName()+AnotationException.NOT_IMPLEMENTED, "The type interface of \"%s.%s\" is not implemented in the class %s.\n Remove the @"+OriginalRigid.class.getSimpleName()+" annotation or implement it.");
		messages.put(OriginalRigid.class.getName()+AnotationException.MISS_USE, "The annotation @"+OriginalRigid.class.getSimpleName()+" was used incorrectly.\n The field \"%s.%s\" should be an interface and implemented on the class %s.");
	}
	
	private Class<? extends Annotation> a;
	private AnotationException e;
	
	public MissUseAnnotationExceptionException(Class<? extends Annotation> annot, AnotationException exception, Object... args){
		super(String.format(messages.get(annot.getName()+exception), args));
		a = annot;
		e = exception;
	}
	
	public Class<? extends Annotation> getAnotation(){
		return a;
	}
	
	public AnotationException getAnotationException(){
		return e;
	}
	
	
}
