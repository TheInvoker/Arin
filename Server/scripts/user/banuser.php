<?php

	if (isset($_POST['ban_user_id']) && isset($_POST['days']) && isset($_POST['ban_reason'])) {
	
		$ban_user_id = $_POST['ban_user_id'];
		$days = $_POST['days'];
		$ban_reason = mysql_real_escape_string($_POST['ban_reason']);
	
		// set the ban start day to be now
		$query = "UPDATE users SET ban_start_date=NOW(),ban_days={$days},ban_reason='{$ban_reason}' WHERE id={$ban_user_id}";
		mysql_query($query);
		if (mysql_error() == "") {
		
			// get the email from the banned user
			$query = "SELECT email FROM users WHERE id={$ban_user_id}";
			$recordset = mysql_query($query);	
			if (mysql_error() == "") {
			
				if (mysql_num_rows($recordset) > 0) {
				
					// email the banned user
					$email =  mysql_result($recordset, 0, 'email');
					$message = "You have been banned for {$days} days because <b>{$ban_reason}</b>.";		
					sendEmail($email, "You are banned", $message);
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