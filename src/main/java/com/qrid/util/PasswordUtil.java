package com.qrid.util;
import org.mindrot.jbcrypt.BCrypt;
public final class PasswordUtil {
    private PasswordUtil(){}
    public static String hash(String raw){return BCrypt.hashpw(raw, BCrypt.gensalt(12));}
    public static boolean matches(String raw,String hash){return raw!=null && hash!=null && BCrypt.checkpw(raw,hash);}
}
