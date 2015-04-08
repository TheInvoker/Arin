<?php

	if (isset($_POST['fish_id']) && isset($_POST['is_node']) && isset($_POST['forHistory'])) {
	
		$fish_id = $_POST['fish_id'];
		$is_node = $_POST['is_node'];
		$forHistory = $_POST['forHistory'];

		// if it is a category
		if ($is_node=="1") {
			
			$query = "SELECT c.*, h.type, u.email, (CASE WHEN (Now() BETWEEN u.ban_start_date AND date_add(u.ban_start_date, interval u.ban_days day))=1 THEN datediff(date_add(u.ban_start_date, interval u.ban_days day), now()) ELSE 0 END) ban_days_left 
			          FROM category_history c 
					  JOIN `change` h ON c.change_id=h.id 
					  JOIN users u ON c.user_id=u.id 
					  WHERE c.category_id={$fish_id} AND c.approved={$forHistory}";
			$recordset = mysql_query($query);	
			if (mysql_error() == "") {
				
				$num_records = mysql_num_rows($recordset);
					
				for ($j = 0; $j < $num_records; $j++) {
					$history = array(
						'id' => mysql_result($recordset, $j, 'id'),
						'user_id' => mysql_result($recordset, $j, 'user_id'),
						'email' => mysql_result($recordset, $j, 'email'),
						'type_id' => mysql_result($recordset, $j, 'category_id'),
						'date_modified' => mysql_result($recordset, $j, 'date_modified'),
						'comment' => mysql_result($recordset, $j, 'comment'),
						'type' => mysql_result($recordset, $j, 'type'),
						'new_name' => mysql_result($recordset, $j, 'name'),
						'ban_days_left' => mysql_result($recordset, $j, 'ban_days_left')
					);
					array_push($successMessage, $history);	
				}
			} else {
				$errorMessage = $GLOBALS['UNEXPECTED_DB_ERROR'];
			}
		} else {

			$query = "SELECT c.*, h.type, u.email, (CASE WHEN (Now() BETWEEN u.ban_start_date AND date_add(u.ban_start_date, interval u.ban_days day))=1 THEN datediff(date_add(u.ban_start_date, interval u.ban_days day), now()) ELSE 0 END) ban_days_left 
			          FROM species_history c 
					  JOIN `change` h ON c.change_id=h.id 
					  JOIN users u ON c.user_id=u.id 
					  WHERE c.species_id={$fish_id} AND c.approved={$forHistory}";
			$recordset = mysql_query($query);	
			if (mysql_error() == "") {
			
				$num_records = mysql_num_rows($recordset);
				
				for ($j = 0; $j < $num_records; $j++) {
					$history = array(
						'id' => mysql_result($recordset, $j, 'id'),
						'user_id' => mysql_result($recordset, $j, 'user_id'),
						'email' => mysql_result($recordset, $j, 'email'),
						'type_id' => mysql_result($recordset, $j, 'species_id'),
						'date_modified' => mysql_result($recordset, $j, 'date_modified'),
						'comment' => mysql_result($recordset, $j, 'comment'),
						'type' => mysql_result($recordset, $j, 'type'),
						'new_name' => mysql_result($recordset, $j, 'name'),
						'new_resource_link' => mysql_result($recordset, $j, 'resource_link'),
						'ban_days_left' => mysql_result($recordset, $j, 'ban_days_left')
					);
					array_push($successMessage, $history);	
				}
			} else {
				$errorMessage = $GLOBALS['UNEXPECTED_DB_ERROR'];
			}
		}
	} else {
		$errorMessage = $GLOBALS['MISSING_INFO'];
	}

?>