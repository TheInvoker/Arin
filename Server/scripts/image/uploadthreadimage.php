<?php

	if (isset($_FILES['uploadedfile']) && isset($_GET['thread_id']) && isset($_GET['user_id']) && isset($_GET['thread_title'])) {
	
		$thread_id = $_GET['thread_id'];
		$user_id = $_GET['user_id'];
		$thread_title = mysql_real_escape_string($_GET['thread_title']);
		
		// add the image to the table
		$query = "INSERT INTO thread_images (thread_id) VALUES ({$thread_id})";
		mysql_query($query);
		if (mysql_error() == "") {
		
			$thread_image_id = mysql_insert_id();
			
			
			$filename = $thread_image_id . '.png';
			$target_path = "./../../pics/threads/{$thread_id}/";
			
			
			// make the directory is not exist
			if (!is_dir($target_path)) {   
				mkdir($target_path);		
			}
			
			// move the image there
			if(!move_uploaded_file($_FILES['uploadedfile']['tmp_name'], $target_path . $filename)) {
				$errorMessage = $GLOBALS['UPLOAD_IMAGE_ERROR'];
			} else {
				
					
					
					
				$query = "SELECT u.email FROM comments c JOIN users u ON c.user_id=u.id WHERE c.thread_id={$thread_id} AND c.user_id!={$user_id} GROUP BY c.user_id";
				$recordset = mysql_query($query);
				if (mysql_error() == "") {
				
					$num_records = mysql_num_rows($recordset);
					for ($j = 0; $j < $num_records; $j++) {
						$email = mysql_result($recordset, $j, 'email');
						
						// email the person
						$message = "A picture was posted on the thread <b>{$thread_title}</b>.";		
						sendEmail($email, "Picture Added", $message);
					}
				} else {
					$errorMessage = $GLOBALS['UNEXPECTED_DB_ERROR'];
				}
				
				
				
				$successMessage = array(
					'pic_id' => $thread_image_id
				);
			}
		} else {
			$errorMessage = $GLOBALS['UNEXPECTED_DB_ERROR'];
		}
	} else {
		$errorMessage = $GLOBALS['MISSING_INFO'];
	}

?>