<!DOCTYPE>
<html>
	<head>
		<script type="text/javascript" src="./js/jquery-2.1.3.min.js"></script>
		<script type="text/javascript" src="./js/main.js"></script>
		
		<link rel="stylesheet" type="text/css" href="./css/main.css">

		<link rel="icon" type="image/png" href="./images/ic_launcher.png" />
		
		<title>
			ARIN
		</title>
	</head>
	<body>
		
		<nav>
			<table align="right">
				<tr>
					<td>
						<a href="http://arin.esy.es/arin/site" title="Home" alt="Go to Home">
							<img src="./images/ic_launcher.png"/>
						</a>
					</td>
					<td>
						<span class="title">ARIN</span>
						<br/>
						<span class="title_sub">Aquatic Recognition Index Network</span>
					</td>
					<td style="padding-left:10px;">
						<div id="loggedoutArea">
							<form>
								<input type="email" placeholder="email address" id="emailfield"><br/>
								<input type="password" placeholder="password" id="passwordfield"><br/>
								<input type="submit" value="Login" id="loginbutton" onclick="return false;">
							</form>
						</div>
						<div id="loggedinArea" style="display:none;">
							<form>
								<input type="submit" value="Logout" id="logoutbutton" onclick="return false;">
							</form>
						</div>
					</td>
				</tr>
			</table>
		</nav>
		
		<article id="bodyloggedout" class="maincontent">
			You are currently logged out.
		</article>
		
		<article id="bodyloggedin" class="maincontent" style="display:none;">
			
			<h2>My Profile</h2>
			<table id="profiletable" class="mytable">
				<tr>
					<td>
						Username
					</td>
					<td>
						Role
					</td>
					<td>
						Ban Start Date
					</td>
					<td>
						Days Banned
					</td>
				</tr>
				<tr>
					<td>
					</td>
					<td>
					</td>
					<td>
					</td>
					<td>
					</td>
				</tr>
			</table>

			
			
			<hr/>
			
			
			<h2>Search User</h2>
			<input type="text" placeholder="email address" id="searchperson">
			<button id="searchbutton">Search</button>
			
			<table id="searchtable" class="mytable">
				<tr>
					<td>
						Username
					</td>
					<td>
						Role
					</td>
					<td>
						Ban Start Date
					</td>
					<td>
						Days Banned
					</td>
				</tr>
				<tr>
					<td>
					</td>
					<td>
					</td>
					<td>
					</td>
					<td>
					</td>
				</tr>
			</table>
			
		</article>	
			
			
		<article class="maincontent">	
			<hr/>
			
			<h2>Actions</h2>
			<table id="main_actions">
				<tr>
					<td id="main_1" onclick="window.open('./species.php', '_blank');" title="Species" alt="Go to Species">
						<img src="./images/species.png"/> Species
					</td>
					<td id="main_2" onclick="window.open('http://arin.esy.es/arin/phpBB3', '_blank');" title="Forums" alt="Go to Forums">
						<img src="./images/forum.png"/> Forums
					</td>
				</tr>
			</table>
		</article>
		
		<footer>
			<table align="right">
				<tr>
					<td>
						<a href="https://play.google.com/store/apps/details?id=com.arin" target="_blank">Download App</a>
					</td>
					<td>
						<a href="./policy.php" target="_blank">Privacy Policy/Terms of Service</a>
					</td>
					<td>
						© Copyright 2014 ARIN
					</td>
				</tr>
			</table>
		</footer>
		
	</body>
</html>
