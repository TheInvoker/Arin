<?php

	if (isset($_POST['id']) && isset($_POST['is_node']) && isset($_POST['name']) && isset($_POST['resource']) && isset($_POST['history_id']) && isset($_POST['user_id']) && isset($_POST['version'])) {
	
		$id = $_POST['id'];
		$is_node = $_POST['is_node'];
		$name = mysql_real_escape_string($_POST['name']);
		$resource = mysql_real_escape_string($_POST['resource']);
		$history_id = $_POST['history_id'];
		$user_id = $_POST['user_id'];
		$version = $_POST['version'];
			
		if ($is_node=="1") {
			$table = "category";
			$part = "";
		} else {
			$table = "species";
			$part = ",resource_link='{$resource}'";
		}
		
		$query = "SELECT version FROM {$table} WHERE id={$id}";
		$recordset = mysql_query($query);
		if (mysql_error() == "") {
		
			if (mysql_num_rows($recordset) > 0) {
				$current_version = mysql_result($recordset, 0, 'version');
				
				// check if there is a conflict
				if ((int) $version == $current_version) {
				
					// update the fish
					$query = "UPDATE {$table} SET name='{$name}'{$part},version=version+1 WHERE id={$id}";
					mysql_query($query);
					if (mysql_error() == "") {

						// record it in history
						$query = "UPDATE {$table}_history SET approved=1,date_modified=NOW() WHERE id={$history_id}";
						mysql_query($query);
						if (mysql_error() == "") {
						
							// get the email of the person who made the change
							$query = "SELECT email
									  FROM users 
									  WHERE id = {$user_id}";
							$recordset = mysql_query($query);
							if (mysql_error() == "") {
							
								// check if they exist
								if (mysql_num_rows($recordset) == 1) {
								
									// make a table
									if ($table == "species") {
										$tab2 = "<tr><td>New Resource Link:</td><td>{$resource}</td></tr>";
									} else {
										$tab2 = "";
									}
									$tab = "<table><tr><td>Name:</td><td>{$name}</td></tr>{$tab2}</table>";
								
								
									// send email to person who made the request
									$email =  mysql_result($recordset, 0, 'email');
									$message = "Congratulations!<br/><br/>Your change request has been approved.<br/><br/>{$tab}";		
									sendEmail($email, "Edit Approved", $message);
								}
							} else {
								$errorMessage = $GLOBALS['UNEXPECTED_DB_ERROR'];
							}
						} else {
							$errorMessage = $GLOBALS['UNEXPECTED_DB_ERROR'];
						}
					} else {
						$errorMessage = $GLOBALS['UNEXPECTED_DB_ERROR'];
					}
				} else {
					$errorMessage = $GLOBALS['CHANGE_CONFLICT'];
				}
			} else {
				$errorMessage = $GLOBALS['FISH_ERROR'];
			}
		} else {
			$errorMessage = $GLOBALS['UNEXPECTED_DB_ERROR'];
		}
	} else {
		$errorMessage = $GLOBALS['MISSING_INFO'];
	}

?>