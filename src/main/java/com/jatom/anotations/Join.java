package com.jatom.anotations;

import com.sun.tools.javac.code.Attribute;
import org.apache.commons.lang.ObjectUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Join {
    Class reference();
    String columnName() default "";
    String columnReference() default "";
}
