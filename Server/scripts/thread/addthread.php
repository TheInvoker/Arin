<?php

	if (isset($_POST['id']) && isset($_POST['title']) && isset($_POST['comment']) && isset($_POST['user_id'])) {
	
		$id = $_POST['id'];
		$title = mysql_real_escape_string($_POST['title']);
		$comment = mysql_real_escape_string($_POST['comment']);
		$user_id = $_POST['user_id'];
		
		// create the new thread
		$query = "INSERT INTO threads (user_id, title, date_created) VALUES ({$id}, '{$title}', NOW())";
		mysql_query($query);
		if (mysql_error() == "") {
		
			$thread_id = mysql_insert_id();

			$query = "INSERT INTO comments (thread_id, user_id, date_sent, comment, is_answer) VALUES ({$thread_id}, {$user_id}, NOW(), '{$comment}', 0)";
			mysql_query($query);
			if (mysql_error() == "") {
				
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