package com.qrid.filter;
import javax.servlet.*;import javax.servlet.annotation.WebFilter;import javax.servlet.http.*;import java.io.IOException;import java.security.SecureRandom;import java.util.Base64;
@WebFilter("/*") public class CsrfFilter implements Filter{
 private static final SecureRandom RANDOM=new SecureRandom();
 public void doFilter(ServletRequest req,ServletResponse res,FilterChain chain)throws IOException,ServletException{HttpServletRequest r=(HttpServletRequest)req;HttpServletResponse s=(HttpServletResponse)res;HttpSession session=r.getSession(true);String expected=(String)session.getAttribute("csrfToken");if(expected==null){byte[] b=new byte[24];RANDOM.nextBytes(b);expected=Base64.getUrlEncoder().withoutPadding().encodeToString(b);session.setAttribute("csrfToken",expected);}if("POST".equalsIgnoreCase(r.getMethod())){String actual=r.getHeader("X-CSRF-Token");if(actual==null)actual=r.getParameter("_csrf");if(!java.security.MessageDigest.isEqual(expected.getBytes(java.nio.charset.StandardCharsets.UTF_8),(actual==null?"":actual).getBytes(java.nio.charset.StandardCharsets.UTF_8))){s.sendError(403,"Invalid security token");return;}}chain.doFilter(req,res);}
}
