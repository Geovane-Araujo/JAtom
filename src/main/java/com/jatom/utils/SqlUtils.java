package com.jatom.utils;

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
}
