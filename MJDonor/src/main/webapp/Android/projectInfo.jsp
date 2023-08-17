<%@page import="com.db.ConnectDB"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%
    ConnectDB connectDB = ConnectDB.getInstance();

    // 한글 인코딩 부분
    request.setCharacterEncoding("utf-8");

    // Retrieve project details using the SQL query
    String projectInfo = connectDB.getProjectInfo(); // Assuming you've added this method in ConnectDB class

    // Send the project info to the Android app
    out.println(projectInfo);
%>
