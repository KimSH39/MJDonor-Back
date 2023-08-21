<%@page import="com.db.ConnectDB"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%
    ConnectDB connectDB = ConnectDB.getInstance();

    // 한글 인코딩 부분
    request.setCharacterEncoding("utf-8");
    String v_a = request.getParameter("virtual account");
    
    // Retrieve user information based on u_id
    String deposit = connectDB.deposit(v_a);
    out.println(deposit);
    
    String updateCurrent = connectDB.updateCurrent(v_a);
    out.println(updateCurrent);
    
    String updateSuccess = connectDB.updateSuccess(v_a);
    out.println(updateSuccess);
%>
