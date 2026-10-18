package com.qrid.dao;

import com.qrid.config.Database;
import com.qrid.model.User;
import java.sql.*;
import java.util.*;

public class UserDao {
    public User authenticate(String email, String password) throws SQLException {
        String sql="SELECT id,name,email,password_hash,role,department,status FROM users WHERE email=?";
        try(Connection c=Database.getConnection();PreparedStatement p=c.prepareStatement(sql)){
            p.setString(1,email.toLowerCase(Locale.ROOT));
            try(ResultSet r=p.executeQuery()){
                if(r.next() && "ACTIVE".equals(r.getString("status")) && com.qrid.util.PasswordUtil.matches(password,r.getString("password_hash"))) return map(r);
            }
        } return null;
    }
    public List<User> list(String search) throws SQLException {
        List<User> out=new ArrayList<>(); String s="%"+(search==null?"":search.trim())+"%";
        String sql="SELECT id,name,email,role,department,status FROM users WHERE name LIKE ? OR email LIKE ? OR department LIKE ? ORDER BY created_at DESC";
        try(Connection c=Database.getConnection();PreparedStatement p=c.prepareStatement(sql)){p.setString(1,s);p.setString(2,s);p.setString(3,s);try(ResultSet r=p.executeQuery()){while(r.next())out.add(map(r));}} return out;
    }
    public void save(long id,String name,String email,String passwordHash,String role,String department,String status) throws SQLException {
        if(id==0){
            try(Connection c=Database.getConnection();PreparedStatement p=c.prepareStatement("INSERT INTO users(name,email,password_hash,role,department,status) VALUES(?,?,?,?,?,?)")){
                p.setString(1,name);p.setString(2,email.toLowerCase(Locale.ROOT));p.setString(3,passwordHash);p.setString(4,role);p.setString(5,department);p.setString(6,status);p.executeUpdate();
            }
        }else{
            String sql=passwordHash==null?"UPDATE users SET name=?,email=?,role=?,department=?,status=? WHERE id=?":"UPDATE users SET name=?,email=?,role=?,department=?,status=?,password_hash=? WHERE id=?";
            try(Connection c=Database.getConnection();PreparedStatement p=c.prepareStatement(sql)){p.setString(1,name);p.setString(2,email.toLowerCase(Locale.ROOT));p.setString(3,role);p.setString(4,department);p.setString(5,status);if(passwordHash==null)p.setLong(6,id);else{p.setString(6,passwordHash);p.setLong(7,id);}p.executeUpdate();}
        }
    }
    public void toggle(long id,long currentUser) throws SQLException {try(Connection c=Database.getConnection();PreparedStatement p=c.prepareStatement("UPDATE users SET status=IF(status='ACTIVE','SUSPENDED','ACTIVE') WHERE id=? AND id<>?")){p.setLong(1,id);p.setLong(2,currentUser);p.executeUpdate();}}
    public User find(long id)throws SQLException{try(Connection c=Database.getConnection();PreparedStatement p=c.prepareStatement("SELECT id,name,email,role,department,status FROM users WHERE id=?")){p.setLong(1,id);try(ResultSet r=p.executeQuery()){return r.next()?map(r):null;}}}
    private User map(ResultSet r)throws SQLException{User u=new User();u.setId(r.getLong("id"));u.setName(r.getString("name"));u.setEmail(r.getString("email"));u.setRole(r.getString("role"));u.setDepartment(r.getString("department"));u.setStatus(r.getString("status"));return u;}
}
