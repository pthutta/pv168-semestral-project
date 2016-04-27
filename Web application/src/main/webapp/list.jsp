<%@ page import="cz.muni.fi.pv168.web.ServiceServlet" %><%--
  Created by IntelliJ IDEA.
  User: Peter
  Date: 19.4.2016
  Time: 17:52
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>

<head>
    <title>List</title>
</head>

<body>

<h2>Cauldrons</h2>
<table border="1">
    <thead>
    <tr>
        <th>Capacity</th>
        <th>Water temperature</th>
        <th>Hell floor</th>
    </tr>
    </thead>
    <c:forEach items="${cauldrons}" var="cauldron">
        <tr>
            <c:if test="${cauldronId != cauldron.id}">
                <td><c:out value="${cauldron.capacity}"/></td>
                <td><c:out value="${cauldron.waterTemperature}"/></td>
                <td><c:out value="${cauldron.hellFloor}"/></td>
                <td>
                    <form method="post" action="${pageContext.request.contextPath}<%=ServiceServlet.MAPPING%><%=ServiceServlet.CAULDRON_MAPPING%>/delete?id=${cauldron.id}"
                          style="margin-bottom: 0;"><input type="submit" value="Delete"></form>
                </td>
                <td>
                    <form method="post" action="${pageContext.request.contextPath}<%=ServiceServlet.MAPPING%><%=ServiceServlet.CAULDRON_MAPPING%>/preupdate?id=${cauldron.id}"
                          style="margin-bottom: 0;"><input type="submit" value="Update"></form>
                </td>
            </c:if>
            <c:if test="${cauldronId == cauldron.id}">
                <form action="${pageContext.request.contextPath}<%=ServiceServlet.MAPPING%><%=ServiceServlet.CAULDRON_MAPPING%>/update?id=${cauldron.id}" method="post">
                    <td><input type="text" name="capacity" placeholder="${cauldron.capacity}" value="<c:out value='${param.capacity}'/>"/></td>
                    <td><input type="text" name="waterTemperature" placeholder="${cauldron.waterTemperature}" value="<c:out value='${param.waterTemperature}'/>"/></td>
                    <td><input type="text" name="hellFloor" placeholder="${cauldron.hellFloor}" value="<c:out value='${param.hellFloor}'/>"/></td>
                    <td>
                        <input type="Submit" value="Confirm" />
                    </td>
                </form>
            </c:if>
        </tr>
    </c:forEach>
</table>

<h3>Enter new cauldron</h3>
<c:if test="${not empty cauldronError}">
    <div style="border: solid 1px red; background-color: yellow; padding: 10px">
        <c:out value="${cauldronError}"/>
    </div>
</c:if>
<form action="${pageContext.request.contextPath}<%=ServiceServlet.MAPPING%><%=ServiceServlet.CAULDRON_MAPPING%>/add" method="post">
    <table>
        <tr>
            <th>Capacity:</th>
            <td><input type="text" name="capacity" value="<c:out value='${param.capacity}'/>"/></td>
        </tr>
        <tr>
            <th>Water temperature:</th>
            <td><input type="text" name="waterTemperature" value="<c:out value='${param.waterTemperature}'/>"/></td>
        </tr>
        <tr>
            <th>Hell floor:</th>
            <td><input type="text" name="hellFloor" value="<c:out value='${param.hellFloor}'/>"/></td>
        </tr>
    </table>
    <input type="Submit" value="Confirm" />
</form>

<h2>Sinners</h2>
<table border="1">
    <thead>
    <tr>
        <th>First name</th>
        <th>Last name</th>
        <th>Sin</th>
        <th>Release date</th>
        <th>Signed contract</th>
    </tr>
    </thead>
    <c:forEach items="${sinners}" var="sinner">
        <tr>
            <c:if test="${sinnerId != sinner.id}">
                <td><c:out value="${sinner.firstName}"/></td>
                <td><c:out value="${sinner.lastName}"/></td>
                <td><c:out value="${sinner.sin}"/></td>
                <td><c:out value="${sinner.releaseDate}"/></td>
                <td><c:out value="${sinner.signedContractWithDevil}"/></td>
                <td>
                    <form method="post" action="${pageContext.request.contextPath}<%=ServiceServlet.MAPPING%><%=ServiceServlet.SINNER_MAPPING%>/delete?id=${sinner.id}"
                          style="margin-bottom: 0;"><input type="submit" value="Delete"></form>
                </td>
                <td>
                    <form method="post" action="${pageContext.request.contextPath}<%=ServiceServlet.MAPPING%><%=ServiceServlet.SINNER_MAPPING%>/preupdate?id=${sinner.id}"
                          style="margin-bottom: 0;"><input type="submit" value="Update"></form>
                </td>
            </c:if>
            <c:if test="${sinnerId == sinner.id}">
                <form action="${pageContext.request.contextPath}<%=ServiceServlet.MAPPING%><%=ServiceServlet.SINNER_MAPPING%>/update?id=${sinner.id}" method="post">
                    <td><input type="text" name="firstName" placeholder="${sinner.firstName}" value="<c:out value='${param.firstName}'/>"/></td>
                    <td><input type="text" name="lastName" placeholder="${sinner.lastName}" value="<c:out value='${param.lastName}'/>"/></td>
                    <td><input type="text" name="sin" placeholder="${sinner.sin}" value="<c:out value='${param.sin}'/>"/></td>
                    <td><input type="text" name="releaseDate" placeholder="${sinner.releaseDate}" value="<c:out value='${param.releaseDate}'/>"/></td>
                    <td><input type="checkbox" name="signedContractWithDevil" value="<c:out value='${param.signedContractWithDevil}'/>"/></td>
                    <td>
                        <input type="Submit" value="Confirm" />
                    </td>
                </form>
            </c:if>
        </tr>
    </c:forEach>
</table>

<h3>Enter new sinner</h3>
<c:if test="${not empty sinnerError}">
    <div style="border: solid 1px red; background-color: yellow; padding: 10px">
        <c:out value="${sinnerError}"/>
    </div>
</c:if>
<form action="${pageContext.request.contextPath}<%=ServiceServlet.MAPPING%><%=ServiceServlet.SINNER_MAPPING%>/add" method="post">
    <table>
        <tr>
            <th>First name:</th>
            <td><input type="text" name="firstName" value="<c:out value='${param.firstName}'/>"/></td>
        </tr>
        <tr>
            <th>Last name:</th>
            <td><input type="text" name="lastName" value="<c:out value='${param.lastName}'/>"/></td>
        </tr>
        <tr>
            <th>Sin:</th>
            <td><input type="text" name="sin" value="<c:out value='${param.sin}'/>"/></td>
        </tr>
        <tr>
            <th>Release date (yyyy-MM-dd):</th>
            <td><input type="text" name="releaseDate" value="<c:out value='${param.releaseDate}'/>"/></td>
        </tr>
        <tr>
            <th>Signed contract with devil:</th>
            <td><input type="checkbox" name="signedContractWithDevil" value="<c:out value='${param.signedContractWithDevil}'/>"/></td>
        </tr>
    </table>
    <input type="Submit" value="Confirm" />
</form>


</body>
</html>
