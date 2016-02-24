<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<style type="text/css">
</style>

<h2>U are going to import bill data</h2>
BatchID: <c:out value="${batch.batchId}" /> <br/>
账单类型: <c:out value="${batch.billName}" /><br/>
数据源类型：<c:out value="${batch.dataLoader.displayName}" /><br/>
<c:choose>
    <c:when test="${batch.dataLoader.displayName == LocalFile}">
        <form action="" method="post" accept="" enctype="">
            Select File
            <input type="file" />
            <input type="submit" />
        </form>
    </c:when>
    <c:otherwise>
    </c:otherwise>
</c:choose>

R U sure ?
<a href="<c:out value='mission.add.jsplayout.do?batchid=${batch.batchId}&billcn=${batch.billCN}' />" > YES !</a>
<DIV id="SOR_EXCEPTION" style="color:red"><c:out value="${REASON}" /></DIV>