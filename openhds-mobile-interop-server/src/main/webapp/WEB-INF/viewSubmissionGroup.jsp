<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!doctype html>
<html>
	<head>
		<title>Create A user to download forms</title>
		<script src="jquery-1.7.1.min.js" type="text/javascript"></script>
	</head>
	<body>
		<h3>View Form Submission</h3>
		<p><a href="${pageContext.request.contextPath}/admin/form/list">Back to listing</a><p>
		<p><strong>Form Id</strong>: ${group.id}</p>
		<p><strong>Form Type</strong>: ${group.submissionGroupType}</p>
		<p><strong>Status</strong>: ${group.completed ? 'Finished' : 'Ongoing'}</p>
		<div>
			<strong>Form Submissions</strong>:
			<ul>
				<c:forEach items="${group.submissions}" var="sub">
					<li>Form Submission ${sub.id} <a href="${group.id}/submission/${sub.id}">View</a> (${sub.active ? 'Active' : 'Inactive'})</li>
				</c:forEach>
			</ul>
		</div>
		<div>
			<strong>Actions</strong>:
			<ul>
				<c:forEach items="${group.formActions}" var="action">
					<li>${action.actionType} on ${action.formattedDate}&nbsp;(Form Submission ${action.submission.id})</li>
				</c:forEach>
			</ul>
		</div>
	</body>
</html>