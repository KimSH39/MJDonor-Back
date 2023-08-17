package com.db;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConnectDB {
    private static ConnectDB instance = new ConnectDB();

    public static ConnectDB getInstance() {
        return instance;
    }
    public ConnectDB() {  }

    // oracle 계정
    String jdbcUrl = "jdbc:oracle:thin:@220.66.233.107:50559:xe";
    String userId = "MJDONOR";
    String userPw = "2023mjdonor";
    
    Connection conn = null;
    PreparedStatement pstmt = null;
    PreparedStatement pstmt2 = null;
    ResultSet rs = null;

    String sql = "";
    String sql2 = "";
    String returns = "a";

    public String connectionDB(String id, String pwd) { // 테스트 코드
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            conn = DriverManager.getConnection(jdbcUrl, userId, userPw);

            sql = "SELECT id FROM userTBL WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, id);

            rs = pstmt.executeQuery();
            if (rs.next()) {
                returns = "이미 존재하는 아이디 입니다.";
            } else {
                sql2 = "INSERT INTO userTBL VALUES(?,?)";
                pstmt2 = conn.prepareStatement(sql2);
                pstmt2.setString(1, id);
                pstmt2.setString(2, pwd);
                pstmt2.executeUpdate();
                returns = "회원 가입 성공 !";
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (pstmt2 != null)try {pstmt2.close();    } catch (SQLException ex) {}
            if (pstmt != null)try {pstmt.close();} catch (SQLException ex) {}
            if (conn != null)try {conn.close();    } catch (SQLException ex) {    }
        }
        return returns;
    }
    
    public String getProjectInfo() { // 메인 페이지, 프로젝트 정보 불러옴
        StringBuilder result = new StringBuilder();
        
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            conn = DriverManager.getConnection(jdbcUrl, userId, userPw);

            sql = "SELECT p_name, image1, target_point, o_name FROM (" +
                  "SELECT p.Name as p_name, p.image1, p.target_point, o.name as o_name, p.success, p.start_date " +
                  "FROM project p " +
                  "INNER JOIN organization o ON p.organization_id = o.o_id " +
                  "ORDER BY p.p_id) " +
                  "WHERE start_date < sysdate and success = 0 and ROWNUM <= 3";
            
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                String pName = rs.getString("p_name");
                String image1 = rs.getString("image1");
                double targetPoint = rs.getDouble("target_point");
                String oName = rs.getString("o_name");
                
                result.append("Project Name: ").append(pName)
                      .append(", Image: ").append(image1)
                      .append(", Target Point: ").append(targetPoint)
                      .append(", Organization Name: ").append(oName)
                      .append("<br>");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Close resources
            if (rs != null) try { rs.close(); } catch (SQLException ex) {}
            if (pstmt != null) try { pstmt.close(); } catch (SQLException ex) {}
            if (conn != null) try { conn.close(); } catch (SQLException ex) {}
        }
        
        return result.toString();
    }
    
    public String getSpecificProjectInfo(int projectId) { // 특정 기부 프로젝트 정보
        StringBuilder result = new StringBuilder();
        
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            conn = DriverManager.getConnection(jdbcUrl, userId, userPw);

            sql = "SELECT p.image1, p.Name, p.target_point, o.name, p.description, p.current_point " +
                  "FROM project p " +
                  "INNER JOIN organization o ON p.organization_id = o.o_id " +
                  "WHERE p.p_id = ?";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, projectId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String image1 = rs.getString("image1");
                String pName = rs.getString("Name");
                double targetPoint = rs.getDouble("target_point");
                String oName = rs.getString("name");
                String description = rs.getString("description");
                double currentPoint = rs.getDouble("current_point");
                
                result.append("Image: ").append(image1)
                      .append(", Project Name: ").append(pName)
                      .append(", Target Point: ").append(targetPoint)
                      .append(", Organization Name: ").append(oName)
                      .append(", Description: ").append(description)
                      .append(", Current Point: ").append(currentPoint);
            } else {
                result.append("Project not found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Close resources
            if (rs != null) try { rs.close(); } catch (SQLException ex) {}
            if (pstmt != null) try { pstmt.close(); } catch (SQLException ex) {}
            if (conn != null) try { conn.close(); } catch (SQLException ex) {}
        }
        
        return result.toString();
    }
    
    public int getProjectSuccess(int projectId) { // 진행 여부를 물어 진행중이라면 기부가 가능하도록 설계 success = 0 진행중, 나머지는 진행중 x
        int success = -1; // Default value indicating failure
        
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            conn = DriverManager.getConnection(jdbcUrl, userId, userPw);

            sql = "SELECT success FROM project WHERE p_id = ?";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, projectId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                success = rs.getInt("success");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Close resources
            if (rs != null) try { rs.close(); } catch (SQLException ex) {}
            if (pstmt != null) try { pstmt.close(); } catch (SQLException ex) {}
            if (conn != null) try { conn.close(); } catch (SQLException ex) {}
        }
        
        return success;
    }
    
    public int getDonationCount(int projectId) { // 프로젝트에 기부한 사람들 수
        int donationCount = -1; // Default value indicating failure
        
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            conn = DriverManager.getConnection(jdbcUrl, userId, userPw);

            sql = "SELECT COUNT(distinct user_id) FROM donation WHERE p_id = ? and deposit = 1";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, projectId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                donationCount = rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Close resources
            if (rs != null) try { rs.close(); } catch (SQLException ex) {}
            if (pstmt != null) try { pstmt.close(); } catch (SQLException ex) {}
            if (conn != null) try { conn.close(); } catch (SQLException ex) {}
        }
        
        return donationCount;
    }
    
    public String getDonorPhoto(int projectId) { // 프로젝트에 기부한 사람들 프로필 사진 5개
        StringBuilder result = new StringBuilder();
        
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            conn = DriverManager.getConnection(jdbcUrl, userId, userPw);

            sql = "SELECT DISTINCT u.u_id, u.photo " +
                  "FROM donation d " +
                  "INNER JOIN users u ON d.user_id = u.u_id " +
                  "WHERE d.p_id = ? " +
                  "AND ROWNUM <= 5";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, projectId);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                String uId = rs.getString("u_id");
                String photo = rs.getString("photo");
                
                result.append("User ID: ").append(uId)
                      .append(", Photo: ").append(photo)
                      .append("<br>");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Close resources
            if (rs != null) try { rs.close(); } catch (SQLException ex) {}
            if (pstmt != null) try { pstmt.close(); } catch (SQLException ex) {}
            if (conn != null) try { conn.close(); } catch (SQLException ex) {}
        }
        
        return result.toString();
    }

    public String getIDoDonateInfo(int projectId) { // 기부하겠습니다 페이지
        StringBuilder result = new StringBuilder();
        
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            conn = DriverManager.getConnection(jdbcUrl, userId, userPw);

            sql = "SELECT p.image2, o.name as org_name, p.Name as proj_name " +
                  "FROM project p " +
                  "INNER JOIN organization o ON p.organization_id = o.o_id " +
                  "WHERE p.p_id = ?";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, projectId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String image2 = rs.getString("image2");
                String orgName = rs.getString("org_name");
                String projName = rs.getString("proj_name");
                
                result.append("Image2: ").append(image2)
                      .append(", Organization Name: ").append(orgName)
                      .append(", Project Name: ").append(projName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Close resources
            if (rs != null) try { rs.close(); } catch (SQLException ex) {}
            if (pstmt != null) try { pstmt.close(); } catch (SQLException ex) {}
            if (conn != null) try { conn.close(); } catch (SQLException ex) {}
        }
        
        return result.toString();
    }
    
    public String getDonationList() { // 전체 기부 프로젝트 리스트(하단바 두번째)
        StringBuilder result = new StringBuilder();
        
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            conn = DriverManager.getConnection(jdbcUrl, userId, userPw);

            sql = "SELECT p.NAME, o.NAME AS ORGANIZATION_NAME, p.IMAGE1, p.SUCCESS " +
                  "FROM Project p " +
                  "INNER JOIN ORGANIZATION o ON p.ORGANIZATION_ID = o.O_ID " +
                  "ORDER BY p.SUCCESS, p.P_ID DESC";
            
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                String projectName = rs.getString("NAME");
                String organizationName = rs.getString("ORGANIZATION_NAME");
                String image1 = rs.getString("IMAGE1");
                int success = rs.getInt("SUCCESS");
                
                result.append("Project Name: ").append(projectName)
                      .append(", Organization Name: ").append(organizationName)
                      .append(", Image1: ").append(image1)
                      .append(", Success: ").append(success)
                      .append("<br>");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Close resources
            if (rs != null) try { rs.close(); } catch (SQLException ex) {}
            if (pstmt != null) try { pstmt.close(); } catch (SQLException ex) {}
            if (conn != null) try { conn.close(); } catch (SQLException ex) {}
        }
        
        return result.toString();
    }

}