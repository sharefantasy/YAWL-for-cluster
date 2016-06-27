<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="container">
    <div class="row">
        <div class="col-md-6 col-md-offset-3 ">
            <ul class="nav nav-tabs">
                <li><a href="<c:url value="/page/host/"/>" class="btn btn-info ">物理机</a></li>
                <li><a href="<c:url value="/page/testplan/"/>" class="btn btn-info">测试计划</a></li>
                <li><a href="<c:url value="/page/tenant/"/>" class="btn btn-info">租户</a></li>
                <li><a href="<c:url value="/page/engine/"/>" class="btn btn-info">引擎</a></li>
            </ul>
        </div>
    </div>
</div>
<br/>
<br/>
<br/>