<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%--
  Created by IntelliJ IDEA.
  User: fantasy
  Date: 2016/2/6
  Time: 19:33
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Host Manager</title>
    <%@include file="header.jsp" %>
</head>
<body>
<%@include file="nav.jsp" %>
<div class="container">
    <div class="row">
        <form:form method="post" modelAttribute="newHost" cssClass="form-horizontal">
            <label for="name">物理机名称: </label><form:input path="name"/><br>
            <label for="ip">ip: </label><form:input path="ip"/><br>
            <input type="submit" value="提交">
        </form:form>
        <a href="${pageContext.request.contextPath}/page/host/reload/">从系统加载host</a>
    </div>

    <table class="table table-bordered table-cell">
        <thead>
        <tr>
            <td>id</td>
            <td>物理机名</td>
            <td>ip</td>
            <td>当前总速度</td>
            <td>删除</td>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${hosts}" var="host">
            <tr class="table-row-cell">
                <td><a href="${pageContext.request.contextPath}/page/host/${host.id}/">${host.id}</a></td>
                <td>${host.name}</td>
                <td>${host.ip}</td>
                <td>${host.currentSpeed}</td>
                <td><a href="${pageContext.request.contextPath}/page/host/delete/${host.id}/">删除</a></td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>
</body>
</html>
