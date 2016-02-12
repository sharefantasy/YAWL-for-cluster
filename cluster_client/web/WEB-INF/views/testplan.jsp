<%--
  Created by IntelliJ IDEA.
  User: fantasy
  Date: 2016/1/31
  Time: 16:28
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Create Test Plan</title>
    <%@include file="header.jsp" %>
</head>
<body>

<form:form method="post" modelAttribute="testplan" cssClass="form-horizontal">
    <form:select path="host">
        <form:options items="hostlists"/>
    </form:select>
    <form:input path="engineNumber" type="text"/>
    <form:input path="endTime" type="dates"/>
    <input type="submit" value="提交计划">

</form:form>
</body>
</html>
