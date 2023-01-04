package com.jatom.utils;

import com.jatom.anotations.TableName;

import java.lang.reflect.InvocationTargetException;

public class SqlUtils {

    public static String filter(String filter){

        filter = filter.toLowerCase().replace("drop","");
        filter = filter.toLowerCase().replace("update","");
        filter = filter.toLowerCase().replace("select","");
        filter = filter.toLowerCase().replace("insert","");
        filter = filter.replace("eq","=");
        filter = filter.replace("neq","<>");
        filter = filter.replace("ma",">");
        filter = filter.replace("mi","<");
        filter = filter.replace("maq",">=");
        filter = filter.replace("miq","<=");

        return filter;
    }

    protected static String getClassName(Class clazz){
        String className = "";
        try {
            Object cls  = clazz.getDeclaredConstructor().newInstance();
            if(cls.getClass().getAnnotation(TableName.class) != null){
                className = cls.getClass().getAnnotation(TableName.class).value();
            }
            else {
                className = cls.getClass().getSimpleName();
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        return className;
    }
}
