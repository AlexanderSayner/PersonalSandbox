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
        <h1>About Page</h1>
        <p>${message}</p>
        <p>This application demonstrates a pure Spring configuration without Spring Boot.</p>
        <ul>
            <li>No Spring Boot dependencies</li>
            <li>Spring configuration via Java annotations</li>
            <li>Deployed using Docker</li>
        </ul>
        <a href="<c:url value='/' />">Back to Home</a>
    </div>
</body>
</html>