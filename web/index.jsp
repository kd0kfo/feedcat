<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" 
  contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
String username = request.getRemoteUser();
%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" 
  content="text/html; charset=UTF-8" />
<title>Feed list</title>
</head>
<body>
<p>Hello, <%= username %>. Welcome to the feed <a href="list">list</a>.</p>
<p><a href="logout.jsp">Logout</a></p>
</body>
</html>
