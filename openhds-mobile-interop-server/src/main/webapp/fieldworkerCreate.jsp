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
			
			<p>A form user can download a set of form submissions</p>
			
			<p>User name:<br /><input type="text" size="50" name="userName" /></p>
			
			<p>Password:<br /><input type="password" size="50" name="password" /></p>
			
			<div id="managedFwEle">
				Managed Field Workers:<br />
				<button id="addBtn">Add</button>
				<div>
					<input type="text" name="managedIds" /><span><a href="#" class="remove">Remove</a></span>
				</div>
			</div>
			
			<input type="submit" value="Create User" />
		</form>
		<script type="text/javascript">
			var newFieldHtml = '<div><input type="text" name="managedIds" /><span><a href="#" class="remove">Remove</a></span></div>';
			
			$("#addBtn").click(function(event) {
				$("#managedFwEle").append(newFieldHtml);
				event.preventDefault();
			});
			
			$(".remove").click(function(event) {
				event.preventDefault();
				$(".remove").unbind("click");
				var parent = $(this).parent().parent().parent();
				if (parent.children().length > 4) { // 4 guarantees user must provide at least 1 field worker
					$(this).parent().parent().remove();
				}
			});
		</script>
	</body>
</html>