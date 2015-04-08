<?php

	if (isset($_POST['thread_id']) && isset($_POST['comment']) && isset($_POST['user_id']) && isset($_POST['thread_title'])) {
	
		$thread_id = $_POST['thread_id'];
		$comment = mysql_real_escape_string($_POST['comment']);
		$thread_title = mysql_real_escape_string($_POST['thread_title']);
		$user_id = $_POST['user_id'];
		
		// add the new comment
		$query = "INSERT INTO comments (thread_id, user_id, date_sent, comment, is_answer) VALUES ({$thread_id}, {$user_id}, NOW(), '{$comment}', 0)";
		mysql_query($query);
		if (mysql_error() == "") {
		
			$comment_id = mysql_insert_id();
			
			
			
			
			$query = "SELECT u.email FROM comments c JOIN users u ON c.user_id=u.id WHERE c.thread_id={$thread_id} AND c.user_id!={$user_id} GROUP BY c.user_id";
			$recordset = mysql_query($query);
			if (mysql_error() == "") {
			
				$num_records = mysql_num_rows($recordset);
				for ($j = 0; $j < $num_records; $j++) {
					$email = mysql_result($recordset, $j, 'email');
					
					// email the person
					$message = "A comment was posted on the thread <b>{$thread_title}</b>.<br/><br/><i>{$comment}</i>";		
					sendEmail($email, "Comment Added", $message);
				}

				
				
				
				$successMessage = array(
					'comment_id' => $comment_id
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