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
<title>New Feed</title>
</head>
<body>
<form action="/feedcat/new" method="get">
<label for="url">Feed URL:</label>
<input name="url" type="text"/>
<input name="submit" type="submit" value="Submit"/>
</form>
</body>
</html>
