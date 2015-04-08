var role_id = 0;

$(document).ready(function() {
	
	$("#loginbutton").click(function() {
		var email = $("#emailfield").val();
		var password = $("#passwordfield").val();
		
		var url = "../scripts/config/sqlhandler.php";

		var data = {
			web : 0,
			page : "user/login.php",
			email : email,
			password : password
		};

		$.post(url, data, function(data, textStatus) {

			var code = data['code'];
			if (code == 200) {
				var result = data['response'];
				
				toggleFields();
				
				setUserData(result);
			} else {
				alert(data['response']);
			}
			
		}, "json").fail(function() {
			alert( "An error occured. Try later." );
		});
	});
	
	$("#logoutbutton").click(function() {
		toggleFields();
	});
	
	$("#searchbutton").click(function() {
		var email = $("#searchperson").val();
		
		var url = "../scripts/config/sqlhandler.php";

		var data = {
			web : 0,
			page : "user/getuser.php",
			email : email
		};

		$.post(url, data, function(data, textStatus) {

			var code = data['code'];
			if (code == 200) {
				var result = data['response'];
				setSearchData(result, email);
			} else {
				alert(data['response']);
			}
			
		}, "json").fail(function() {
			alert( "An error occured. Try later." );
		});
	});
});

function toggleFields() {
	$("#loggedoutArea").toggle();
	$("#loggedinArea").toggle();
	$("#bodyloggedout").toggle();
	$("#bodyloggedin").toggle();
	
	var a = $("#profiletable").find("tr").eq(1).find("td");
	var b = $("#searchtable").find("tr").eq(1).find("td");
	a.empty();
	b.empty();
}

function setUserData(result) {
	setUserField(result);
	role_id = result['role_id'];
}
function setUserField(result) {
	var tag = $("#profiletable > tbody > tr").eq(1).find("td");
	
	$(tag[0]).html(result['username']);
	$(tag[1]).html(formatUserField(result['role_id']));
	$(tag[2]).html(result['ban_start_date']);
	$(tag[3]).html(result['ban_days']);
}
function formatUserField(rid) {
	return getRoleName(rid);
}





function setSearchData(result, email) {
	setSearchField(result, email);
}
function setSearchField(result, email) {
	var tag = $("#searchtable > tbody > tr").eq(1).find("td");
	
	$(tag[0]).html(result['username']);
	$(tag[1]).html(formatSearchField(result['role_id'], email));
	$(tag[2]).html(result['ban_start_date']);
	$(tag[3]).html(result['ban_days']);
}
function formatSearchField(rid, email) {
	if (role_id == 1) {
		return getRoleName(rid);
	} else if (role_id == 2) {
		return "<select onchange=\"change_role(this.options[this.selectedIndex].value, '" + email + "');\"><option " + (rid==1?"selected" : "") + " value='1'>User</option><option " + (rid==2?"selected" : "") + " value='2'>Biologist</option></select>";
	} else {
		return getRoleName(rid);
	}
}



function getRoleName(role_id) {
	if (role_id == 1) {
		return "User";
	} else if (role_id == 2) {
		return "Biologist";
	} else {
		return "Unknown";
	}
}


function change_role(rid, email) {

	var url = "../scripts/config/sqlhandler.php";

	var data = {
		web : 0,
		page : "user/setpower.php",
		email : email,
		rid : rid
	};

	$.post(url, data, function(data, textStatus) {

		var code = data['code'];
		if (code == 200) {
			alert("Changed!");
		} else {
			alert(data['response']);
		}
		
	}, "json").fail(function() {
		alert( "An error occured. Try later." );
	});
}


