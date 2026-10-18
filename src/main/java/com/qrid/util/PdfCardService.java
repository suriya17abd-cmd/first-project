package com.qrid.util;
import com.lowagie.text.*;import com.lowagie.text.pdf.*;import com.qrid.model.Identity;import java.awt.Color;import java.io.*;
public class PdfCardService {
 public byte[] create(Identity x,byte[] photo,String verifyUrl)throws Exception{
  ByteArrayOutputStream out=new ByteArrayOutputStream();Rectangle page=new Rectangle(360,225);Document d=new Document(page,18,18,16,14);PdfWriter.getInstance(d,out);d.open();
  PdfPTable header=new PdfPTable(new float[]{1,4});header.setWidthPercentage(100);Image logo=Image.getInstance(QrUtil.png("QRID CAMPUS",80));logo.scaleAbsolute(38,38);PdfPCell lc=new PdfPCell(logo);lc.setBorder(Rectangle.NO_BORDER);lc.setVerticalAlignment(Element.ALIGN_MIDDLE);header.addCell(lc);PdfPCell title=new PdfPCell();title.setBorder(Rectangle.NO_BORDER);title.addElement(new Paragraph("QRID CAMPUS",new Font(Font.HELVETICA,16,Font.BOLD,new Color(90,24,154))));title.addElement(new Paragraph("SECURE DIGITAL IDENTITY",new Font(Font.HELVETICA,8,Font.BOLD,Color.DARK_GRAY)));header.addCell(title);d.add(header);d.add(new Paragraph(" ",new Font(Font.HELVETICA,3)));
  PdfPTable body=new PdfPTable(new float[]{1.25f,3.2f,1.35f});body.setWidthPercentage(100);
  PdfPCell pc;if(photo!=null&&photo.length>0){Image im=Image.getInstance(photo);im.scaleToFit(70,82);pc=new PdfPCell(im,true);}else{pc=new PdfPCell(new Phrase("PHOTO",new Font(Font.HELVETICA,8)));}pc.setFixedHeight(95);pc.setHorizontalAlignment(Element.ALIGN_CENTER);pc.setVerticalAlignment(Element.ALIGN_MIDDLE);body.addCell(pc);
  PdfPCell info=new PdfPCell();info.setPadding(7);info.setBorder(Rectangle.NO_BORDER);info.addElement(new Paragraph(x.getFullName(),new Font(Font.HELVETICA,13,Font.BOLD)));info.addElement(line("ID: ",x.getIdentityNo()));info.addElement(line("Type: ",x.getPersonType()));info.addElement(line("Department: ",x.getDepartment()));info.addElement(line("Course: ",x.getCourse()));info.addElement(line("Valid until: ",String.valueOf(x.getValidUntil())));body.addCell(info);
  Image qr=Image.getInstance(QrUtil.png(verifyUrl,180));qr.scaleAbsolute(82,82);PdfPCell qc=new PdfPCell(qr,true);qc.setBorder(Rectangle.NO_BORDER);qc.setHorizontalAlignment(Element.ALIGN_CENTER);qc.setVerticalAlignment(Element.ALIGN_MIDDLE);body.addCell(qc);d.add(body);
  Paragraph foot=new Paragraph("Scan QR to verify authenticity  |  Version "+x.getVersion(),new Font(Font.HELVETICA,7,Font.NORMAL,Color.GRAY));foot.setAlignment(Element.ALIGN_CENTER);d.add(foot);d.close();return out.toByteArray();
 }
 private Paragraph line(String a,String b){Phrase p=new Phrase();p.add(new Chunk(a,new Font(Font.HELVETICA,7,Font.BOLD)));p.add(new Chunk(b==null?"-":b,new Font(Font.HELVETICA,7)));return new Paragraph(p);}
}
