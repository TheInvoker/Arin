<?php

	if (isset($_POST['id']) && isset($_POST['is_node'])) {
	
		$id = $_POST['id'];
		$is_node = $_POST['is_node'];
		
		if ($is_node=="1") {
			$table = "category";
		} else {
			$table = "species";
		}
		
		$query = "DELETE FROM {$table}_images WHERE id={$id}";
		
		mysql_query($query);
		if (mysql_error() == "") {
			
		} else {
			$errorMessage = $GLOBALS['UNEXPECTED_DB_ERROR'];
		}
	} else {
		$errorMessage = $GLOBALS['MISSING_INFO'];
	}

?>