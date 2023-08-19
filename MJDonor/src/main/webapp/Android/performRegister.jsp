<%@page import="com.db.ConnectDB"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="java.util.Date" %>

<%
   ConnectDB connectDB = ConnectDB.getInstance();

   // 한글 인코딩 부분
   request.setCharacterEncoding("utf-8");

   // Get registration parameters from request
   String name = request.getParameter("name");
   String description = request.getParameter("description");
   int target_point = Integer.parseInt(request.getParameter("target_point"));

  
   String start_date = request.getParameter("start_date");
   String end_date = request.getParameter("end_date");
   
   String image1 = request.getParameter("image1");
   String image2 = request.getParameter("image2");
   
   String category = request.getParameter("category");
   
   int ORGANIZATION_ID = Integer.parseInt(request.getParameter("ORGANIZATION_ID"));
   int REGISTRANT_ID = Integer.parseInt(request.getParameter("REGISTRANT_ID"));

   String resultMessage = connectDB.performRegister(name, description, target_point, start_date, end_date, image1, image2, category, ORGANIZATION_ID, REGISTRANT_ID);

   // 안드로이드로 전송
   out.println(resultMessage);
%>
