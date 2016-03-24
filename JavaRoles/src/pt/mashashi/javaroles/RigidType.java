package pt.mashashi.javaroles;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * What does this do to a class:
 * + Inject the code to get the dependency injection going
 * 
 * @author Rafael
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RigidType {}