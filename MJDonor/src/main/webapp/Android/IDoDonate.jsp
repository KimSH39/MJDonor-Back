<%@page import="com.db.ConnectDB"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%
    ConnectDB connectDB = ConnectDB.getInstance();

    // 한글 인코딩 부분
    request.setCharacterEncoding("utf-8");

    // Retrieve project info based on p_id
    String pIdParam = request.getParameter("p_id");
    if (pIdParam != null) {
        int projectId = Integer.parseInt(pIdParam);
        String specificProjectInfo = connectDB.getSpecificProjectInfo(projectId);
        
        // Retrieve project and organization information
        String IDoDonateInfo = connectDB.getIDoDonateInfo(projectId);
        out.println(IDoDonateInfo);
    }
%>
