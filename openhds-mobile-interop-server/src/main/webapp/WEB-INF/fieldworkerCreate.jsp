<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!doctype html>
<html>
	<head>
		<title>Create A user to download forms</title>
		<script src="jquery-1.7.1.min.js" type="text/javascript"></script>
	</head>
	<body>
		<form action="createFieldWorker.do" method="POST">
			<h3>Create a Form User</h3>
			<c:if test="${success}">
				<p style="color:green">${success}</p>
			</c:if>
			
			<c:if test="${errors != null}">
				<p style="color:red">
					Create user failed:
					<ul>
						<c:forEach items="${errors}" var="error">
							<li>${error}</li>
						</c:forEach>
					</ul>
				</p>
			</c:if>
			<p>A form user can download a set of form submissions</p>
			
			<p>User name:<br /><input type="text" size="50" name="username" value="${formUser == null ? '' : formUser.userName}" /></p>
			
			<p>Password:<br /><input type="password" size="50" name="password" value="${formUser == null ? '' : formUser.password}" /></p>
			<p>Role:</br><select name="role"><option value="admin">Admin</option><option value="fieldworker">Field Worker</option></select></p>
			<input type="submit" value="Create User" />
			
		</form>
	</body>
</html>