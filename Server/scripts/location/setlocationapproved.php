<?php

	if (isset($_POST['id'])) {
	
		$id = $_POST['id'];

		// update the location approved
		$query = "UPDATE location SET approved=1-approved WHERE id=" . $id;
		
		mysql_query($query);
		if (mysql_error() == "") {
			
		} else {
			$errorMessage = $GLOBALS['UNEXPECTED_DB_ERROR'];
		}
	} else {
		$errorMessage = $GLOBALS['MISSING_INFO'];
	}

?>