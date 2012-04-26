<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!doctype html>
<html>
	<head>
		<title>Create A user to download forms</title>
		<script src="jquery-1.7.1.min.js" type="text/javascript"></script>
	</head>
	<body>
		<form method="POST">
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
			
			<p><strong>Username</strong>:<br /><input type="text" size="50" name="username" value="${formUser == null ? '' : formUser.username}" /></p>
			
			<p><strong>Password</strong>:<br />
				<input type="password" size="50" name="password" />
			</p>
			<p><strong>Confirm Password</strong>:<br />
				<input type="password" size="50" name="confirmPassword" />
			</p>
			<div><strong>Supervised Field Workers</strong>:<br />
			  Enter 1 Field Worker Id per line. Blank fields will be ignored
			  <div><input name="supervisedFieldworker" type="text" /></div>
			  <div><input name="supervisedFieldworker" type="text" /></div>
			  <div><input name="supervisedFieldworker" type="text" /></div>
			  <div><input name="supervisedFieldworker" type="text" /></div>
			  <div><input name="supervisedFieldworker" type="text" /></div>
			  <div><input name="supervisedFieldworker" type="text" /></div>
			</div>
			<input type="submit" value="Create User" />
		</form>
	</body>
</html>
