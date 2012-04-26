<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!doctype html>
<html>
	<head>
		<title></title>
		<script src="jquery-1.7.1.min.js" type="text/javascript"></script>
	</head>
	<body>
		<h3>Form Submissions</h3>
		<p><a href="${pageContext.request.contextPath}/">Home</a></p>
		<table border=1 cellpadding=5>
			<tr>
				<th>Form Id</th>
				<th>Form Type</th>
				<th>Status</th>
				<th># of Submission's</th>
				<th>View</th>
			</tr>
			<c:forEach items="${submissions}" var="sub">
				<tr>
					<td>${sub.id}</td>
					<td>${sub.submissionGroupType}</td>
					<td>${sub.completed ? 'Finished' : 'Ongoing'}</td>
					<td>${fn:length(sub.submissions)}</td>
					<td><a href="group/${sub.id}">View Form Submission</a></td>
				</tr>
			</c:forEach>
		</table>
	</body>
</html>