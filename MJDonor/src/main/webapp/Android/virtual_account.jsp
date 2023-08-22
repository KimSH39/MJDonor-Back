<%@page import="com.db.ConnectDB"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="org.json.simple.JSONObject" %>
<%@ page import="java.io.IOException" %>
<%@ page import="java.util.Calendar" %>

<%
	boolean isSuccess = true; // 변수를 미리 선언하고 초기화
    JSONObject jsonObject = null;
    String orderName = " ";
    String method = " ";
    String accountNumber = " ";
    String bank = " ";
    String dueDate = " ";
    String dd = " ";

    try {
        ConnectDB connectDB = ConnectDB.getInstance();

        // 한글 인코딩 부분
        request.setCharacterEncoding("utf-8");
        int u_id = Integer.parseInt(request.getParameter("u_id"));
        String email = connectDB.getEmail(u_id); // u_id 값을 전달
        int point = Integer.parseInt(request.getParameter("point"));
        String nick = request.getParameter("nick");
        String msg = request.getParameter("msg");
        int p_id = Integer.parseInt(request.getParameter("p_id"));
        String project = connectDB.getProjectName(p_id);
        String due = request.getParameter("dueDate");
        String rbank = request.getParameter("rbank");
        String r_a = request.getParameter("refund_account");

        //jsonObject = connectDB.createVirtualAccount(point, email, nick, project, due, rbank, r_a);
        jsonObject = connectDB.createVirtualAccount(point, "email@1234", nick, project, due, rbank, r_a);
		
        out.println(point);
        out.println(nick);
        out.println(rbank);
        out.println(r_a);
        
		//VirtualAccountHelper helper = new VirtualAccountHelper();
        //jsonObject = helper.createVirtualAccount();
        orderName = (String) jsonObject.get("orderName");
        method = (String) jsonObject.get("method");
        accountNumber = (String) ((JSONObject) jsonObject.get("virtualAccount")).get("accountNumber");
        bank = (String) ((JSONObject) jsonObject.get("virtualAccount")).get("bank");
        String v_a = bank +'/' + accountNumber;
        
        
        dueDate = (String) ((JSONObject) jsonObject.get("virtualAccount")).get("dueDate");
        
        
        if (jsonObject != null){
        //	String Donation = connectDB.performDonation(u_id, p_id, nick, point, rbank, r_a, msg, v_a, due);
        //	out.println(Donation);
        //	out.println(jsonObject.toJSONString());
        }

    } catch (Exception e) {
        // Handle exception
    }
%>

<!DOCTYPE html>
<html lang="ko">
<head>
    <title>결제 성공</title>
    <meta http-equiv="x-ua-compatible" content="ie=edge"/>
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no"/>
</head>
<body>
<section>
    <%
    if (isSuccess) { %>
        <h1>결제 성공</h1>
        <p>결과 데이터 : <%= jsonObject.toJSONString() %></p>
        <p>orderName : <%= orderName %></p>
        <p>method : <%= method %></p>
        <p>virtualAccount -> accountNumber : <%= accountNumber %></p>
        <p>virtualAccount -> bank : <%= bank %></p>
        <p>virtualAccount -> dueDate : <%= dueDate %></p>
        
        
        
        
    <% } else { %>
        <h1>결제 실패</h1>
        <p>에러가 발생했습니다.</p>
    <% } %>
</section>
</body>
</html>
