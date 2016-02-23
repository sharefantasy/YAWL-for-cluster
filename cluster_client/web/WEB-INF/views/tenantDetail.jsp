<%--
  Created by IntelliJ IDEA.
  User: fantasy
  Date: 2016/2/21
  Time: 20:20
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Tenant: ${tenant.name}</title>
    <%@include file="header.jsp" %>
</head>
<body>
<%@include file="nav.jsp" %>
<div class="container">
    <div class="row">
        <ul>
            <li>${tenant.id}</li>
            <li>${tenant.name}</li>
            <li>${tenant.createTime}</li>
            <li>${tenant.SLOspeed}</li>
            <li>${tenant.currentSpeed}</li>
        </ul>
        <table class="table table-bordered">
            <thead>
            <tr>
                <td>角色</td>
                <td>引擎</td>
            </tr>
            </thead>
            <c:forEach items="${tenant.engineList}" var="e">
                <tr>
                    <td><a href="${pageContext.request.contextPath}/page/engineRole/${e.id}">${e}</a></td>
                    <td>
                        <a href="${pageContext.request.contextPath}/page/engine/${e.engine.id}/">${e.engine.engineID}</a>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </div>
</div>

</body>
</html>
