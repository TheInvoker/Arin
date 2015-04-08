<?php

	if (isset($_POST['email'])) {
	
		$email = mysql_real_escape_string($_POST['email']);
	
		// get the password from the person
		$query = "SELECT password
				  FROM users 
				  WHERE email = '{$email}'";
		$recordset = mysql_query($query);
		if (mysql_error() == "") {
			
			if (mysql_num_rows($recordset) == 1) {
			
				$password =  mysql_result($recordset, 0, 'password');

				// email the person with the password
				$message = "Your password is <b>{$password}</b>.";		
				sendEmail($email, "Password Reminder", $message);

			} else {
				$errorMessage = $GLOBALS['ACCOUNT_NOT_EXIST'];
			}
		} else {
			$errorMessage = $GLOBALS['UNEXPECTED_DB_ERROR'];
		}
	} else {
		$errorMessage = $GLOBALS['MISSING_INFO'];
	}

?>