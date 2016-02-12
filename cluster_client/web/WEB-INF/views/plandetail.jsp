<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: fantasy
  Date: 2016/2/6
  Time: 14:13
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Test Plan: ${entity.id}</title>
</head>
<body>
<ul>
    <li>${entity.id}</li>
    <li>${entity.host}</li>
    <li>${entity.engineNumber}</li>
    <li>${entity.startTime}</li>
    <li>${entity.endTime}</li>
    <li>
        <ol>
            <c:forEach items="${entity.testTenant.engineList}" var="e">
                <li><a href="/page/engine/${e.role}">${e.role}</a>&nbsp;&nbsp;&nbsp;&nbsp;${e.currentSpeed}</li>
            </c:forEach>
        </ol>
    </li>
</ul>

</body>
</html>
