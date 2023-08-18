<%@page import="com.db.ConnectDB"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%
   ConnectDB connectDB = ConnectDB.getInstance();

//한글 인코딩 부분
	request.setCharacterEncoding("utf-8");
	int u_id = Integer.parseInt(request.getParameter("u_id"));
   String email = request.getParameter("email");
   String name = request.getParameter("name");
   String password = request.getParameter("password");
   String wallet = request.getParameter("wallet");
	
   String returns = connectDB.performSignup(u_id, email, name, password, wallet);

   System.out.println(returns);

   // 안드로이드로 전송
   out.println(returns);
%>