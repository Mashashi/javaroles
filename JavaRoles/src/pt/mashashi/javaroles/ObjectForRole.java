package pt.mashashi.javaroles;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines that a field in the definition of a rigid type is the object responsible for dispatching the calls.
 * 
 * This field should have as type one of the same interfaces that the rigid object implements.
 * 
 * @author Rafael
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ObjectForRole {

}
