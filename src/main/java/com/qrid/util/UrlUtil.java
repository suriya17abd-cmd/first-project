package com.qrid.util;
import com.qrid.config.Database;import javax.servlet.http.HttpServletRequest;
public final class UrlUtil{
 private UrlUtil(){}
 public static String base(HttpServletRequest r){String configured=Database.property("app.baseUrl");if(configured!=null&&!configured.isBlank()&&!"auto".equalsIgnoreCase(configured))return configured.replaceAll("/+$","");int port=r.getServerPort();boolean standard=("http".equals(r.getScheme())&&port==80)||("https".equals(r.getScheme())&&port==443);return r.getScheme()+"://"+r.getServerName()+(standard?"":":"+port)+r.getContextPath();}
}
