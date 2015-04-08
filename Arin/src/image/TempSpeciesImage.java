package image;

import java.io.File;

import org.json.JSONObject;

import fish.Category;
import fish.Fish;

public class TempSpeciesImage extends TempImage {
	
	public TempSpeciesImage(File file, File cfile, JSONObject obj, int fish_id) {
		this.file = file;
		this.cfile = cfile;
		this.fish_id = fish_id;
		this.obj = obj;
	}
	
	@Override
	public Boolean isCategory() {
		return false;
	}
	
	@Override
	public Fish getFish(Category root) {
		return root.getSpeciesFromId(fish_id);
	}
}
