package com.qrid.dao;
import com.qrid.config.Database;import java.sql.*;import java.util.*;
public class ScanDao {
 public void record(Long identityId,Long verifierId,String gate,String result,String ip)throws SQLException{try(Connection c=Database.getConnection();PreparedStatement p=c.prepareStatement("INSERT INTO scan_logs(identity_id,verified_by,gate_location,result,ip_address) VALUES(?,?,?,?,?)")){if(identityId==null)p.setNull(1,Types.BIGINT);else p.setLong(1,identityId);if(verifierId==null)p.setNull(2,Types.BIGINT);else p.setLong(2,verifierId);p.setString(3,gate);p.setString(4,result);p.setString(5,ip);p.executeUpdate();}}
 public List<Map<String,Object>> list(int limit,String department)throws SQLException{String q="SELECT s.id,i.identity_no,i.full_name,i.department,s.gate_location,s.result,u.name verified_by,s.scanned_at FROM scan_logs s LEFT JOIN identities i ON i.id=s.identity_id LEFT JOIN users u ON u.id=s.verified_by"+(department==null?"":" WHERE i.department=?")+" ORDER BY s.scanned_at DESC LIMIT ?";return department==null?AuditDao.rows(q,limit):AuditDao.rows(q,department,limit);}
 public List<Map<String,Object>> weekly()throws SQLException{return AuditDao.rows("SELECT DATE(scanned_at) day,COUNT(*) total,SUM(result='VALID') valid_count FROM scan_logs WHERE scanned_at>=CURRENT_DATE-INTERVAL 6 DAY GROUP BY DATE(scanned_at) ORDER BY day");}
}
