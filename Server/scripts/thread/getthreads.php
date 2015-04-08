<?php

	if (isset($_POST['user_id']) && isset($_POST['mine']) && isset($_POST['pagenum']) && isset($_POST['text']) && isset($_POST['page_size'])) {
	
		$user_id = $_POST['user_id'];
		$mine = $_POST['mine'];
		$pagenum = $_POST['pagenum'];
		$text = mysql_real_escape_string($_POST['text']);
		$pagelen = $_POST['page_size'];
		
		$offset = $pagelen * intval($pagenum);
		
		$cond1 = $mine == "0" ? "t.user_id" : $user_id;
		$cond2 = ConvertToSQLList($text, "c.comment");
		$cond3 = ConvertToSQLList($text, "a.title");
		
		// this part creates a query based on all the settings
		
		$query = "SELECT t.id,t.user_id,t.title,c.comment,d.has_answer,d.recent_date recent_date,MIN(i.id) image_id 
				  FROM threads t 
				  LEFT JOIN comments c ON c.thread_id = t.id
				  INNER JOIN (
						SELECT  thread_id, MAX(date_sent) recent_date, MAX(is_answer) has_answer
						FROM    comments
						GROUP   BY thread_id
					) d ON d.thread_id = c.thread_id AND d.recent_date = c.date_sent
				  LEFT JOIN thread_images i ON t.id = i.thread_id
				  WHERE t.user_id = {$cond1}
				  GROUP BY id";
		
		if ($text == "") {
			// if there was no search setting
			$query = $query . " ORDER BY recent_date DESC";
		} else {
			// if there was a search setting
			$query = "SELECT a.*
                      FROM (
						{$query}
					  ) a
					  LEFT JOIN comments c ON c.thread_id = a.id
                      WHERE (c.id IS NULL AND {$cond3}) OR {$cond2} OR {$cond3}
					  GROUP BY thread_id
					  ORDER BY count(*) DESC";
		}
		
		$query = $query . " LIMIT {$offset}, {$pagelen}";
		
		// get the results
		$recordset = mysql_query($query);
		if (mysql_error() == "") {
		
			$num_records = mysql_num_rows($recordset);
			$successMessage = array();

			
			for ($i = 0; $i < $num_records; $i++) {

				$thread_id = mysql_result($recordset, $i, 'id');
				$image_id = mysql_result($recordset, $i, 'image_id');
			
				$thread = array(
					'id' => $thread_id, 
					'user_id' => mysql_result($recordset, $i, 'user_id'),
					'title' => mysql_result($recordset, $i, 'title'),
					'comment' => mysql_result($recordset, $i, 'comment'),
					'has_answer' => mysql_result($recordset, $i, 'has_answer'),
					'recent_date' => mysql_result($recordset, $i, 'recent_date'),
					'image_link' => getThreadExternalLink($thread_id, $image_id)
				);
				
				array_push($successMessage, $thread);	
			}
		} else {
			$errorMessage = $GLOBALS['UNEXPECTED_DB_ERROR'];
		}
	} else {
		$errorMessage = $GLOBALS['MISSING_INFO'];
	}
	
?>