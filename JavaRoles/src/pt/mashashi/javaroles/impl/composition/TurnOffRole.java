package pt.mashashi.javaroles.impl.composition;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import pt.mashashi.javaroles.RoleBus;

/**
 * This will tell the system to not inject a call to resolve the role object method.
 * 
 * @author Rafael
 * @see RoleBus#resolve(javassist.CtMethod, Object[])
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface TurnOffRole {}