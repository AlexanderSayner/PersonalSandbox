<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>${title}</title>
    <link rel="stylesheet" type="text/css" href="<c:url value='/resources/css/style.css' />">
</head>
<body>
    <div class="container">
        <h1>Welcome to Pure Spring Configuration Example</h1>
        <p>${message}</p>
        <ul>
            <li><a href="<c:url value='/' />">Home</a></li>
            <li><a href="<c:url value='/about' />">About</a></li>
        </ul>
    </div>
</body>
</html>