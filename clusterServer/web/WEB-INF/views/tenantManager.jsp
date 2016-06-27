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
<%@include file="nav.jsp" %>
<div class="container">
    <form:form modelAttribute="newTenant" method="post" cssClass="form-horizontal col-lg-6">
        <label>租户名： </label><form:input path="name"/><br>
        <label>SLO速度：</label><form:input path="SLOspeed"/><br>
        <label>SLO速度：</label><form:input path="roleNum"/><br>
        <input type="submit" value="确认"><br>
    </form:form>
    <table class="table table-row-cell">
        <thead>
        <tr>
            <td>id</td>
            <td>租户名</td>
            <td>SLO速度</td>
            <td>删除</td>
        </tr>
        </thead>
        <c:forEach items="${tenants}" var="tenant">
            <tr>
                <td><a href="${pageContext.request.contextPath}/page/tenant/${tenant.id}/">${tenant.id}</a></td>
                <td>${tenant.name}</td>
                <td>${tenant.SLOspeed}</td>
                <td><a href="${pageContext.request.contextPath}/page/tenant/delete/${tenant.id}/">删除</a></td>
            </tr>
        </c:forEach>
    </table>
</div>

</body>
</html>
