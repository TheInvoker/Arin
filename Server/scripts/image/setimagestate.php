<?php

	if (isset($_POST['fish_id']) && isset($_POST['id']) && isset($_POST['main_state']) && isset($_POST['used_state']) && isset($_POST['is_node'])) {
	
		$fish_id = $_POST['fish_id'];
		$id = $_POST['id'];
		$main_state = $_POST['main_state'];
		$used_state = $_POST['used_state'];
		$is_node = $_POST['is_node'];
		
		if ($is_node=="1") {
			$table = "category";
		} else {
			$table = "species";
		}
		
		if ($main_state=="1") {
		
			// remove the main status on the other pic which is main
			$query = "UPDATE {$table}_images SET main=0 WHERE {$table}_id={$fish_id} AND main=1";
			mysql_query($query);
			if (mysql_error() == "") {
				
			} else {
				$errorMessage = $GLOBALS['UNEXPECTED_DB_ERROR'];
			}
		}
		
		// update the fish image with the new information
		$query = "UPDATE {$table}_images SET main={$main_state},approved={$used_state} WHERE id={$id}";
		mysql_query($query);
		if (mysql_error() == "") {
			
		} else {
			$errorMessage = $GLOBALS['UNEXPECTED_DB_ERROR'];
		}
	} else {
		$errorMessage = $GLOBALS['MISSING_INFO'];
	}

?>