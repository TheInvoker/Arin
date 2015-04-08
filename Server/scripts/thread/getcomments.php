<?php

	if (isset($_POST['thread_id'])) {
	
		$thread_id = $_POST['thread_id'];

		
		// get all the images related to this thread
		$query = "SELECT * FROM thread_images WHERE thread_id={$thread_id}";
		$recordset = mysql_query($query);
		if (mysql_error() == "") {
		
			$num_records = mysql_num_rows($recordset);
			
			// add all links of images to an erray
			$imageArray = array();
			for ($i = 0; $i < $num_records; $i++) {
				$image_id = mysql_result($recordset, $i, 'id');

				array_push($imageArray, getThreadExternalLink($thread_id, $image_id));	
			}
			
			
			// get all comments
			$query = "SELECT c.*, u.username, u.email FROM comments c JOIN users u ON u.id=c.user_id WHERE c.thread_id={$thread_id} ORDER BY c.date_sent ASC";
			$recordset = mysql_query($query);
			if (mysql_error() == "") {
			
				$num_records = mysql_num_rows($recordset);
			
				// put all comments in an array
				$commentArray = array();
				for ($i = 0; $i < $num_records; $i++) {

					$comment = array(
						'id' => mysql_result($recordset, $i, 'id'), 
						'user_id' => mysql_result($recordset, $i, 'user_id'),
						'date_sent' => mysql_result($recordset, $i, 'date_sent'),
						'comment' => mysql_result($recordset, $i, 'comment'),
						'is_answer' => mysql_result($recordset, $i, 'is_answer'),
						'username' => mysql_result($recordset, $i, 'username'),
						'email' => mysql_result($recordset, $i, 'email')
					);
					
					array_push($commentArray, $comment);	
				}
				
				// this part assumes that, threads will have >= 1 comments at all times
				$query = "SELECT user_id FROM threads WHERE id={$thread_id}";
				
				$recordset = mysql_query($query);
				if (mysql_error() == "") {
					$successMessage = array(
						'comments' => $commentArray, 
						'images' => $imageArray,
						'op_id' => mysql_result($recordset, 0, 'user_id')
					);
				} else {
					$errorMessage = $GLOBALS['UNEXPECTED_DB_ERROR'];
				}
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