package pt.mashashi.javaroles.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * What does this do to a class:
 * + Makes it like the calls to its method through a rigid are serializable
 * 
 * @author Rafael
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.CONSTRUCTOR})
public @interface ThreadSafeRole {}