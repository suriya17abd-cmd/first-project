package com.qrid.servlet;
import com.qrid.dao.AuditDao;import com.qrid.model.User;import javax.servlet.annotation.WebServlet;import javax.servlet.http.*;import java.io.IOException;
@WebServlet("/logout") public class LogoutServlet extends HttpServlet{protected void doPost(HttpServletRequest r,HttpServletResponse s)throws IOException{User u=(User)r.getSession().getAttribute("user");if(u!=null)new AuditDao().log(u.getId(),"LOGOUT","USER",String.valueOf(u.getId()),"Session ended",r.getRemoteAddr());r.getSession().invalidate();s.sendRedirect(r.getContextPath()+"/login?logout=1");}}
