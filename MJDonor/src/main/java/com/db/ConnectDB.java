package com.db;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Base64.Encoder;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import oracle.sql.DATE;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Calendar;

public class ConnectDB {
    private static ConnectDB instance = new ConnectDB();

    public static ConnectDB getInstance() {
        return instance;
    }
    
    
    public ConnectDB() {  }

    // oracle 계정
    String jdbcUrl = "jdbc:oracle:thin:@220.66.233.107:50559:xe";
    String userOId = "MJDONOR";
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
            conn = DriverManager.getConnection(jdbcUrl, userOId, userPw);

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
    
    public String performSignup(int u_id, String email, String name, String password, String wallet, String photo) { // 회원가입 코드
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            conn = DriverManager.getConnection(jdbcUrl, userOId, userPw);

            // Check if the user with the given u_id already exists
            sql = "SELECT u_id FROM users WHERE u_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, u_id);

            rs = pstmt.executeQuery();
            if (rs.next()) {
                returns = "이미 존재하는 아이디 입니다.";
            } else {
                // Insert the new user data into the database
                sql2 = "INSERT INTO users (email, password, name, u_id, wallet, photo) VALUES (?, ?, ?, ?, ?, ?)";
                pstmt2 = conn.prepareStatement(sql2);
                
                pstmt2.setString(1, email);
                pstmt2.setString(2, password);
                pstmt2.setString(3, name);
                pstmt2.setInt(4, u_id);
                pstmt2.setString(5, wallet);
                pstmt2.setString(6, photo);
                pstmt2.executeUpdate();
                returns = "회원 가입 성공 !";
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (pstmt2 != null) try { pstmt2.close(); } catch (SQLException ex) {}
            if (pstmt != null) try { pstmt.close(); } catch (SQLException ex) {}
            if (conn != null) try { conn.close(); } catch (SQLException ex) {}
        }
        return returns;
    }
    
    // 기부 등록 페이지
    public String performRegister(String name, String description, int target_point, String start_date, String end_date, String image1, String image2, String category, int ORGANIZATION_ID, int REGISTRANT_ID) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        String resultMessage = "";

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            conn = DriverManager.getConnection(jdbcUrl, userOId, userPw);
            
         // Calculate new_p_id
            int new_p_id = 0;
            String selectMaxIdSQL = "SELECT COALESCE(MAX(P_ID) + 1, 1) AS NEW_P_ID FROM Project";
            try (PreparedStatement selectMaxIdStmt = conn.prepareStatement(selectMaxIdSQL);
                 ResultSet resultSet = selectMaxIdStmt.executeQuery()) {
                if (resultSet.next()) {
                	new_p_id = resultSet.getInt("new_p_id");
                }
            }
            
            int success = 0;
            int current_point = 0;

            // Example: Insert registration data into the database
            String sql = "INSERT INTO PROJECT (p_id, name, description, target_point, start_date, end_date, image1, image2, category, ORGANIZATION_ID, REGISTRANT_ID, current_point, success) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, new_p_id);
            pstmt.setString(2, name);
            pstmt.setString(3, description);
            pstmt.setInt(4, target_point);
            pstmt.setDate(5, java.sql.Date.valueOf(start_date));
            pstmt.setDate(6, java.sql.Date.valueOf(end_date));
            pstmt.setString(7, image1);
            pstmt.setString(8, image2);
            pstmt.setString(9, category);
            pstmt.setInt(10, ORGANIZATION_ID);
            pstmt.setInt(11, REGISTRANT_ID);
            pstmt.setInt(12, current_point);
            pstmt.setInt(13, success);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                resultMessage = "Registration successful!";
            } else {
                resultMessage = "Failed to register.";
            }
        } catch (Exception e) {
            e.printStackTrace();
            resultMessage = "An error occurred during registration.";
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        return resultMessage;
    }
    
    public String getProjectInfo() { // 메인 페이지, 프로젝트 정보 불러옴
        StringBuilder result = new StringBuilder();
        
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            conn = DriverManager.getConnection(jdbcUrl, userOId, userPw);

            sql = "SELECT p.p_id, p.ORGANIZATION_ID, p.name AS project_name, o.name AS organization_name, p.image1, p.image2, p.DESCRIPTION, p.start_date, p.end_date, p.target_point, p.current_point "
            	      + "FROM Project p "
            	      + "INNER JOIN ORGANIZATION o ON p.ORGANIZATION_ID = o.O_ID "
            	      + "WHERE p.start_date < sysdate AND p.success = 0 AND ROWNUM <= 3 "
            	      + "ORDER BY p.p_id";
            
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
            	int p_id = rs.getInt("p_id");
            	int ORGANIZATION_ID = rs.getInt("ORGANIZATION_ID");
            	String project_name = rs.getString("project_name");
            	String organization_name = rs.getString("organization_name");
            	String image1 = rs.getString("image1");
            	String image2 = rs.getString("image2");
            	String DESCRIPTION = rs.getString("DESCRIPTION");
            	String start_date = rs.getString("start_date");
            	String end_date = rs.getString("end_date");
            	double target_point = rs.getDouble("target_point");
            	double current_point = rs.getDouble("current_point");
                
                result.append("p_id: ").append(p_id)
                		.append(", ORGANIZATION_ID ").append(ORGANIZATION_ID)
                		.append(", Project Name: ").append(project_name)
                .append(", organization_name: ").append(organization_name)
                      .append(", Image1: ").append(image1)
                      .append(", Image2: ").append(image2)
                      .append(", DESCRIPTION: ").append(DESCRIPTION)
                      .append(", start_date: ").append(start_date)
                      .append(", end_date: ").append(end_date)
                      .append(", Target Point: ").append(target_point)
                      .append(", current_point: ").append(current_point)
                      .append("<br>")
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
            conn = DriverManager.getConnection(jdbcUrl, userOId, userPw);

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
            conn = DriverManager.getConnection(jdbcUrl, userOId, userPw);

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
            conn = DriverManager.getConnection(jdbcUrl, userOId, userPw);

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
            conn = DriverManager.getConnection(jdbcUrl, userOId, userPw);

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
            conn = DriverManager.getConnection(jdbcUrl, userOId, userPw);

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
            conn = DriverManager.getConnection(jdbcUrl, userOId, userPw);

            String sql = "SELECT p.p_id, p.ORGANIZATION_ID, p.name AS project_name, o.name AS organization_name, p.image1, p.image2, p.DESCRIPTION, p.category, p.end_date, p.target_point, p.current_point "
            	      + "FROM Project p "
              	      + "INNER JOIN ORGANIZATION o ON p.ORGANIZATION_ID = o.O_ID "
              	      + "where p.category = '저소득층' and p.success= 0"
              	      + "ORDER BY p.p_id";
            
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
            	int p_id = rs.getInt("p_id");
            	int ORGANIZATION_ID = rs.getInt("ORGANIZATION_ID");
            	String project_name = rs.getString("project_name");
            	String organization_name = rs.getString("organization_name");
            	String image1 = rs.getString("image1");
            	String image2 = rs.getString("image2");
            	String DESCRIPTION = rs.getString("DESCRIPTION");
            	String category = rs.getString("category");
            	String end_date = rs.getString("end_date");
            	double target_point = rs.getDouble("target_point");
            	double current_point = rs.getDouble("current_point");
                
                result.append("p_id: ").append(p_id)
                		.append(", ORGANIZATION_ID ").append(ORGANIZATION_ID)
                		.append(", Project Name: ").append(project_name)
                .append(", organization_name: ").append(organization_name)
                      .append(", Image1: ").append(image1)
                      .append(", Image2: ").append(image2)
                      .append(", DESCRIPTION: ").append(DESCRIPTION)
                      .append(", start_date: ").append(category)
                      .append(", end_date: ").append(end_date)
                      .append(", Target Point: ").append(target_point)
                      .append(", current_point: ").append(current_point)
                      .append("<br>")
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
    
    public String getIRegisteredDonationList(String userId) { // 내가 등록한 기부 목록
        StringBuilder result = new StringBuilder();
        
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            conn = DriverManager.getConnection(jdbcUrl, userOId, userPw);

            sql = "SELECT p.NAME, o.NAME AS ORGANIZATION_NAME, p.IMAGE1, p.SUCCESS " +
                  "FROM Project p " +
                  "INNER JOIN ORGANIZATION o ON p.ORGANIZATION_ID = o.O_ID " +
                  "WHERE p.REGISTRANT_ID = ? " +
                  "ORDER BY p.SUCCESS, p.P_ID DESC";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);
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
    
    public String getContributedDonationList(String userId) { // 내가 기부한 기부 목록
        StringBuilder result = new StringBuilder();
        
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            conn = DriverManager.getConnection(jdbcUrl, userOId, userPw);

            sql = "SELECT p.Name, p.image1, o.name, d.deposit, d.vaccount, p.end_date, d.point, d.limit " +
                  "FROM donation d " +
                  "INNER JOIN (project p " +
                  "            INNER JOIN organization o ON p.organization_id = o.o_id) " +
                  "ON d.P_ID = p.P_ID AND d.user_id = ? " +
                  "ORDER BY d.deposit, p.success, d.limit, p.end_date";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                String projectName = rs.getString("Name");
                String image1 = rs.getString("image1");
                String organizationName = rs.getString("name");
                int deposit = rs.getInt("deposit");
                String vaccount = rs.getString("vaccount");
                String endDate = rs.getString("end_date");
                int point = rs.getInt("point");
                Date donationLimit = rs.getDate("limit");
                
                result.append("Project Name: ").append(projectName)
                      .append(", Image1: ").append(image1)
                      .append(", Organization Name: ").append(organizationName)
                      .append(", Deposit: ").append(deposit)
                      .append(", Virtual Account: ").append(vaccount)
                      .append(", End Date: ").append(endDate)
                      .append(", Point: ").append(point)
                      .append(", Donation Limit: ").append(donationLimit)
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

    public String getUserInfo(String userId) { // 마이페이지 사용자 정보
        StringBuilder result = new StringBuilder();
        
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            conn = DriverManager.getConnection(jdbcUrl, userOId, userPw);

            sql = "SELECT photo, name, u_id " +
                  "FROM users " +
                  "WHERE u_id = ?";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String photo = rs.getString("photo");
                String name = rs.getString("name");
                String uId = rs.getString("u_id");
                
                result.append("Photo: ").append(photo)
                      .append(", Name: ").append(name)
                      .append(", User ID: ").append(uId);
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
    
    public int getDonationCountForUser(String userId) { // 내가 기부한 프로젝트 count
        int donationCount = 0;
        
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            conn = DriverManager.getConnection(jdbcUrl, userOId, userPw);

            sql = "SELECT count(distinct vaccount) " +
                  "FROM donation " +
                  "WHERE user_id = ?";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);
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
    
    public int getSumDonationPointForUser(String userId) { // 내가 총 기부한 금액
        int sumDonationPoint = 0;
        
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            conn = DriverManager.getConnection(jdbcUrl, userOId, userPw);

            sql = "SELECT sum(point) " +
                  "FROM donation " +
                  "WHERE user_id = ? AND refund_state = 0 AND deposit = 1";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                sumDonationPoint = rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Close resources
            if (rs != null) try { rs.close(); } catch (SQLException ex) {}
            if (pstmt != null) try { pstmt.close(); } catch (SQLException ex) {}
            if (conn != null) try { conn.close(); } catch (SQLException ex) {}
        }
        
        return sumDonationPoint;
    }
    
    public String getEmail(int u_id) { // 이메일 받아오기
        String email = " ";
        
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            conn = DriverManager.getConnection(jdbcUrl, userOId, userPw);

            sql = "SELECT email " +
                  "FROM users " +
                  "WHERE u_id = ? ";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, u_id);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
            	email = rs.getString(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Close resources
            if (rs != null) try { rs.close(); } catch (SQLException ex) {}
            if (pstmt != null) try { pstmt.close(); } catch (SQLException ex) {}
            if (conn != null) try { conn.close(); } catch (SQLException ex) {}
        }
        
        return email;
    }
    
    
    public JSONObject createVirtualAccount(String point, String email, String nick, String project, String due, String rbank, String r_a) throws Exception {
        String path = "http://127.0.0.1/Samples";
        String orderId = "virtualaccount-" + String.valueOf(System.currentTimeMillis());
        String amount = String.valueOf(point); // :point로 android에서 입력
        String customerEmail = email; // :email로 android에서 입력
        String customerName = nick; // :nick로 android에서 입력
        String orderName = project; //프로젝트명
        String bank = rbank; //가상계좌 은행
        Date dueDate = java.sql.Date.valueOf(due); //유효날짜 :limit
        String dueDateStr = new SimpleDateFormat("yyyy-MM-dd").format(dueDate); // Convert Date to String
        String virtualAccountCallbackUrl = path + "/va_callback.jsp";
        String customerMobilePhone = "01039812239"; //핸드폰 번호인데 여기다 본인 핸드폰 번호넣으면 문자 감
        String useEscrow = "false";
        
        String type = "소득공제";
        String registrationNumber = "01039812239"; 
        
        String refundbank = rbank; // 환불받을 계좌은행
        String accountNumber = r_a; //환불받을 계좌번호
        String holderName = "MJDonor";
        
        String secretKey = "test_ak_ZORzdMaqN3wQd5k6ygr5AkYXQGwy:";

        Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode(secretKey.getBytes("UTF-8"));
        String authorizations = "Basic " + new String(encodedBytes, 0, encodedBytes.length);
        
        URL url = new URL("https://api.tosspayments.com/v1/virtual-accounts");

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization", authorizations);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        JSONObject obj = new JSONObject();
        obj.put("orderId", orderId);
        obj.put("amount", amount);
        obj.put("customerEmail", customerEmail);
        obj.put("customerName", customerName);
        obj.put("orderName", orderName);
        obj.put("bank", "국민");
        obj.put("dueDate", dueDateStr);
        obj.put("virtualAccountCallbackUrl", virtualAccountCallbackUrl);
        obj.put("customerMobilePhone", customerMobilePhone);
        obj.put("useEscrow", useEscrow);
        
        System.out.println("orderId: " + orderId);
        System.out.println("amount: " + amount);
        System.out.println("customerEmail: " + customerEmail);
        System.out.println("customerName: " + customerName);
        System.out.println("orderName: " + orderName);
        System.out.println("bank: " + bank);
        System.out.println("dueDate: " + dueDateStr);
        System.out.println("virtualAccountCallbackUrl: " + virtualAccountCallbackUrl);
        System.out.println("customerMobilePhone: " + customerMobilePhone);
        System.out.println("useEscrow: " + useEscrow);
        
        System.out.println("obj: " + obj);
        
        JSONObject cashReceipt = new JSONObject();
        cashReceipt.put("type", type);
        cashReceipt.put("registrationNumber", registrationNumber);

        obj.put("cashReceipt", cashReceipt);
        
        
        JSONObject refundReceiveAccount = new JSONObject();
        refundReceiveAccount.put("bank", bank);
        refundReceiveAccount.put("accountNumber", r_a);
        refundReceiveAccount.put("holderName", holderName);
        
        obj.put("refundReceiveAccount", refundReceiveAccount);


        OutputStream outputStream = connection.getOutputStream();
        outputStream.write(obj.toString().getBytes("UTF-8"));

        int code = connection.getResponseCode();
        boolean isSuccess = code == 200;
        
        InputStream responseStream = isSuccess ? connection.getInputStream() : connection.getErrorStream();

        Reader reader = new InputStreamReader(responseStream, StandardCharsets.UTF_8);
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(reader);
        responseStream.close();
        
        return jsonObject;
    }
	
	    public String getRefundInfo() { // 환불해야할 donation 정보를 프로젝트명과 같이 불러
	        StringBuilder result = new StringBuilder();
	        
	        try {
	            Class.forName("oracle.jdbc.driver.OracleDriver");
	            conn = DriverManager.getConnection(jdbcUrl, userOId, userPw);

	            sql = "select name, NICKNAME, DONATE_DATE, VACCOUNT, LIMIT "
	            		+ "from donation d inner join project p on d.p_id = p.p_id"
	            		+ "where refund_state =1";
	            
	            pstmt = conn.prepareStatement(sql);
	            rs = pstmt.executeQuery();
	            
	            while (rs.next()) {
	                String pName = rs.getString("name");
	                String nick = rs.getString("NICKNAME");
	                String DONATE_DATE = rs.getString("DONATE_DATE");
	                String VACCOUNT = rs.getString("VACCOUNT");
	                String LIMIT = rs.getString("LIMIT");
	                
	                result.append("Project Name: ").append(pName)
	                	  .append(", NICKNAME: ").append(nick)
	                      .append(", DONATE DATE: ").append(DONATE_DATE)
	                      .append(", VACCOUNT: ").append(VACCOUNT)
	                      .append(", LIMIT: ").append(LIMIT)
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
	    
	    public String updateRefund() { // refund_state 업데이트 함수
	    	String resultMessage = "";
	        try {
	            Class.forName("oracle.jdbc.driver.OracleDriver");
	            conn = DriverManager.getConnection(jdbcUrl, userOId, userPw);
	            

	            sql = "update donation "
	            		+ "set refund_state = 2 "
	            		+ "where refund_state =1";
	       
	            pstmt = conn.prepareStatement(sql); 
	            
	            
	            int rowsAffected = pstmt.executeUpdate();
	            if (rowsAffected > 0) {
	                resultMessage = "refund_state 업데이트!";
	            } else {
	                resultMessage = "변화 없음!";
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        } finally {
	            if (pstmt != null)try {pstmt.close();} catch (SQLException ex) {}
	            if (conn != null)try {conn.close();    } catch (SQLException ex) {    }
	        }
			return resultMessage;
	        
	        
	    }
	    
	    public String performDonation(int u_id, int p_id, String nick, int point, String rbank, String refund, String msg, String v_a, String limit) {
	    	// 기부하기 데이터 삽입
	    	String resultMessage = "";
	        try {
	            Class.forName("oracle.jdbc.driver.OracleDriver");
	            conn = DriverManager.getConnection(jdbcUrl, userOId, userPw);
	            

	            String sql = "INSERT INTO DONATION (USER_ID, P_ID, DONATE_DATE, NICKNAME, POINT, REFUND, MSG, RBANK, VACCOUNT, LIMIT, DEPOSIT, REFUND_STATE) " +
                        "VALUES (?, ?, sysdate, ?, ?, ?, ?, ?, ?, ?, ?, 0, 0)";
	            
	            pstmt = conn.prepareStatement(sql);
	            pstmt.setInt(1, u_id);
	            pstmt.setInt(2, p_id);
	            pstmt.setString(3, nick);
	            pstmt.setInt(4, point);
	            pstmt.setString(5, refund);
	            pstmt.setString(6, msg);
	            pstmt.setString(7, rbank);
	            pstmt.setString(8, v_a);
	            pstmt.setString(9, limit);

	            
	            
	            int rowsAffected = pstmt.executeUpdate();
	            if (rowsAffected > 0) {
	                resultMessage = "donation 등록!";
	            } else {
	                resultMessage = "실패!";
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        } finally {
	            if (pstmt != null)try {pstmt.close();} catch (SQLException ex) {}
	            if (conn != null)try {conn.close();    } catch (SQLException ex) {    }
	        }
			return resultMessage;
	    }
	    
	    
	    public String deposit(String v_a) { // 입금 시 입금 상태 업데이트 함수
	    	String resultMessage = "";
	        try {
	            Class.forName("oracle.jdbc.driver.OracleDriver");
	            conn = DriverManager.getConnection(jdbcUrl, userOId, userPw);

	            sql = "UPDATE Donation "
	            		+ "SET Deposit = 1 "
	            		+ "WHERE VACCOUNT = ?;";
	            pstmt = conn.prepareStatement(sql);
	            
	            pstmt.setString(1, v_a);
	       
	            int rowsAffected = pstmt.executeUpdate();
	            if (rowsAffected > 0) {
	                resultMessage = "deposit 업데이트!";
	            } else {
	                resultMessage = "변화 없음!";
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        } finally {
	            if (pstmt != null)try {pstmt.close();} catch (SQLException ex) {}
	            if (conn != null)try {conn.close();    } catch (SQLException ex) {    }
	        }
			return returns;
	        
	    }
	    
	    public String updateCurrent(String v_a) { // 입금 시 현재 모금액 업데이트
	        String resultMessage = "";

	        try (Connection conn = DriverManager.getConnection(jdbcUrl, userOId, userPw)) {
	            // Calculate new_p_id
	            int p_id = 0;
	            int point = 0;
	            String p_idSql = "SELECT P_ID, point FROM Donation where VACCOUNT = ?";
	            try (PreparedStatement p_idStmt = conn.prepareStatement(p_idSql)) {
	                p_idStmt.setString(1, v_a);
	                try (ResultSet resultSet = p_idStmt.executeQuery()) {
	                    if (resultSet.next()) {
	                        p_id = resultSet.getInt("p_id");
	                        point = resultSet.getInt("point");
	                    }
	                }
	            }

	            // Example: Insert registration data into the database
	            String sql = "UPDATE PROJECT SET CURRENT_POINT = CURRENT_POINT + ? WHERE P_ID = ?;";
	            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
	                pstmt.setInt(1, point);
	                pstmt.setInt(2, p_id);

	                int rowsAffected = pstmt.executeUpdate();
	                if (rowsAffected > 0) {
	                    resultMessage = "현재 모금액 변화!";
	                } else {
	                    resultMessage = "현재 모금액 업데이트 실패!";
	                }
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	            resultMessage = "An error occurred during update.";
	        }

	        return resultMessage;
	    }

	    public String updateSuccess(String v_a) { //현재 모금액 증가 시 프로젝트 성공체크 업데이트 함수
	        String resultMessage = "";

	        try (Connection conn = DriverManager.getConnection(jdbcUrl, userOId, userPw)) {
	            int p_id = -1;

	            String p_idSql = "SELECT P_ID FROM Donation where VACCOUNT = ?";
	            try (PreparedStatement p_idStmt = conn.prepareStatement(p_idSql)) {
	                p_idStmt.setString(1, v_a);
	                try (ResultSet resultSet = p_idStmt.executeQuery()) {
	                    if (resultSet.next()) {
	                        p_id = resultSet.getInt("p_id");
	                    }
	                }
	            }

	            int t_p = 0;
	            int c_p = -1;
	            String pointSql = "SELECT target_point, current_point FROM PROJECT WHERE P_ID = ?;";
	            try (PreparedStatement pointStmt = conn.prepareStatement(pointSql)) {
	                pointStmt.setInt(1, p_id);
	                try (ResultSet resultSet = pointStmt.executeQuery()) {
	                    if (resultSet.next()) {
	                        t_p = resultSet.getInt("target_point");
	                        c_p = resultSet.getInt("current_point");
	                    }
	                }
	            }

	            if (t_p <= c_p) {
	                String sql = "UPDATE PROJECT SET success = 1 WHERE P_ID = ?;";
	                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
	                    pstmt.setInt(1, p_id);
	                    int rowsAffected = pstmt.executeUpdate();
	                    if (rowsAffected > 0) {
	                        resultMessage = "프로젝트 성공 변화!";
	                    } else {
	                        resultMessage = "변화 없음!";
	                    }
	                }
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	            resultMessage = "An error occurred during update success.";
	        }

	        return resultMessage;
	    }

	  
	    
	    
	    public String updateProject() { // 프로젝트 성공 실패 체크 및 업데이트 함수
	    	String resultMessage = "";
	        try {
	            Class.forName("oracle.jdbc.driver.OracleDriver");
	            conn = DriverManager.getConnection(jdbcUrl, userOId, userPw);

	            sql = "UPDATE Project "
	            		+ "SET SUCCESS = CASE "
	            		+ "                 WHEN END_DATE < SYSDATE AND CURRENT_POINT < TARGET_POINT THEN '2' "
	            		+ "                 ELSE SUCCESS "
	            		+ "              END "
	            		+ "WHERE SUCCESS = '0'; "
	            		+ "DELETE FROM donation "
	            		+ "WHERE deposit = 0 AND P_ID IN (SELECT P_ID FROM Project WHERE SUCCESS = '2'); "
	            		+ "UPDATE donation d "
	            		+ "SET d.refund_state = 1 "
	            		+ "WHERE deposit = 1 AND refund_state = 0 and P_ID IN (SELECT P_ID FROM Project WHERE SUCCESS = '2');";
	            pstmt = conn.prepareStatement(sql);
	       

	            int rowsAffected = pstmt.executeUpdate();
	            if (rowsAffected > 0) {
	                resultMessage = "project 업데이트!";
	            } else {
	                resultMessage = "변화 없음!";
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        } finally {
	            if (pstmt != null)try {pstmt.close();} catch (SQLException ex) {}
	            if (conn != null)try {conn.close();    } catch (SQLException ex) {    }
	        }
			return returns;
	        
	    }
	    
	    public static void main(String[] args) { // 서버가 실행될 때 자동으로  자정마다 프로젝트 기한 체크
	        // Calculate the delay until the next midnight
	        Calendar midnight = Calendar.getInstance();
	        midnight.add(Calendar.DAY_OF_MONTH, 1);
	        midnight.set(Calendar.HOUR_OF_DAY, 0);
	        midnight.set(Calendar.MINUTE, 0);
	        midnight.set(Calendar.SECOND, 0);
	        long delay = midnight.getTimeInMillis() - System.currentTimeMillis();
	        
	        ConnectDB connectDBInstance = ConnectDB.getInstance();

	        // Create a timer task
	        TimerTask task = new TimerTask() {
	            @Override
	            public void run() {
	                // Perform your task here
	                connectDBInstance.updateProject();
	                System.out.println("Queries executed successfully.");
	            }
	        };

	        // Schedule the task to run at midnight daily
	        Timer timer = new Timer();
	        timer.schedule(task, delay, 24 * 60 * 60 * 1000); // 24 hours in milliseconds
	    }
	    
	    public String getProjectName(int projectId) { 
	        String name = " "; 
	        
	        try {
	            Class.forName("oracle.jdbc.driver.OracleDriver");
	            conn = DriverManager.getConnection(jdbcUrl, userOId, userPw);

	            sql = "SELECT name FROM project WHERE p_id = ?";
	            
	            pstmt = conn.prepareStatement(sql);
	            pstmt.setInt(1, projectId);
	            rs = pstmt.executeQuery();
	            
	            if (rs.next()) {
	                name = rs.getString("name");
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        } finally {
	            // Close resources
	            if (rs != null) try { rs.close(); } catch (SQLException ex) {}
	            if (pstmt != null) try { pstmt.close(); } catch (SQLException ex) {}
	            if (conn != null) try { conn.close(); } catch (SQLException ex) {}
	        }
	        
	        return name;
	    }
	    
	    public String getOrganizationInfo() { // 기관 조회
	        StringBuilder result = new StringBuilder();
	        
	        try {
	            Class.forName("oracle.jdbc.driver.OracleDriver");
	            conn = DriverManager.getConnection(jdbcUrl, userOId, userPw);

	            sql = "SELECT name, image " +
	                  "FROM ORGANIZATION";
	            
	            pstmt = conn.prepareStatement(sql);
	            rs = pstmt.executeQuery();
	            
	            while (rs.next()) {
	                String orgName = rs.getString("name");
	                String orgImage = rs.getString("image");
	                
	                result.append("Organization Name: ").append(orgName)
	                      .append(", Image: ").append(orgImage)
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
