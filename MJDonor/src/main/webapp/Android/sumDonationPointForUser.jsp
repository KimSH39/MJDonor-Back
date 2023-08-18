<%@page import="com.db.ConnectDB"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%
    ConnectDB connectDB = ConnectDB.getInstance();

    // 한글 인코딩 부분
    request.setCharacterEncoding("utf-8");
    String u_id = request.getParameter("u_id");
    
    // Retrieve sum of donation points for the user
    int sumDonationPoint = connectDB.getSumDonationPointForUser(u_id);
    out.println("Sum of Donation Points for User " + u_id + ": " + sumDonationPoint);
%>
