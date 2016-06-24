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
    <form:form modelAttribute="newTenant" method="post" cssClass="form-horizontal col-lg-6">
        <label>租户名： </label><form:input path="name"/><br>
        <label>worklist：</label><form:input path="defaultWorklist"/><br>
        <label>引擎：</label>
        <form:select path="engineSet">
            <form:options items="${engines}"/>
        </form:select><br>
        <input type="submit" value="确认"><br>
    </form:form>
    <table class="table table-row-cell">
        <thead>
        <tr>
            <td>id</td>
            <td>租户名</td>
            <td>worklist</td>
            <td>删除</td>
        </tr>
        </thead>
        <c:forEach items="${tenants}" var="tenant">
            <tr>
                <td><a href="${pageContext.request.contextPath}/page/tenant/${tenant.id}/">${tenant.id}</a></td>
                <td>${tenant.name}</td>
                <td>${tenant.defaultWorklist}</td>
                <td><a href="${pageContext.request.contextPath}/tenant/delete/${tenant.id}/">删除</a></td>
            </tr>
        </c:forEach>
    </table>
</div>

</body>
</html>
