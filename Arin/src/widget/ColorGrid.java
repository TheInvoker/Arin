package widget;

import android.app.Activity;
import com.arin.R;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class ColorGrid {

	private Context context;
	private int offset = 0;
	private GridLayout gridlayout = null;
	private View[][] cellArray = null;
	private int[] colors = {
			Color.parseColor("#F21B7F"), 
			Color.parseColor("#09BCE5"), 
			Color.parseColor("#21e800"),
			Color.parseColor("#F2AC2A"),
			Color.parseColor("#002f86"),
			Color.parseColor("#002175")};

	public ColorGrid(Activity context) {
		this.context = context;
		this.gridlayout = (GridLayout) context.findViewById(R.id.gridlayout);
		
		ScrollView scroll = (ScrollView) context.findViewById(R.id.scrollview);
		final LinearLayout scrollContainer = (LinearLayout) scroll.getParent();
		
		scrollContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
	        @Override
	        public void onGlobalLayout() {
	        	removeOnGlobalLayoutListener(scrollContainer, this);

	        	int w = scrollContainer.getWidth();
	    		int h = scrollContainer.getHeight();
	    		int colCount = gridlayout.getColumnCount();
	    		int rowCount = gridlayout.getRowCount();
	    		int cellWidth = w/colCount;
	    		int cellHeight = h/rowCount;
	    		
	    		int y = -1;
	    		cellArray = new View[colCount][rowCount];
	    	
	    		for(int i=0; i<colCount*rowCount; i+=1) {
	    			View cell = createCell(cellWidth, cellHeight);
	    			gridlayout.addView(cell);
	    			
	    			int x = i % colCount;
	    			if (x==0) y++;
	    			cellArray[x][y] = cell;
	    		}
	        }
	    });
	}

	public void changeCellColors() {
		if (cellArray != null) {
			
			int width = cellArray.length;
			for(int x=0; x<width; x++) {
				
				View[] col = cellArray[x];
				int height = col.length;
			    for(int y=0; y<height; y++) {
			    	
			    	View cell = col[y];
			    	setCellColor(cell, x, y, width, height);
			    }
			}
			
			offset += 1;
		}
	}
	
	private void setCellColor(View cell, int x, int y, int width, int height) {
		int b = (int) Math.floor(height/2);
		int o = helper((x+offset)%height, height);
		int result1 = b + o;
		int result2 = b - o;
		
		int u = (int) (Math.floor((x+offset)/height)%2);
		
		if(y==result1) {
			cell.setBackgroundColor(colors[u]);
		} else if (y==result2) {
			cell.setBackgroundColor(colors[1-u]);
		} else if (y > Math.min(result1, result2) && y < Math.max(result1, result2)) {
			if ((x+offset)%2==0) {
				if (y >= b) {
					cell.setBackgroundColor(colors[2+u]);
				} else {
					cell.setBackgroundColor(colors[3-u]);
				}
			} else {
				if (y%2==0) {
					cell.setBackgroundColor(colors[4]);
				} else {
					cell.setBackgroundColor(colors[5]);
				}
			}
		} else {
			if (y%2==0) {
				cell.setBackgroundColor(colors[4]);
			} else {
				cell.setBackgroundColor(colors[5]);
			}
		}
	}
	
	private int helper(int x, int height) {
		if (x > height/2 - 1) {
			return height - x - 1;
		} 
		return x;
	}
	
	public void dismiss() {
		int len = gridlayout.getChildCount();
		for(int i=0; i<len; i+=1) {
			View cell = (View) gridlayout.getChildAt(i);
			cell.setBackgroundColor(Color.TRANSPARENT);
		}
	}
	
	
	
	
	private View createCell(int width, int height) {
		View cell = new View(context);
		GridLayout.LayoutParams params = new GridLayout.LayoutParams();
		params.width = width;
		params.height = height;
		cell.setLayoutParams(params);
		return cell;
	}
	
	@SuppressWarnings("deprecation")
	private void removeOnGlobalLayoutListener(View v, ViewTreeObserver.OnGlobalLayoutListener listener){
	    if (Build.VERSION.SDK_INT < 16) {
	        v.getViewTreeObserver().removeGlobalOnLayoutListener(listener);
	    } else {
	        v.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
	    }
	}
}
