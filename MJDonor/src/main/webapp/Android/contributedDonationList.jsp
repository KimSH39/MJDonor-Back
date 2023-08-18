<%@page import="com.db.ConnectDB"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%
    ConnectDB connectDB = ConnectDB.getInstance();

    // 한글 인코딩 부분
    request.setCharacterEncoding("utf-8");
    String u_id = request.getParameter("u_id");
    
    // Retrieve the donation list that you've contributed to
    String contributedDonationList = connectDB.getContributedDonationList(u_id);
    out.println(contributedDonationList);
%>
