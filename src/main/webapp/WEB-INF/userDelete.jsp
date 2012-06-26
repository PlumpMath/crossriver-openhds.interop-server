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
			<h3>Delete User</h3>
			<p style="color:red">
				Are you sure you want to delete the user ${username}?
			</p>
			<input type="submit" name="delete" value="Delete User" />
			<input type="submit" name="cancel" value="Cancel" />
		</form>
	</body>
</html>
