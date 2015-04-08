<?php

	$category_container = array();
	$species_container = array();
	
	///////////////////////////////////////////////////////////
	
	// get all the categories
	$query = "SELECT * FROM category";
	$recordset = mysql_query($query);	
	if (mysql_error() == "") {
	
		$num_records = mysql_num_rows($recordset);
		
		for ($i = 0; $i < $num_records; $i++) {
			$id = mysql_result($recordset, $i, 'id');
			
			// get all the images
			$query = "SELECT c.*, (CASE WHEN (Now() BETWEEN u.ban_start_date AND date_add(u.ban_start_date, interval u.ban_days day))=1 THEN datediff(date_add(u.ban_start_date, interval u.ban_days day), now()) ELSE 0 END) ban_days_left  
			          FROM category_images c
					  JOIN users u on c.user_id=u.id
					  WHERE c.category_id=" . $id;
			$recordset2 = mysql_query($query);
			if (mysql_error() == "") {
			
				$num_records2 = mysql_num_rows($recordset2);
				$image_container = array();
				for ($j = 0; $j < $num_records2; $j++) {
				
					// get image size and dimensions
					$image_id = mysql_result($recordset2, $j, 'id');
					$info = getInfo("category", $id, $image_id);
					
					if ($info != null) {
						$image = array(
							'id' => $image_id, 
							'comment' => mysql_result($recordset2, $j, 'comment'),
							'approved' => mysql_result($recordset2, $j, 'approved'),
							'main' => mysql_result($recordset2, $j, 'main'),
							'date_added' => mysql_result($recordset2, $j, 'date_added'),
							'user_id' => mysql_result($recordset2, $j, 'user_id'),
							'ban_days_left' => mysql_result($recordset2, $j, 'ban_days_left'),
							'info' => $info
						);
						
						array_push($image_container, $image);
					}
				}
			} else {
				$errorMessage = $GLOBALS['UNEXPECTED_DB_ERROR'];
			}
			
			$fish = array(
				'id' => $id, 
				'parent_id' => mysql_result($recordset, $i, 'parent_id'),
				'name' => mysql_result($recordset, $i, 'name'),
				'version' => mysql_result($recordset, $i, 'version'),
				'images' => $image_container
			);
			
			array_push($category_container, $fish);
		}
	} else {
		$errorMessage = $GLOBALS['UNEXPECTED_DB_ERROR'];
	}
	
	
	//////////////////////////////////////////////////////////
	
	
	$query = "SELECT * FROM species";
	$recordset = mysql_query($query);	
	if (mysql_error() == "") {
	
		$num_records = mysql_num_rows($recordset);
		
		for ($i = 0; $i < $num_records; $i++) {
			$id = mysql_result($recordset, $i, 'id');
			
			// get all the images
			$query = "SELECT s.*, (CASE WHEN (Now() BETWEEN u.ban_start_date AND date_add(u.ban_start_date, interval u.ban_days day))=1 THEN datediff(date_add(u.ban_start_date, interval u.ban_days day), now()) ELSE 0 END) ban_days_left   
			          FROM species_images s
					  JOIN users u on s.user_id=u.id
					  WHERE s.species_id=" . $id;
			$recordset2 = mysql_query($query);
			if (mysql_error() == "") {
			
				$num_records2 = mysql_num_rows($recordset2);
				$image_container = array();
				for ($j = 0; $j < $num_records2; $j++) {
				
					// get image size and dimensions
					$image_id = mysql_result($recordset2, $j, 'id');
					$info = getInfo("species", $id, $image_id);
					
					if ($info != null) {
						$image = array(
							'id' => $image_id, 
							'comment' => mysql_result($recordset2, $j, 'comment'),
							'approved' => mysql_result($recordset2, $j, 'approved'),
							'main' => mysql_result($recordset2, $j, 'main'),
							'date_added' => mysql_result($recordset2, $j, 'date_added'),
							'user_id' => mysql_result($recordset2, $j, 'user_id'),
							'ban_days_left' => mysql_result($recordset2, $j, 'ban_days_left'),
							'info' => $info
						);
						
						array_push($image_container, $image);
					}
				}
				
			} else {
				$errorMessage = $GLOBALS['UNEXPECTED_DB_ERROR'];
			}
				
			// get all the locations
			$query = "SELECT l.*, (CASE WHEN (Now() BETWEEN u.ban_start_date AND date_add(u.ban_start_date, interval u.ban_days day))=1 THEN datediff(date_add(u.ban_start_date, interval u.ban_days day), now()) ELSE 0 END) ban_days_left   
			          FROM location l
					  JOIN users u on l.user_id=u.id
					  WHERE l.species_id=" . $id;
			$recordset2 = mysql_query($query);
			if (mysql_error() == "") {
			
				$num_records2 = mysql_num_rows($recordset2);
				$location_container = array();
				for ($j = 0; $j < $num_records2; $j++) {
					$location = array(
						'id' => mysql_result($recordset2, $j, 'id'), 
						'address' => mysql_result($recordset2, $j, 'address'),
						'latitude' => mysql_result($recordset2, $j, 'latitude'),
						'longitude' => mysql_result($recordset2, $j, 'longitude'),
						'comment' => mysql_result($recordset2, $j, 'comment'),
						'approved' => mysql_result($recordset2, $j, 'approved'),
						'user_id' => mysql_result($recordset2, $j, 'user_id'),
						'ban_days_left' => mysql_result($recordset2, $j, 'ban_days_left')
					);
					
					array_push($location_container, $location);
				}
				
			} else {
				$errorMessage = $GLOBALS['UNEXPECTED_DB_ERROR'];
			}
				
			$fish = array(
				'id' => $id, 
				'name' => mysql_result($recordset, $i, 'name'),
				'version' => mysql_result($recordset, $i, 'version'),
				'resource_link' => mysql_result($recordset, $i, 'resource_link'),
				'category_id' => mysql_result($recordset, $i, 'category_id'),
				'images' => $image_container,
				'locations' => $location_container
			);
			
			array_push($species_container, $fish);
		}
	} else {
		$errorMessage = $GLOBALS['UNEXPECTED_DB_ERROR'];
	}


	///////////////////////////////////////////////////////////

	$successMessage = array(
		'category_data' => $category_container, 
		'species_data' => $species_container
	);

?>