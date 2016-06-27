<%--
  Created by IntelliJ IDEA.
  User: fantasy
  Date: 2016/2/19
  Time: 15:05
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
<head>
    <title>Engine Manager</title>
    <%@ include file="header.jsp" %>
</head>
<body>
<%@ include file="nav.jsp" %>
<div class="container">
    <form:form modelAttribute="newEngine" method="post" cssClass="form-horizontal">
        <label for="engineID">引擎ID: </label><form:input path="engineID"/><br>
        <label for="address">引擎连接地址：</label><form:input path="address"/><br>
        <label for="ip">引擎ip地址：</label><form:input path="ip"/><br>

        <input type="submit" value="接入引擎">
    </form:form>
    <table class="table table-row-cell">
        <thead>
        <tr>
            <td>id</td>
            <td>引擎ID</td>
            <td>密码</td>
            <td>地址</td>
            <td>角色</td>
            <td>上次心跳时间</td>
            <td>状态</td>
            <td>删除</td>
        </tr>
        </thead>
        <c:forEach items="${engines}" var="engine">
            <tr>
                <td><a href="${pageContext.request.contextPath}/page/engine/${engine.id}/">${engine.id}</a></td>
                <td>${engine.engineID}</td>
                <td>${engine.password}</td>
                <td>${engine.address}</td>
                <td>${engine.engineRole}</td>
                <td>${engine.lastHeartbeatTime}</td>
                <td>${engine.status}</td>
                <td><a href="${pageContext.request.contextPath}/page/engine/delete/${engine.id}/">删除</a></td>
            </tr>
        </c:forEach>
    </table>
</div>
</body>
</html>
