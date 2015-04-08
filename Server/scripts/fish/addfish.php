<?php

	if (isset($_POST['id']) && isset($_POST['add_category']) && isset($_POST['name']) && isset($_POST['resource']) && isset($_POST['comment']) && isset($_POST['user_id'])) {
	
		$id = $_POST['id'];
		$add_category = $_POST['add_category'];
		$name = mysql_real_escape_string($_POST['name']);
		$resource = mysql_real_escape_string($_POST['resource']);
		$comment = mysql_real_escape_string($_POST['comment']);
		$user_id = $_POST['user_id'];
			
		if ($add_category=="1") {
			$table = "category";
			$part1 = "";
			$part2 = "";
			
			$query = "INSERT INTO category (parent_id,name,version) VALUES ({$id},'{$name}',1)";
		} else {
			$table = "species";
			$part1 = ",resource_link";
			$part2 = ",'{$resource}'";
			
			$query = "INSERT INTO species (category_id,name,resource_link,version) VALUES ({$id},'{$name}','{$resource}',1)";
		}
		
		// create the new fish
		mysql_query($query);
		if (mysql_error() == "") {
			
			$new_id = mysql_insert_id();
			
			// add to the history
			$query = "INSERT INTO {$table}_history (user_id,{$table}_id,date_modified,comment,change_id,name{$part1},approved) VALUES ({$user_id},{$new_id},NOW(),'{$comment}',1,'{$name}'{$part2},1)";
			
			mysql_query($query);
			if (mysql_error() == "") {
			
				$successMessage = array(
					'fish_id' => $new_id
				);
			} else {
				$errorMessage = $GLOBALS['UNEXPECTED_DB_ERROR'];
			}
		} else {
			$errorMessage = $GLOBALS['UNEXPECTED_DB_ERROR'];
		}
	} else {
		$errorMessage = $GLOBALS['MISSING_INFO'];
	}

?>