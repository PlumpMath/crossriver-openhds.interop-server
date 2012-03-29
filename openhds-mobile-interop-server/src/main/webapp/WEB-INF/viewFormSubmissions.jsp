<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!doctype html>
<html>
	<head>
		<title>Create A user to download forms</title>
		<script src="jquery-1.7.1.min.js" type="text/javascript"></script>
	</head>
	<body>
		<h3>Form Submissions</h3>
		<p><a href="${pageContext.request.contextPath}/index.jsp">Home</a></p>
		<table border=1>
			<tr>
				<th>Form Id</th>
				<th>Form Type</th>
				<th>Form Owner Id</th>
				<th>Form Status</th>
				<th>Date Downloaded</th>
			</tr>
			<c:forEach items="${submissions}" var="sub">
				<tr>
					<td>${sub.id}</td>
					<td>${sub.formType}</td>
					<td>${sub.formOwnerId}</td>
					<td>${sub.submissionStatus}</td>
					<td></td>
				</tr>
			</c:forEach>
		</table>
	</body>
</html>