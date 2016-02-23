<%--
  Created by IntelliJ IDEA.
  User: fantasy
  Date: 2016/2/6
  Time: 19:48
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
<head>
    <title>Host: ${engine.engineID}</title>
    <%@include file="header.jsp" %>
</head>
<body>
<%@include file="nav.jsp" %>
<div class="container">
    <div class="row">
        <form:form modelAttribute="engine" method="post" cssClass="form-horizontal col-lg-6">
            <label for="id">id：</label><form:input path="id"/><br>
            <label for="engineID">引擎id：</label><form:input path="engineID"/><br>
            <label for="address">引擎地址：</label><form:input path="address"/><br>
            <label for="ip">引擎ip：</label><form:input path="ip"/><br>
            <label for="engineRole">选择角色：</label>
            <form:select path="engineRole">
                <form:option value="null"/>
                <form:options items="${availableRoles}"/>
            </form:select><br>
            <input type="submit" value="确认">
        </form:form>
    </div>
</div>
</body>
</html>