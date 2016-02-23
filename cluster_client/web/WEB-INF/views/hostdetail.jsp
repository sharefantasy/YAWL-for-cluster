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
<%@include file="nav.jsp" %>
<div class="container">
    <div class="row">
        <ul>
            <li>${host.id}</li>
            <li>${host.name}</li>
        </ul>
        <c:choose>
            <c:when test="${host.capabilitySet==null}">
                <p>未测试能力值</p>
            </c:when>
            <c:otherwise>
                <table class="table table-bordered">
                    <caption>能力表</caption>
                    <thead>
                    <tr>
                        <td>引擎数</td>
                        <td>能力</td>
                    </tr>
                    </thead>
                    <c:forEach items="${host.capabilitySet}" var="c">
                        <tr>
                            <td>${c.eNum}</td>
                            <td>${c.capability}</td>
                        </tr>
                    </c:forEach>
                </table>
            </c:otherwise>
        </c:choose>

        <table class="table table-bordered">
            <caption>引擎表</caption>
            <thead>
            <tr>
                <td>角色</td>
                <td>引擎</td>
            </tr>
            </thead>
            <c:forEach items="${host.engineList}" var="e">
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
