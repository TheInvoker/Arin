<?php

	if (isset($_POST['id']) && isset($_POST['is_node']) && isset($_POST['version'])) {
	
		$id = $_POST['id'];
		$is_node = $_POST['is_node'];
		$version = $_POST['version'];
			
		if ($is_node=="1") {
			$table = "category";
		} else {
			$table = "species";
		}
		
		
		$query = "SELECT version FROM {$table} WHERE id={$id}";
		
		$recordset = mysql_query($query);
		if (mysql_error() == "") {
		
			if (mysql_num_rows($recordset) > 0) {
				$current_version = mysql_result($recordset, 0, 'version');
			
				// check if there is a conflict
				if ((int) $version == $current_version) {
			
					// delete fish
					$query = "DELETE FROM {$table} WHERE id={$id}";
					
					mysql_query($query);
					if (mysql_error() == "") {
						
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