<%@page import="com.db.ConnectDB"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%
   ConnectDB connectDB = ConnectDB.getInstance();

//한글 인코딩 부분
	request.setCharacterEncoding("utf-8");
   String id = request.getParameter("id");
   String pw = request.getParameter("pw");
	
   String returns = connectDB.connectionDB(id, pw);

   System.out.println(returns);

   // 안드로이드로 전송
   out.println(returns);
%>