package pt.mashashi.javaroles;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This will tell the system to inject a call to resolve the role object method.
 * 
 * @author Rafael
 * @see RoleBus#resolve(javassist.CtMethod, Object[])
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface TurnOnRole {}