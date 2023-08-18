<%@page import="com.db.ConnectDB"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%
    ConnectDB connectDB = ConnectDB.getInstance();

    // 한글 인코딩 부분
    request.setCharacterEncoding("utf-8");
    String u_id = request.getParameter("u_id");
    
    // Retrieve user information based on u_id
    String userInfo = connectDB.getUserInfo(u_id);
    out.println(userInfo);
%>
