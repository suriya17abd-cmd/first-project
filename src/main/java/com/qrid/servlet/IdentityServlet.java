package com.qrid.servlet;
import com.qrid.dao.*;import com.qrid.model.*;import com.qrid.util.*;import javax.servlet.*;import javax.servlet.annotation.*;import javax.servlet.http.*;import java.io.*;import java.sql.Date;import java.util.*;
@WebServlet(urlPatterns={"/admin/identities","/admin/identity-form","/staff/identities","/staff/identity-form"})
@MultipartConfig(maxFileSize=3145728,maxRequestSize=4194304) public class IdentityServlet extends HttpServlet{
 private final IdentityDao dao=new IdentityDao();
 protected void doGet(HttpServletRequest r,HttpServletResponse s)throws ServletException,IOException{
  User u=(User)r.getSession().getAttribute("user");String path=r.getServletPath();
  try{
   if(path.endsWith("identity-form")){String id=r.getParameter("id");if(id!=null&&!id.isBlank()){Identity x=dao.find(Long.parseLong(id));if(!allowed(u,x)){s.sendError(403);return;}r.setAttribute("identity",x);}forward(r,s,u,"identity-form.jsp");}
   else{String dept="STAFF".equals(u.getRole())?u.getDepartment():r.getParameter("department");r.setAttribute("identities",dao.list(r.getParameter("q"),r.getParameter("status"),dept));forward(r,s,u,"identities.jsp");}
  }catch(Exception e){throw new ServletException(e);}
 }
 protected void doPost(HttpServletRequest r,HttpServletResponse s)throws ServletException,IOException{
  User u=(User)r.getSession().getAttribute("user");String action=Optional.ofNullable(r.getParameter("action")).orElse("save");
  try{
   if("status".equals(action)){long id=Long.parseLong(r.getParameter("id"));Identity x=dao.find(id);if(!allowed(u,x)){s.sendError(403);return;}String status=r.getParameter("status");if(!Set.of("ACTIVE","SUSPENDED","GRADUATED").contains(status))throw new IllegalArgumentException("Invalid status");dao.status(id,status);audit(r,u,"STATUS_CHANGED",id,"Status set to "+status);Flash.set(r,"success","Identity status updated.");}
   else if("regenerate".equals(action)){requireAdmin(u);long id=Long.parseLong(r.getParameter("id"));dao.regenerate(id,TokenUtil.generate());audit(r,u,"QR_REGENERATED",id,"Old QR token revoked");Flash.set(r,"success","A new QR token and card version were generated.");}
   else if("delete".equals(action)){requireAdmin(u);long id=Long.parseLong(r.getParameter("id"));dao.delete(id);audit(r,u,"IDENTITY_DELETED",id,"Identity deleted");Flash.set(r,"success","Identity deleted.");}
   else save(r,u);
   s.sendRedirect(r.getContextPath()+("ADMIN".equals(u.getRole())?"/admin/identities":"/staff/identities"));
  }catch(IllegalArgumentException e){Flash.set(r,"danger",e.getMessage());s.sendRedirect(r.getContextPath()+(("ADMIN".equals(u.getRole()))?"/admin/identity-form":"/staff/identity-form"));}catch(SecurityException e){s.sendError(403);}catch(Exception e){throw new ServletException(e);}
 }
 private void save(HttpServletRequest r,User u)throws Exception{
  Identity x=new Identity();String id=r.getParameter("id");if(id!=null&&!id.isBlank())x.setId(Long.parseLong(id));
  x.setIdentityNo(Validation.text(r.getParameter("identityNo"),40));x.setFullName(Validation.text(r.getParameter("fullName"),120));x.setEmail(Validation.text(r.getParameter("email"),120));x.setPersonType(r.getParameter("personType"));x.setDepartment("STAFF".equals(u.getRole())?u.getDepartment():Validation.text(r.getParameter("department"),100));x.setCourse(Validation.text(r.getParameter("course"),120));x.setStatus(Optional.ofNullable(r.getParameter("status")).orElse("ACTIVE"));x.setValidFrom(Date.valueOf(r.getParameter("validFrom")));x.setValidUntil(Date.valueOf(r.getParameter("validUntil")));
  if(!Validation.identityNo(x.getIdentityNo())||x.getFullName().length()<2||!Validation.email(x.getEmail())||!Set.of("STUDENT","STAFF").contains(x.getPersonType()))throw new IllegalArgumentException("Enter a valid identity number, name, email, and person type.");if(x.getValidUntil().before(x.getValidFrom()))throw new IllegalArgumentException("Valid-until date must be after valid-from date.");
  Part photo=r.getPart("photo");byte[] bytes=null;String mime=null;if(photo!=null&&photo.getSize()>0){mime=photo.getContentType();if(photo.getSize()>3145728||!Set.of("image/jpeg","image/png").contains(mime))throw new IllegalArgumentException("Photo must be JPG or PNG and no larger than 3 MB.");bytes=photo.getInputStream().readAllBytes();}
  if(x.getId()==0){
   if(bytes==null)throw new IllegalArgumentException("A photo is required for a new identity.");x.setQrToken(TokenUtil.generate());long newId=dao.insert(x,bytes,mime,u.getId());Identity saved=dao.find(newId);byte[] pdf=new PdfCardService().create(saved,bytes,UrlUtil.base(r)+"/verify?token="+saved.getQrToken());dao.savePdf(newId,saved.getVersion(),pdf);boolean emailed=false;try{emailed=new EmailService().sendIdentityCard(saved.getEmail(),saved,pdf);}catch(Exception mailError){audit(r,u,"EMAIL_FAILED",newId,mailError.getClass().getSimpleName());}audit(r,u,"IDENTITY_CREATED",newId,x.getIdentityNo()+(emailed?"; email sent":""));
  }else{Identity old=dao.find(x.getId());if(!allowed(u,old))throw new SecurityException();dao.update(x,bytes,mime);audit(r,u,"IDENTITY_UPDATED",x.getId(),x.getIdentityNo());}
  Flash.set(r,"success","Digital identity saved successfully.");
 }
 private boolean allowed(User u,Identity x){return x!=null&&(!"STAFF".equals(u.getRole())||Objects.equals(u.getDepartment(),x.getDepartment()));}
 private void requireAdmin(User u){if(!"ADMIN".equals(u.getRole()))throw new SecurityException();}
 private void audit(HttpServletRequest r,User u,String a,long id,String d){new AuditDao().log(u.getId(),a,"IDENTITY",String.valueOf(id),d,r.getRemoteAddr());}
 private void forward(HttpServletRequest r,HttpServletResponse s,User u,String file)throws ServletException,IOException{r.getRequestDispatcher("/WEB-INF/views/"+("ADMIN".equals(u.getRole())?"admin/":"staff/")+file).forward(r,s);}
}
