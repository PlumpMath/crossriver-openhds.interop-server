<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!doctype html>
<html>
	<head>
		<title>Create A user to download forms</title>
		<script src="jquery-1.7.1.min.js" type="text/javascript"></script>
	</head>
	<body>
		<form method="POST">
			<h3>Create Admin user</h3>
			<p>
				You must create an admin user
			</p>	
			<c:if test="${success}">
				<p style="color:green">${success}</p>
			</c:if>
			
			<c:if test="${errors != null}">
				<p style="color:red">
					Create admin failed:
					<ul>
						<c:forEach items="${errors}" var="error">
							<li>${error}</li>
						</c:forEach>
					</ul>
				</p>
			</c:if>
			<p>Password:<br />
				<input type="password" size="50" name="password" />
			</p>
			
			<p>Confirm Password:<br />
				<input type="password" size="50" name="confirmPassword" />
			</p>
		
			<input type="submit" value="Create Admin User" />
		</form>
	</body>
</html>
