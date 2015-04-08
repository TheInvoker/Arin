<?php

	include dirname(__FILE__).'/common.php';
	
	$result = isset($_POST['page']) ? 
				RunQueries($_POST['page']) : 
				(isset($_GET['page']) ? 
				   RunQueries($_GET['page']) : 
				   $GLOBALS['MISSING_INFO']);

	$dat = json_encode(array("code" => (is_string($result) ? 401 : 200), "response" => $result));

	$cdat = isset($_POST['web']) ? $dat : gzcompress($dat, 9);
	
	print($cdat);
	
?>