<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!doctype html>
<html>
	<head>
		<title>Create A user to download forms</title>
		<script src="jquery-1.7.1.min.js" type="text/javascript"></script>
	</head>
	<body>
		<h3>View Form Submission</h3>
		<p><a href="${pageContext.request.contextPath}/">Home</a></p>
		<p><a href="${pageContext.request.contextPath}/admin/form/group/${group}">Back to Group</a><p>
		<p><strong>Form Id</strong>: ${form.id}</p>
		<p><strong>Form Type</strong>: ${form.formType}</p>
		<p><strong>ODK Aggregate Id</strong>: ${form.odkUri}</p>
		<p><strong>Form Owner Id</strong>: ${form.formOwnerId}</p>
		<div>
			<strong>Form Errors</strong>:
			<ul>
				<c:forEach items="${form.formErrors}" var="error">
					<li>${error.error}</li>
				</c:forEach>
			</ul>
		</div>
		<p>
			<strong>Form Instance XML</strong>:
		</p>
					<pre>
${instanceXml}
			</pre>
		<p><strong></strong></p>
		<p><strong></strong></p>
	</body>
</html>