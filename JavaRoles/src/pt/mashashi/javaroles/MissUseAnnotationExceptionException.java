package pt.mashashi.javaroles;

import java.lang.annotation.Annotation;
import java.util.HashMap;

import pt.mashashi.javaroles.composition.MissMsgReceptor;
import pt.mashashi.javaroles.composition.OriginalRigid;

@SuppressWarnings("serial")
public class MissUseAnnotationExceptionException extends RuntimeException{
	
	private static HashMap<String, String> messages;
	
	static{
		messages = new HashMap<>();
		messages.put(OriginalRigid.class.getName()+AnnotationException.NOT_IMPLEMENTED, "The type interface of \"%s.%s\" is not implemented in the class %s.\n Remove the @"+OriginalRigid.class.getSimpleName()+" annotation or implement it.");
		messages.put(OriginalRigid.class.getName()+AnnotationException.MISS_USE, "The annotation @"+OriginalRigid.class.getSimpleName()+" was used incorrectly.\n The field \"%s.%s\" should be an interface and implemented on the class %s.");
		messages.put(MissMsgReceptor.class.getName()+AnnotationException.BAD_TYPE, "The annotation @"+MissMsgReceptor.class.getSimpleName()+" was used incorrectly.\n The field \"%s.%s\" should be of type HashTable<String, Object>.");
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
