package com.qrid.filter;
import javax.servlet.*;import javax.servlet.annotation.WebFilter;import javax.servlet.http.*;import java.io.IOException;
@WebFilter("/*") public class SecurityHeadersFilter implements Filter{
 public void doFilter(ServletRequest req,ServletResponse res,FilterChain chain)throws IOException,ServletException{HttpServletResponse r=(HttpServletResponse)res;r.setHeader("X-Content-Type-Options","nosniff");r.setHeader("X-Frame-Options","SAMEORIGIN");r.setHeader("Referrer-Policy","same-origin");r.setHeader("Permissions-Policy","camera=(self)");r.setHeader("Content-Security-Policy","default-src 'self'; style-src 'self' 'unsafe-inline'; script-src 'self' 'unsafe-inline'; img-src 'self' data: blob:; media-src 'self' blob:; connect-src 'self'");chain.doFilter(req,res);}
}
