<?php

	if (isset($_POST['id']) && isset($_POST['username']) && isset($_POST['email']) && isset($_POST['password'])) {
	
		$id = $_POST['id'];
		$username = mysql_real_escape_string($_POST['username']);
		$email = mysql_real_escape_string($_POST['email']);
		$password = mysql_real_escape_string($_POST['password']);
	
		// edit the profile
		$query = "UPDATE users SET username='{$username}',email='{$email}',password='{$password}' WHERE id={$id}";
		
		mysql_query($query);
		if (mysql_error() == "") {
			
		} else {
			$errorMessage = $GLOBALS['UNEXPECTED_DB_ERROR'];
		}
	} else {
		$errorMessage = $GLOBALS['MISSING_INFO'];
	}

?>