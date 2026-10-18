package com.qrid.model;

import java.sql.Date;
import java.sql.Timestamp;

public class Identity {
    private long id; private String identityNo, fullName, email, personType, department, course, status, qrToken, photoMime;
    private Date validFrom, validUntil; private int version; private Timestamp createdAt, updatedAt;
    public long getId(){return id;} public void setId(long v){id=v;}
    public String getIdentityNo(){return identityNo;} public void setIdentityNo(String v){identityNo=v;}
    public String getFullName(){return fullName;} public void setFullName(String v){fullName=v;}
    public String getEmail(){return email;} public void setEmail(String v){email=v;}
    public String getPersonType(){return personType;} public void setPersonType(String v){personType=v;}
    public String getDepartment(){return department;} public void setDepartment(String v){department=v;}
    public String getCourse(){return course;} public void setCourse(String v){course=v;}
    public String getStatus(){return status;} public void setStatus(String v){status=v;}
    public String getQrToken(){return qrToken;} public void setQrToken(String v){qrToken=v;}
    public String getPhotoMime(){return photoMime;} public void setPhotoMime(String v){photoMime=v;}
    public Date getValidFrom(){return validFrom;} public void setValidFrom(Date v){validFrom=v;}
    public Date getValidUntil(){return validUntil;} public void setValidUntil(Date v){validUntil=v;}
    public int getVersion(){return version;} public void setVersion(int v){version=v;}
    public Timestamp getCreatedAt(){return createdAt;} public void setCreatedAt(Timestamp v){createdAt=v;}
    public Timestamp getUpdatedAt(){return updatedAt;} public void setUpdatedAt(Timestamp v){updatedAt=v;}
}
