package com.qrid.util;
import java.util.regex.Pattern;
public final class Validation {
    private static final Pattern EMAIL=Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern ID=Pattern.compile("^[A-Za-z0-9/_-]{3,40}$");
    private Validation(){}
    public static boolean email(String v){return v!=null && v.length()<=120 && EMAIL.matcher(v).matches();}
    public static boolean identityNo(String v){return v!=null && ID.matcher(v).matches();}
    public static String text(String v,int max){if(v==null)return "";v=v.trim();return v.length()>max?v.substring(0,max):v;}
}
