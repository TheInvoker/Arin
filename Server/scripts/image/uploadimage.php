<?php

	if (isset($_FILES['uploadedfile']) && 
		isset($_GET['user_id']) && 
		isset($_GET['fish_id']) && 
		isset($_GET['is_node']) && 
		isset($_GET['comment']) && 
		isset($_GET['lat']) && 
		isset($_GET['long']) && 
		isset($_GET['addr'])) {

		$user_id = $_GET['user_id'];
		$fish_id = $_GET['fish_id'];
		$is_node = $_GET['is_node'];
		$comment = mysql_real_escape_string($_GET['comment']);
		$lat = $_GET['lat'];
		$long = $_GET['long'];
		$addr = mysql_real_escape_string($_GET['addr']);
		
		if ($is_node=="1") {
			$table = "category";
		} else {
			$table = "species";
		}

		// add the fish image to the table
		$query = "INSERT INTO {$table}_images ({$table}_id,comment,approved,main,date_added,user_id) VALUES ({$fish_id},'{$comment}',1,0,NOW(),{$user_id})";
		mysql_query($query);
		if (mysql_error() == "") {
		
			$pic_id = mysql_insert_id();
			
			
			$filename = $pic_id . '.png';
			$target_path = "./../../pics/{$table}/{$fish_id}/";
			
			
			// make the directory if not exist
			if (!is_dir($target_path)) {   
				mkdir($target_path);		
			}

			// move the image there
			if(!move_uploaded_file($_FILES['uploadedfile']['tmp_name'], $target_path . $filename)) {
				$errorMessage = $GLOBALS['UPLOAD_IMAGE_ERROR'];
			} else {

				$location_id = 0;
				if ($is_node != "1" && $addr != "") {
					// create the new location
					$query = "INSERT INTO location (species_id, address, latitude, longitude, comment, approved) VALUES ({$fish_id}, '{$addr}', {$lat}, {$long}, '', 1)";
					
					mysql_query($query);
					if (mysql_error() == "") {
						$location_id = mysql_insert_id();
					}
				}
				
				$successMessage = array(
					'pic_id' => $pic_id,
					'elink' => getExternalLink($table, $fish_id, $pic_id),
					'locationId' => $location_id
				);
			}
		} else {
			$errorMessage = $GLOBALS['UNEXPECTED_DB_ERROR'];
		}
	} else {
		$errorMessage = $GLOBALS['MISSING_INFO'];
	}

?>