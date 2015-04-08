<?php

	if (isset($_POST['id']) && isset($_POST['is_node'])) {
	
		$id = $_POST['id'];
		$is_node = $_POST['is_node'];

		if ($is_node=="1") {
			$table = "category";
		} else {
			$table = "species";
		}
		
		$query = "SELECT (SELECT version FROM {$table} WHERE id={$id}) version,
		        (SELECT count(*) FROM {$table}_history WHERE approved=0 AND {$table}_id={$id}) unapproved_history, 
				(SELECT count(*) FROM {$table}_history WHERE approved=1 AND {$table}_id={$id}) approved_history,
				(SELECT count(*) FROM {$table}_images WHERE approved=0 AND {$table}_id={$id}) unapproved_images, 
				(SELECT count(*) FROM {$table}_images WHERE approved=1 AND {$table}_id={$id}) approved_images";
				
		if ($is_node=="0") {
			$query = $query . ", (SELECT count(*) FROM location WHERE approved=0 AND {$table}_id={$id}) unapproved_locations,
				                 (SELECT count(*) FROM location WHERE approved=1 AND {$table}_id={$id}) approved_locations";
		} else {
			$query = $query . ", (SELECT count(*) FROM species WHERE category_id={$id}) species";
		}		

		$recordset = mysql_query($query);	
		if (mysql_error() == "") {
		
			$successMessage = array(
				'version' => mysql_result($recordset, 0, 'version'),
				'unapproved_history' => mysql_result($recordset, 0, 'unapproved_history'),
				'approved_history' => mysql_result($recordset, 0, 'approved_history'),
				'unapproved_images' => mysql_result($recordset, 0, 'unapproved_images'),
				'approved_images' => mysql_result($recordset, 0, 'approved_images'),
				'unapproved_locations' => $is_node=="0" ? mysql_result($recordset, 0, 'unapproved_locations') : 0,
				'approved_locations' => $is_node=="0" ? mysql_result($recordset, 0, 'approved_locations') : 0,
				'species' => $is_node=="1" ? mysql_result($recordset, 0, 'species') : 0
			);
		
		} else {
			$errorMessage = $GLOBALS['UNEXPECTED_DB_ERROR'];
		}
	} else {
		$errorMessage = $GLOBALS['MISSING_INFO'];
	}

?>