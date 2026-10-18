package com.qrid.util;
import java.security.SecureRandom;
import java.util.Base64;
public final class TokenUtil {
    private static final SecureRandom R=new SecureRandom(); private TokenUtil(){}
    public static String generate(){byte[] b=new byte[24];R.nextBytes(b);return Base64.getUrlEncoder().withoutPadding().encodeToString(b);}
}
