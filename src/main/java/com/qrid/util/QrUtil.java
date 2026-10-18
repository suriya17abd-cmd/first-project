package com.qrid.util;
import com.google.zxing.*;import com.google.zxing.client.j2se.*;import com.google.zxing.common.*;import com.google.zxing.qrcode.QRCodeWriter;
import javax.imageio.ImageIO;import java.awt.image.BufferedImage;import java.io.*;
public final class QrUtil {
 private QrUtil(){}
 public static byte[] png(String text,int size)throws Exception{BitMatrix m=new QRCodeWriter().encode(text,BarcodeFormat.QR_CODE,size,size);ByteArrayOutputStream out=new ByteArrayOutputStream();MatrixToImageWriter.writeToStream(m,"PNG",out);return out.toByteArray();}
 public static String decode(byte[] data)throws Exception{BufferedImage img=ImageIO.read(new ByteArrayInputStream(data));if(img==null)throw new IllegalArgumentException("Invalid image");BinaryBitmap b=new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(img)));return new MultiFormatReader().decode(b).getText();}
}
