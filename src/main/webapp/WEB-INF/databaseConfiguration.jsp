<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!doctype html>
<html>
	<head>
		<title>Create A user to download forms</title>
		<script src="jquery-1.7.1.min.js" type="text/javascript"></script>
	</head>
	<body>
		<form method="POST">
			<h3>Database Configuration</h3>
			
			<p><a href="${pageContext.request.contextPath}/">Home</a></p>
			
			<p>Configure your the database</p>
			
			<c:if test="${errors != null}">
				<p style="color:red">
					Create database failed:
					<ul>
						<c:forEach items="${errors}" var="error">
							<li>${error}</li>
						</c:forEach>
					</ul>
				</p>
			</c:if>
			
			<p>Database:<br />
				<select>
					<option value="mysql">MySQL</option>
				</select>
			</p>
			
			<p>Database username:<br />
				<input type="text" size="50" name="databaseUsername" value="${databaseUsername}" />
			</p>
			
			<p>Database password:<br />
				<input type="password" size="50" name="databasePassword" value="${databasePassword}" />
			</p>
			
			<p>Database name:<br />
				<input type="text" size="50" name="databaseName" value="${databaseName}" />
			</p>
			<p>
			 <label><input type="checkbox" name="createDatabase" /> Create a fresh database</label>
			 <br />
			 <span style="color:red">NOTE: If this is checked, it will delete all existing data</span> 
			</p>
			<input type="submit" name="databaseCreate" value="Save Database Configuration" />
		</form>
	</body>
</html>
