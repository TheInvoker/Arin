<?php

	if (isset($_POST['email']) && isset($_POST['password'])) {
	
		$email = mysql_real_escape_string($_POST['email']);
		$password = mysql_real_escape_string($_POST['password']);
		
		// find the person
		$query = "SELECT * FROM users WHERE email='{$email}' AND password='{$password}'";
		$recordset = mysql_query($query);	
		if (mysql_error() == "") {
		
			// if it exists
			if (mysql_num_rows($recordset) > 0) {
			
				$user_id = mysql_result($recordset, 0, 'id');
				$username = mysql_result($recordset, 0, 'username');
				$ban_start_date = mysql_result($recordset, 0, 'ban_start_date');
				$ban_days = mysql_result($recordset, 0, 'ban_days');
				$role_id = mysql_result($recordset, 0, 'role_id');

				$successMessage = array(
					'id' => $user_id,
					'username' => $username,
					'ban_start_date' => $ban_start_date,
					'ban_days' => $ban_days,
					'role_id' => $role_id
				);

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