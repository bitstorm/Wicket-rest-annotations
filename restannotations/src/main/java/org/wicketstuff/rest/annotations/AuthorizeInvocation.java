package org.wicketstuff.rest.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD})
@Documented
@Inherited
public @interface AuthorizeInvocation {
	
	/**
	 * Gets the roles that are allowed to invoke a method.
	 * 
	 * @return the roles that are allowed. Returns a zero length array by default
	 */
	String[] value() default { };
}
