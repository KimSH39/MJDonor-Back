<%@page import="com.db.ConnectDB"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%
   ConnectDB connectDB = ConnectDB.getInstance();

//한글 인코딩 부분
	request.setCharacterEncoding("utf-8");

   String refundInfo = connectDB.getRefundInfo(); 

   System.out.println(refundInfo); // 환불해야할 입금 목록

%>