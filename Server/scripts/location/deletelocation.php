<?php

	if (isset($_POST['id'])) {
	
		$id = $_POST['id'];

		// delete the location
		$query = "DELETE FROM location WHERE id=" . $id;
		
		mysql_query($query);
		if (mysql_error() == "") {
			
		} else {
			$errorMessage = $GLOBALS['UNEXPECTED_DB_ERROR'];
		}
	} else {
		$errorMessage = $GLOBALS['MISSING_INFO'];
	}

?>