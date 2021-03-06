<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%--
  Created by IntelliJ IDEA.
  User: fantasy
  Date: 2016/2/6
  Time: 18:34
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>PlanManager</title>
    <%@include file="header.jsp" %>
</head>
<body>
<%@include file="nav.jsp" %>
<div class="container">
    <div class="row">
        <form:form method="post" modelAttribute="testplan" cssClass="form-horizontal">
            <label for="host">被测物理机：</label>
            <form:select path="host">
                <form:options items="${hosts}"/>
            </form:select>
            <br>
            <label for="engineNumber">引擎数：</label><form:input path="engineNumber" type="text"/><br>
            <label for="endTime">结束时间：</label><form:input path="endTime" type="dates"/><br>
            <input type="submit" value="提交计划">
        </form:form>

        <table class="table table-bordered col-lg-6 ">
        <thead>
        <tr>
            <td>id</td>
            <td>物理机名</td>
            <td>引擎个数</td>
            <td>起始时间</td>
            <td>结束时间</td>
            <td>开始</td>
            <td>删除</td>
        </tr>
        </thead>
        <c:forEach items="${plans}" var="plan">
            <tr>
                <td>${plan.id}</td>
                <td>${plan.host}</td>
                <td>${plan.engineNumber}</td>
                <td>${plan.startTime}</td>
                <td>${plan.endTime}</td>
                <td><a href="${pageContext.request.contextPath}/page/testplan/start/${plan.id}/">开始计划</a></td>
                <td><a href="${pageContext.request.contextPath}/page/testplan/delete/${plan.id}/">删除计划</a></td>
            </tr>
        </c:forEach>
    </table>

    </div>
</div>
</body>
</html>
