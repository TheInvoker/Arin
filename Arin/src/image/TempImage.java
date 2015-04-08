package image;

import java.io.File;

import org.json.JSONObject;

import fish.Category;
import fish.Fish;

public abstract class TempImage {

	protected File file;
	protected File cfile;
	protected int fish_id;
	protected JSONObject obj;

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public JSONObject getObj() {
		return obj;
	}

	public void setObj(JSONObject obj) {
		this.obj = obj;
	}
	
	public int getFishId() {
		return fish_id;
	}
	
	public File getCommentFile() {
		return cfile;
	}
	
	public long getSize() {
		return file.length();
	}
	
	public void dispose() {
		file.delete();
		cfile.delete();
	}
	
	public abstract Boolean isCategory();
	
	public abstract Fish getFish(Category root);
}
