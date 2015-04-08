<?php

	if (isset($_POST['id']) && isset($_POST['is_node']) && isset($_POST['name']) && isset($_POST['resource']) && isset($_POST['comment']) && isset($_POST['user_id'])) {
	
		$id = $_POST['id'];
		$is_node = $_POST['is_node'];
		$name = mysql_real_escape_string($_POST['name']);
		$resource = mysql_real_escape_string($_POST['resource']);
		$comment = mysql_real_escape_string($_POST['comment']);
		$user_id = $_POST['user_id'];
			
		if ($is_node=="1") {
			$table = "category";
			$part1 = "";
			$part2 = "";
		} else {
			$table = "species";
			$part1 = ",resource_link";
			$part2 = ",'{$resource}'";
		}
		
		// record edit request
		$query = "INSERT INTO {$table}_history (user_id,{$table}_id,date_modified,comment,change_id,name{$part1},approved) VALUES ({$user_id},{$id},NOW(),'{$comment}',2,'{$name}'{$part2},0)";
		
		mysql_query($query);
		if (mysql_error() == "") {
			
		} else {
			$errorMessage = $GLOBALS['UNEXPECTED_DB_ERROR'];
		}
	} else {
		$errorMessage = $GLOBALS['MISSING_INFO'];
	}

?>