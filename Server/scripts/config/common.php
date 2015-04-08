<?php

	//ini_set('display_errors',1);
	//error_reporting(E_ALL);
	
	$GLOBALS['SERVICE_ERROR'] = "Service is unavailable.";
	$GLOBALS['DB_ERROR'] = "Could not read from database.";
	$GLOBALS['DB_COM_ERROR'] = "Could not connect to database.";
	$GLOBALS['MISSING_INFO'] = "Did not recieve all of the data.";
	$GLOBALS['ACCOUNT_NOT_EXIST'] = "Account does not exist.";
	$GLOBALS['FISH_ERROR'] = "Fish not found.";
	$GLOBALS['CHANGE_CONFLICT'] = "Change conflict. Please restart ARIN.";
	$GLOBALS['UPLOAD_IMAGE_ERROR'] = "There was an error uploading the file.";
	$GLOBALS['SAME_EMAIL'] = "That email address is already registered to someone.";
	$GLOBALS['UNEXPECTED_DB_ERROR'] = "An unexpected error occured when updating database.";
	
	function getLocalLink($type, $type_id, $id) {
		$filename = "../../pics/{$type}/{$type_id}/{$id}.png";
		return $filename;
	}

	function getExternalLink($type, $type_id, $id) {
		$base = "http://" . $_SERVER['HTTP_HOST'] . dirname($_SERVER['REQUEST_URI']) . "/";
		$filename = "../../pics/{$type}/{$type_id}/{$id}.png";
		return $base . $filename;
	}
	
	function getThreadExternalLink($thread_id, $image_id) {
		if ($image_id==null) return "";
		$base = "http://" . $_SERVER['HTTP_HOST'] . dirname($_SERVER['REQUEST_URI']) . "/";
		$filename = "../../pics/threads/{$thread_id}/{$image_id}.png";
		return $base . $filename;
	}
	
	function getFileSize($type, $type_id, $id) {
		$filename = getLocalLink($type, $type_id, $id);
		if (file_exists($filename)) {
			$filesize = filesize($filename);
		} else {
			$filesize = 0;
		}
		return $filesize;
	}
	
	function getInfo($type, $type_id, $id) {
		$filename = getLocalLink($type, $type_id, $id);
		if (file_exists($filename)) {
			list($width, $height) = getImageSize($filename);
			$dimen = array( 
				'elink' => getExternalLink($type, $type_id, $id),
				'width' => $width,
				'height' => $height,
				'filesize' => getFileSize("category", $type_id, $id)
			);
			return $dimen;
		}
		return null;
	}
	
	function sendEmail($toEmail, $subject, $message) {
		$hostEmail = "rydsouza82@gmail.com";
		$message = $message . "<br/><br/><br/><br/>ARIN";		
		//$message = wordwrap($message, 70, "<br/>");
		$headers = "From: " . $hostEmail . "\r\n" .
			"Reply-To: " . $hostEmail . "\r\n" .
			"MIME-Version: 1.0" . "\r\n" .
			"Content-Type: text/html; charset=ISO-8859-1" . "\r\n";
			
		mail($toEmail, $subject, $message, $headers);
	}
	
	function ConvertToSQLList($text, $cName) {
		$pieces = explode(" ", $text);
		
		$sqlquery = "";
		foreach ($pieces as $value) {
			$sqlquery = $sqlquery . ($sqlquery == "" ? "" : " OR ") . "INSTR({$cName}, '{$value}') > 0";  
		}

		if ($sqlquery == "") {
			$sqlquery = "FALSE";
		}
		
		return $sqlquery;
	}
	
	function RunQueries($page) {
	
		$errorMessage = null;
		$successMessage = array();

		include dirname(__FILE__).'/dbinfo.php';
		$sqlConnection = mysql_connect($sqlHost, $sqlusername, $sqlpassword);
		
		if ($sqlConnection) {
		
			$sqlDB = mysql_select_db($sqldbname); 
			if ($sqlDB) {
				
				$path = dirname(__FILE__) . '/../' . $page;
				if (file_exists($path)) {
					include $path;
				} else {
					$errorMessage = $GLOBALS['SERVICE_ERROR'];
				}
				
			} else {
				$errorMessage = $GLOBALS['DB_ERROR'];
			}
			
			mysql_close($sqlConnection);
		} else {
			$errorMessage = $GLOBALS['DB_COM_ERROR'];
		}

		if ($errorMessage == null) {
			return $successMessage;
		}
		
		return $errorMessage;
	}
	
	function logQuery($str) {
		$myFile = "../../logs/log.txt";
		$fh = fopen($myFile, 'a');
		fwrite($fh, $str . "\n\n\n");
		fwrite($fh, "----------------------------------------------------\n\n");
		fclose($fh);
	}
	
?>