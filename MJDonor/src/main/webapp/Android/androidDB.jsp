<%@page import="com.db.ConnectDB"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%
    ConnectDB connectDB = ConnectDB.getInstance();

    // 한글 인코딩 부분
    request.setCharacterEncoding("utf-8");

    // Check if a specific u_id exists
    String u_id = request.getParameter("u_id");
    if (u_id != null) {
        boolean uIdExists = connectDB.checkUserIdExists(u_id);
        out.println("u_id_exists: " + uIdExists);
    }
%>
