<?php

	if (isset($_POST['id']) && isset($_POST['parent_id']) && isset($_POST['comment']) && isset($_POST['user_id']) && isset($_POST['is_node']) && isset($_POST['version'])) {
	
		$id = $_POST['id'];
		$parent_id = $_POST['parent_id'];
		$comment = mysql_real_escape_string($_POST['comment']);
		$user_id = $_POST['user_id'];
		$is_node =  $_POST['is_node'];
		$version = $_POST['version'];
		
		if ($is_node=="1") {
			$table = "category";
			$part = "";
			$part2 = "";
			$part3 = "parent_id";
		} else {
			$table = "species";
			$part = ",resource_link";
			$part2 = ",''";
			$part3 = "category_id";
		}
		
		
		$query = "SELECT version FROM {$table} WHERE id={$id}";
		$recordset = mysql_query($query);
		if (mysql_error() == "") {
		
			if (mysql_num_rows($recordset) > 0) {
				$current_version = mysql_result($recordset, 0, 'version');
				
				// check if there is a conflict
				if ((int) $version == $current_version) {
			
					// update the fish
					$query = "UPDATE {$table} SET {$part3}={$parent_id},version=version+1 WHERE id={$id}";
					mysql_query($query);
					if (mysql_error() == "") {
					
						// record it in history
						$query = "INSERT INTO {$table}_history (user_id,{$table}_id,date_modified,comment,change_id,name,approved{$part}) VALUES ({$user_id},{$id},NOW(),'{$comment}',3,'',1{$part2})";
						mysql_query($query);
						if (mysql_error() == "") {
							
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