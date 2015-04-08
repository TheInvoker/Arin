<!DOCTYPE>
<html>
	<head>
		<script type="text/javascript" src="./js/jquery-2.1.3.min.js"></script>
		<script type="text/javascript" src="./js/fish.js"></script>
		
		
		<link rel="stylesheet" type="text/css" href="./css/fish.css">

		<link rel="icon" type="image/png" href="./images/ic_launcher.png" />
		
		<title>
			ARIN
		</title>
	</head>
	<body>
	
		<table id="fish_container">
			<tr>
				<td>
					<h2>Family Selector</h2>
					<p>
						Here you can control which fish family you want to see. Each row only displays the children categories of the selected category in the row above it. The species shown on the bottom is the collection of all species using the furthest down category you selected. With this you can narrow your total result pool by selecting families and the specific you get, the smaller the result pool will get.  
					</p>
				</td>
			</tr>
			<tr>
				<td id="category_container" valign="top">
				</td>
			</tr>
			<tr>
				<td>
					<h2>Species</h2>
					<p>
						Here is the collected species shown from the selected category above. It is a collection meaning, it combines all the species from all the categories using the selected category as the root of the category tree.
					</p>
				</td>
			</tr>
			<tr>
				<td id="species_container" valign="top">
				</td>
			</tr>
		</table>
		
	</body>
</html>
