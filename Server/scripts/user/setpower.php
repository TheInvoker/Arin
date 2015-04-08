<?php

	if (isset($_POST['email']) && isset($_POST['rid'])) {
	
		$rid = $_POST['rid'];
		$email = mysql_real_escape_string($_POST['email']);
		
		$query = "UPDATE users SET role_id={$rid} WHERE email='{$email}'";
		mysql_query($query);	
		if (mysql_error() == "") {
		
			// get the email of the user
			$query = "SELECT email
					  FROM users 
					  WHERE email='{$email}'";
			$recordset = mysql_query($query);
			if (mysql_error() == "") {
				
				if (mysql_num_rows($recordset) > 0) {
					
					// send email to the user
					$email = mysql_result($recordset, 0, 'email');
					if ($rid==1) {
						$message = "You are now a basic user.";	
					} else if ($rid==2) {
						$message = "You have been given additional power for more features. Use it wisely.";	
					} else {
						$message = "You are an unknown role.";	
					}
					sendEmail($email, "Your role has been updated!", $message);
				}
			
			} else {
				$errorMessage = $GLOBALS['UNEXPECTED_DB_ERROR'];
			}
		} else {
			$errorMessage = $GLOBALS['UNEXPECTED_DB_ERROR'];
		}
	} else {
		$errorMessage = $GLOBALS['MISSING_INFO'];
	}
	
?>