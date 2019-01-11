<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
	<h1>Result Document (View)</h1><br>
	This data was passed by the Controller: 
	<font color=red>
	<% 
		out.println(request.getAttribute("outputDataToDisplay"));
	%>
	</font>
</body>
</html>