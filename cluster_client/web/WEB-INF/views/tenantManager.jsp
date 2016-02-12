<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%--
  Created by IntelliJ IDEA.
  User: fantasy
  Date: 2016/2/6
  Time: 22:50
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Tenant Manager</title>
    <%@include file="header.jsp" %>
</head>
<body>
<div class="container">
    <nav class="nav nav-tabs">
        <ul>
            <li><a href="<c:url value="/page/host/"/>" class="btn btn-info">物理机</a></li>
            <li><a href="<c:url value="/page/testplan"/>" class="btn btn-info">测试计划</a></li>
            <li><a href="<c:url value="/page/tenant"/>" class="btn btn-info">租户</a></li>
        </ul>
    </nav>

</div>
<div class="container">
    <form:form modelAttribute="newTenant" method="post">
        <form:input path="id"
    </form:form>
    <table class="table table-row-cell">
        <thead>
        <tr>
            <td>id</td>
            <td>物理机名</td>
            <td>速度</td>
            <td>删除</td>
        </tr>
        </thead>
        <c:forEach items="${tenants}" var="tenant">
            <tr>
                <td><a href="/page/host/${host.id}/">${host.id}</a></td>
                <td>${host.name}</td>
                <td>${host.currentSpeed}</td>
                <td><a href="/page/host/delete/${host.id}/">删除</a></td>
            </tr>
        </c:forEach>
    </table>
</div>

</body>
</html>
