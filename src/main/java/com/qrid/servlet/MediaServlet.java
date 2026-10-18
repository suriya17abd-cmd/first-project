package com.qrid.servlet;
import com.qrid.dao.IdentityDao;import com.qrid.model.*;import com.qrid.util.*;import javax.servlet.*;import javax.servlet.annotation.WebServlet;import javax.servlet.http.*;import java.io.*;import java.util.Objects;
@WebServlet(urlPatterns={"/identity-photo","/card"}) public class MediaServlet extends HttpServlet{
 protected void doGet(HttpServletRequest r,HttpServletResponse s)throws ServletException,IOException{
  try{
   long id=Long.parseLong(r.getParameter("id"));IdentityDao dao=new IdentityDao();Identity x=dao.find(id);
   if(x==null){s.sendError(404);return;}User u=(User)r.getSession().getAttribute("user");
   if("STAFF".equals(u.getRole())&&!Objects.equals(u.getDepartment(),x.getDepartment())){s.sendError(403);return;}
   byte[] photo=dao.photo(id);
   if("/identity-photo".equals(r.getServletPath())){if(photo==null){s.sendError(404);return;}s.setContentType(x.getPhotoMime());s.setHeader("Cache-Control","private,max-age=300");s.getOutputStream().write(photo);return;}
   byte[] pdf=new PdfCardService().create(x,photo,UrlUtil.base(r)+"/verify?token="+x.getQrToken());dao.savePdf(id,x.getVersion(),pdf);
   s.setContentType("application/pdf");s.setHeader("Content-Disposition","attachment; filename=QRID-"+x.getIdentityNo().replaceAll("[^A-Za-z0-9_-]","_")+"-v"+x.getVersion()+".pdf");s.setContentLength(pdf.length);s.getOutputStream().write(pdf);
  }catch(NumberFormatException e){s.sendError(400);}catch(Exception e){throw new ServletException(e);}
 }
}
