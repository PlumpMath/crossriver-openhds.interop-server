<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!doctype html>

<html>
<head>

</head>
<body>
	<h3>Mobile Administration</h3>
  <c:if test="${needDatabaseConfiguration}">
    <div style="background-color:#FFFFCC">
      <strong>You are currently using the in-memory database. This means you could lose data. Please use 'Database Configuration' to configure database properties.
    </div>
  </c:if>	
	<ul>
		<li><a href="admin/users/">Manage Server Users</a></li>
		<li><a href="admin/form/list">View Form Submissions</a></li>
		<li><a href="admin/database-configuration">Database Configuration</a></li>
	</ul>
</body>
</html>
