<?php

	if (isset($_POST['username']) && isset($_POST['email']) && isset($_POST['password'])) {
	
		$username = mysql_real_escape_string($_POST['username']);
		$email = mysql_real_escape_string($_POST['email']);
		$password = mysql_real_escape_string($_POST['password']);

		// create the user account
		$query = "INSERT INTO users (username, email, password, role_id, ban_start_date, ban_days) VALUES ('{$username}', '{$email}', '{$password}', 1, NOW(), 0)";
		
		mysql_query($query);
		if (mysql_error() == "") {
		
			// send an email
			$message = "Thanks for registering. Your username is <b>{$username}</b> and password is <b>{$password}</b>.";		
			sendEmail($email, "Welcome to ARIN!", $message);
		} else {
			$errorMessage = $GLOBALS['SAME_EMAIL'];
		}
	} else {
		$errorMessage = $GLOBALS['MISSING_INFO'];
	}

?>