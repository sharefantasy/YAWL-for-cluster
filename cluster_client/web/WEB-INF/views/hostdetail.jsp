<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%--
  Created by IntelliJ IDEA.
  User: fantasy
  Date: 2016/2/6
  Time: 19:48
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Host: ${host.name}</title>
    <%@include file="header.jsp" %>
</head>
<body>

<ul>
    <li>${host.id}</li>
    <li>${host.name}</li>
    <li>${host.capacitySpeed}</li>
    <li>
        <ol>
            <c:forEach items="${host.engineList}" var="e">
                <li><a href="/page/engine/${e.role}">${e.role}</a></li>
            </c:forEach>
        </ol>
    </li>
</ul>
</body>
</html>
