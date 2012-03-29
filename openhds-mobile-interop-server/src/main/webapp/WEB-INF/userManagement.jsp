<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!doctype html>
<html>
	<head>
		<title>Create A user to download forms</title>
		<script src="jquery-1.7.1.min.js" type="text/javascript"></script>
	</head>
	<body>
		<h3>Manage Users</h3>
		<ul>
			<li><a href="create">Add a new user</a></li>
		</ul>
		
		<c:if test="${fn:length(userList) == 0}">
			<p>No users in the system</p>
		</c:if>
		
		<c:if test="${fn:length(userList) > 0}">
			<table border=1>
				<tr>
					<th>Username</th>
					<th>&nbsp;</th>
					<th>&nbsp;</th>
				</tr>
				<c:forEach items="${userList}" var="user">
					<tr>
						<td>${user.username}</td>
						<td><a href="editUser/${user.username}">Edit User</a></td>
						<td><a href="deleteUser/${user.username}">Delete User</a></td>
					</tr>
				</c:forEach>
			</table>
		</c:if>
	</body>
	
</html>