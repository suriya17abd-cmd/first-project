package com.qrid.util;
import com.qrid.model.Identity;import org.junit.jupiter.api.Test;import java.sql.Date;import static org.junit.jupiter.api.Assertions.*;
class QrPdfTest{
 @Test void qrRoundTrip()throws Exception{String value="http://localhost:8080/qrid-campus/verify?token=test-123";assertEquals(value,QrUtil.decode(QrUtil.png(value,300)));}
 @Test void pdfGeneration()throws Exception{Identity x=new Identity();x.setIdentityNo("STU/CSE/001");x.setFullName("Test Student");x.setPersonType("STUDENT");x.setDepartment("Computer Science");x.setCourse("B.Sc CS");x.setValidUntil(Date.valueOf("2029-05-31"));x.setVersion(1);byte[] pdf=new PdfCardService().create(x,null,"http://localhost/verify?token=test");assertTrue(pdf.length>1000);assertEquals("%PDF",new String(pdf,0,4));}
 @Test void passwordRoundTrip(){String hash=PasswordUtil.hash("Admin@123");assertTrue(PasswordUtil.matches("Admin@123",hash));assertFalse(PasswordUtil.matches("wrong",hash));}
}
