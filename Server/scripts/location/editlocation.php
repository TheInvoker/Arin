<?php

	if (isset($_POST['id']) && isset($_POST['species_id']) && isset($_POST['address']) && isset($_POST['comment']) && isset($_POST['latitude']) && isset($_POST['longitude']) && isset($_POST['user_id'])) {
	
		$id = $_POST['id'];
		$species_id = $_POST['species_id'];
		$address = mysql_real_escape_string($_POST['address']);
		$comment = mysql_real_escape_string($_POST['comment']);
		$latitude = $_POST['latitude'];
		$longitude = $_POST['longitude'];
		$user_id = $_POST['user_id'];
		
		if ($id == "0") {
		
			// create the new location
			$query = "INSERT INTO location (species_id, address, latitude, longitude, comment, approved, user_id) VALUES ({$species_id}, '{$address}', {$latitude}, {$longitude}, '{$comment}', 1, {$user_id})";
			
			mysql_query($query);
			if (mysql_error() == "") {
				$location_id = mysql_insert_id();
				
				$successMessage = array(
					'location_id' => $location_id
				);
			
			} else {
				$errorMessage = $GLOBALS['UNEXPECTED_DB_ERROR'];
			}
		} else {
		
			// update the location
			$query = "UPDATE location SET address='{$address}', latitude={$latitude}, longitude={$longitude}, comment='{$comment}', user_id={$user_id} WHERE id={$id}";
			
			mysql_query($query);
			if (mysql_error() == "") {
				$successMessage = array(
					'location_id' => $id
				);
			} else {
				$errorMessage = $GLOBALS['UNEXPECTED_DB_ERROR'];
			}
		}
		
	} else {
		$errorMessage = $GLOBALS['MISSING_INFO'];
	}

?>