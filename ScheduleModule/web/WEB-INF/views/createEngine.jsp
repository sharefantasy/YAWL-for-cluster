<%@ page contentType="text/html;charset=GBK" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%--
  Created by IntelliJ IDEA.
  User: fantasy
  Date: 2016/5/30
  Time: 23:45
  To change this template use File | Settings | File Templates.
--%>

<html>
<head>
    <title>Engine:${engine.id}</title>
    <%@include file="header.jsp" %>
</head>
<body>
<form:form modelAttribute="engine" method="post">
    <label for="address">�����ַ��</label><form:input path="address"/><br>
    <label for="port">����˿�</label><form:input path="port"/><br>
    <%--<label for="tenant">ѡ���⻧��</label>--%>
    <%--<form:select path="tenant">--%>
    <%--<form:option value="null"/>--%>
    <%--<form:options items="${tenants}"/>--%>
    <%--</form:select><br>--%>
    <input type="submit" value="ȷ��">
</form:form>
</body>
</html>
