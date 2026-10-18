package com.qrid.dao;

import com.qrid.config.Database;import com.qrid.model.Identity;
import java.sql.*;import java.util.*;

public class IdentityDao {
 public long insert(Identity x,byte[] photo,String mime,long createdBy)throws SQLException{
  String q="INSERT INTO identities(identity_no,full_name,email,person_type,department,course,photo,photo_mime,status,valid_from,valid_until,qr_token,version,created_by) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,1,?)";
  try(Connection c=Database.getConnection();PreparedStatement p=c.prepareStatement(q,Statement.RETURN_GENERATED_KEYS)){fill(p,x);p.setBytes(7,photo);p.setString(8,mime);p.setString(9,x.getStatus());p.setDate(10,x.getValidFrom());p.setDate(11,x.getValidUntil());p.setString(12,x.getQrToken());p.setLong(13,createdBy);p.executeUpdate();try(ResultSet r=p.getGeneratedKeys()){r.next();return r.getLong(1);}}
 }
 public void update(Identity x,byte[] photo,String mime)throws SQLException{
  String base="UPDATE identities SET identity_no=?,full_name=?,email=?,person_type=?,department=?,course=?,status=?,valid_from=?,valid_until=?";
  String q=photo==null?base+" WHERE id=?":base+",photo=?,photo_mime=? WHERE id=?";
  try(Connection c=Database.getConnection();PreparedStatement p=c.prepareStatement(q)){p.setString(1,x.getIdentityNo());p.setString(2,x.getFullName());p.setString(3,x.getEmail());p.setString(4,x.getPersonType());p.setString(5,x.getDepartment());p.setString(6,x.getCourse());p.setString(7,x.getStatus());p.setDate(8,x.getValidFrom());p.setDate(9,x.getValidUntil());if(photo==null)p.setLong(10,x.getId());else{p.setBytes(10,photo);p.setString(11,mime);p.setLong(12,x.getId());}p.executeUpdate();}
 }
 private void fill(PreparedStatement p,Identity x)throws SQLException{p.setString(1,x.getIdentityNo());p.setString(2,x.getFullName());p.setString(3,x.getEmail());p.setString(4,x.getPersonType());p.setString(5,x.getDepartment());p.setString(6,x.getCourse());}
 public Identity find(long id)throws SQLException{return one("SELECT * FROM identities WHERE id=?",id);}
 public Identity findByToken(String token)throws SQLException{return one("SELECT * FROM identities WHERE qr_token=?",token);}
 private Identity one(String q,Object v)throws SQLException{try(Connection c=Database.getConnection();PreparedStatement p=c.prepareStatement(q)){p.setObject(1,v);try(ResultSet r=p.executeQuery()){return r.next()?map(r):null;}}}
 public List<Identity> list(String search,String status,String department)throws SQLException{
  List<Identity> out=new ArrayList<>();StringBuilder q=new StringBuilder("SELECT * FROM identities WHERE (identity_no LIKE ? OR full_name LIKE ? OR email LIKE ?)");
  List<Object>a=new ArrayList<>();String s="%"+(search==null?"":search.trim())+"%";a.add(s);a.add(s);a.add(s);
  if(status!=null&&!status.isBlank()){q.append(" AND status=?");a.add(status);}if(department!=null&&!department.isBlank()){q.append(" AND department=?");a.add(department);}q.append(" ORDER BY created_at DESC");
  try(Connection c=Database.getConnection();PreparedStatement p=c.prepareStatement(q.toString())){for(int i=0;i<a.size();i++)p.setObject(i+1,a.get(i));try(ResultSet r=p.executeQuery()){while(r.next())out.add(map(r));}}return out;
 }
 public void status(long id,String status)throws SQLException{try(Connection c=Database.getConnection();PreparedStatement p=c.prepareStatement("UPDATE identities SET status=? WHERE id=?")){p.setString(1,status);p.setLong(2,id);p.executeUpdate();}}
 public void regenerate(long id,String token)throws SQLException{try(Connection c=Database.getConnection();PreparedStatement p=c.prepareStatement("UPDATE identities SET qr_token=?,version=version+1 WHERE id=?")){p.setString(1,token);p.setLong(2,id);p.executeUpdate();}}
 public void delete(long id)throws SQLException{try(Connection c=Database.getConnection();PreparedStatement p=c.prepareStatement("DELETE FROM identities WHERE id=?")){p.setLong(1,id);p.executeUpdate();}}
 public byte[] photo(long id)throws SQLException{return bytes("SELECT photo FROM identities WHERE id=?",id);}
 public String photoMime(long id)throws SQLException{try(Connection c=Database.getConnection();PreparedStatement p=c.prepareStatement("SELECT photo_mime FROM identities WHERE id=?")){p.setLong(1,id);try(ResultSet r=p.executeQuery()){return r.next()?r.getString(1):null;}}}
 public void savePdf(long identityId,int version,byte[] data)throws SQLException{try(Connection c=Database.getConnection();PreparedStatement p=c.prepareStatement("INSERT INTO generated_cards(identity_id,version,pdf_data) VALUES(?,?,?) ON DUPLICATE KEY UPDATE pdf_data=VALUES(pdf_data),generated_at=CURRENT_TIMESTAMP")){p.setLong(1,identityId);p.setInt(2,version);p.setBytes(3,data);p.executeUpdate();}}
 private byte[] bytes(String q,long id)throws SQLException{try(Connection c=Database.getConnection();PreparedStatement p=c.prepareStatement(q)){p.setLong(1,id);try(ResultSet r=p.executeQuery()){return r.next()?r.getBytes(1):null;}}}
 private Identity map(ResultSet r)throws SQLException{Identity x=new Identity();x.setId(r.getLong("id"));x.setIdentityNo(r.getString("identity_no"));x.setFullName(r.getString("full_name"));x.setEmail(r.getString("email"));x.setPersonType(r.getString("person_type"));x.setDepartment(r.getString("department"));x.setCourse(r.getString("course"));x.setPhotoMime(r.getString("photo_mime"));x.setStatus(r.getString("status"));x.setValidFrom(r.getDate("valid_from"));x.setValidUntil(r.getDate("valid_until"));x.setQrToken(r.getString("qr_token"));x.setVersion(r.getInt("version"));x.setCreatedAt(r.getTimestamp("created_at"));x.setUpdatedAt(r.getTimestamp("updated_at"));return x;}
}
