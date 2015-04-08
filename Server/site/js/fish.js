$(document).ready(function() {
	
	var url = "../scripts/config/sqlhandler.php";

	var data = {
		web : 0,
		page : "fish/getdatabase.php"
	};

	$.post(url, data, function(data, textStatus) {

		var tree = getFishTree(data);
		if (tree != null) {
			configureDepths(tree, 0);
			configureClicks(tree);
			addNodeToTree(tree);
		} else {
			alert("Data is corrupted.");
		}
		
	}, "json").fail(function() {
		alert( "An error occured. Try later." );
	});
});


/*
	Contructs the tree
*/


function getFishTree(data) {
	var code = data['code'];
	var result = data['response'];
	
	if (code == 200) {
		return construct_tree_data(result['category_data'], result['species_data']);
	}
	
	alert(result);
	return null;
}

function construct_tree_data(categories, species) {
	var root = null;
	var data = {};
	
	for(var i=0; i<categories.length; i+=1) {
		var cat_data = categories[i];
		var id = cat_data['id'];
		data[id] = cat_data;
		
		cat_data['children'] = [];
		cat_data['species'] = [];
		
		if (cat_data['parent_id'] == 0) {
			root = cat_data;
		}
		
		cat_data.parent = null;
	}
	for(var i=0; i<categories.length; i+=1) {
		var cat_data = categories[i];
		var parent_id = cat_data['parent_id'];
		if (parent_id != 0) {
			var parent = data[parent_id];
			if (parent != null) {
				cat_data.parent = parent;
				parent['children'].push(cat_data);
			}
		}
	}
	for(var i=0; i<species.length; i+=1) {
		var specie = species[i];
		var cat_id = specie['category_id'];
		var parent = data[cat_id];
		if (parent != null) {
			parent['species'].push(specie);
		}
	}
	
	return root;
}





function addNodeToTree(root) {
	var row = getRow(root.depth);
	var node = getNode(root);
	node.click(function() {
		root.clicked(this);
	});
	row.append(node);
}

function addSpcNodeToTree(root) {
	var tag = $("#species_container");
	var node = getSpeciesNode(root);
	tag.append(node);
}

function getRow(depth) {
	var container = $("#category_container");
	
	var rows = $("#category_container > div");
	var rowlen = rows.length;
	
	while (rowlen <= depth) {
		rowlen += 1;
		container.append("<div class='row'></div>");
	}
	
	rows = $("#category_container > div");
	return $(rows[depth]);
}

function getNode(node) {
	var elink = getMainImageLink(node);
	var str = "<div class='node cat_node' style='background-image: url(" + elink + ");' alt='" + node['name'] + "' title='" + node['name'] + "'></div>";
	return $(str);
}

function getSpeciesNode(node) {
	var elink = getMainImageLink(node);
	var str = "<div class='node spc_node' style='background-image: url(" + elink + ");' alt='" + node['name'] + "' title='" + node['name'] + "'></div>";
	return $(str);
}

function getMainImageLink(node) {
	var images = node['images'];
	var len = images.length;
	for(var i=0; i<len; i+=1) {
		var image = images[i];
		if (image['approved']) {
			var info = image['info'];
			return info['elink'];
		}
	}
	return "./images/noimage.png";
}



function emptyRows(startIndex) {
	var rows = $("#category_container > div");
	for(var i=startIndex; i<rows.length; i+=1) {
		$(rows[i]).empty();
	}
}

function unselectAdj(depth) {
	var row = getRow(depth);
	row.find("div").removeClass("selected");
}



function configureDepths(tree, depth) {
	tree.depth = depth;
	var len = tree.children.length;
	for(var i=0; i<len; i+=1) {
		var child = tree.children[i];
		configureDepths(child, depth+1);
	}
}

function configureClicks(tree) {
	var children = tree.children
	var len = children.length;
	
	tree.clicked = function(me) {
		emptyRows(tree.depth+1);
		
		
		if ($(me).hasClass("selected")) {
			$(me).removeClass("selected");
			show = tree.parent;
		} else {
			unselectAdj(tree.depth);
			$(me).addClass("selected");
			
			for(var i=0; i<len; i+=1) {
				var child = children[i];
				addNodeToTree(child);
			}
			show = tree;
		}
		
		$("#species_container").empty();
		if (show != null) {
			showSpecies(show);
		}
	};
	
	for(var i=0; i<len; i+=1) {
		var child = children[i];
		configureClicks(child);
	}
}



function showSpecies(root) {
	var list = [];
	var species = collectSpecies(root, list);
	
	var len = list.length;
	for(var i=0; i<len; i+=1) {
		var specie = list[i];
		addSpcNodeToTree(specie);
	}
}

function collectSpecies(root, list) {
	var species = root['species'];
	for(var i=0; i<species.length; i+=1) {
		list.push(species[i]);
	}
	
	var children = root['children'];
	for(var i=0; i<children.length; i+=1) {
		collectSpecies(children[i], list);
	}
}
