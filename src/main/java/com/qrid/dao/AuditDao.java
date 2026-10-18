package com.qrid.dao;
import com.qrid.config.Database;
import java.sql.*;import java.util.*;
public class AuditDao {
 public void log(Long userId,String action,String entity,String entityId,String details,String ip){try(Connection c=Database.getConnection();PreparedStatement p=c.prepareStatement("INSERT INTO audit_logs(user_id,action,entity_type,entity_id,details,ip_address) VALUES(?,?,?,?,?,?)")){if(userId==null)p.setNull(1,Types.BIGINT);else p.setLong(1,userId);p.setString(2,action);p.setString(3,entity);p.setString(4,entityId);p.setString(5,details);p.setString(6,ip);p.executeUpdate();}catch(SQLException ignored){}}
 public List<Map<String,Object>> list(int limit)throws SQLException{return rows("SELECT a.id,u.name user_name,a.action,a.entity_type,a.entity_id,a.details,a.ip_address,a.created_at FROM audit_logs a LEFT JOIN users u ON u.id=a.user_id ORDER BY a.created_at DESC LIMIT ?",limit);}
 public static List<Map<String,Object>> rows(String sql,Object...args)throws SQLException{List<Map<String,Object>> out=new ArrayList<>();try(Connection c=Database.getConnection();PreparedStatement p=c.prepareStatement(sql)){for(int i=0;i<args.length;i++)p.setObject(i+1,args[i]);try(ResultSet r=p.executeQuery()){ResultSetMetaData m=r.getMetaData();while(r.next()){Map<String,Object>x=new LinkedHashMap<>();for(int i=1;i<=m.getColumnCount();i++)x.put(m.getColumnLabel(i),r.getObject(i));out.add(x);}}}return out;}
 public static long count(String sql,Object...args)throws SQLException{try(Connection c=Database.getConnection();PreparedStatement p=c.prepareStatement(sql)){for(int i=0;i<args.length;i++)p.setObject(i+1,args[i]);try(ResultSet r=p.executeQuery()){return r.next()?r.getLong(1):0;}}}
}
