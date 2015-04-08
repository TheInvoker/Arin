<?php

	if (isset($_POST['thread_id']) && isset($_POST['comment_id']) && isset($_POST['new_state'])) {
	
		$thread_id = $_POST['thread_id'];
		$comment_id = $_POST['comment_id'];
		$new_state = $_POST['new_state'];
		
		// remove the answer status on the other one
		$query = "UPDATE comments SET is_answer=0 WHERE thread_id={$thread_id} AND is_answer=1";
		
		mysql_query($query);
		if (mysql_error() == "") {
	
			if ($new_state == 1) {
			
				// set the comment as the answer
				$query = "UPDATE comments SET is_answer=1 WHERE thread_id={$thread_id} AND id={$comment_id}";
				
				mysql_query($query);
				if (mysql_error() == "") {
					
				} else {
					$errorMessage = $GLOBALS['UNEXPECTED_DB_ERROR'];
				}
			}
		} else {
			$errorMessage = $GLOBALS['UNEXPECTED_DB_ERROR'];
		}
	} else {
		$errorMessage = $GLOBALS['MISSING_INFO'];
	}

?>