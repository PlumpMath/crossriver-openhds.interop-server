<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

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
			
			
			<p>User name:<br />
				<input type="text" disabled="disabled" size="50" name="username" value="${user == null ? '' : user.username}" />
			</p>
			
			<p>Password:<br />
				<input type="password" size="50" name="password" />
			</p>
			<p>Confirm Password:<br />
				<input type="password" size="50" name="confirmPassword" />
			</p>
			
			<div><strong>Supervised Field Workers</strong>:<br />
			  Enter 1 Field Worker Id per line. Blank fields will be ignored
			  <c:forEach items="${user.managedFieldworkers}" var="fw">
			  	<div><input name="supervisedFieldworker" type="text" value="${fw}" /></div>
			  </c:forEach>
			  <c:forEach begin="1" end="${6 - fn:length(user.managedFieldworkers)}">
			  	<div><input name="supervisedFieldworker" type="text" /></div>
			  </c:forEach>			  
			</div>			
			
			<input type="submit" name="saveUser" value="Save" />
		</form>
	</body>
</html>
