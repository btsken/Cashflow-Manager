package domain.category;

import java.util.List;

import services.database.Db;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

/*
 * Implement BaseExpandableListAdapter 
 */

public class CategoryAdapter extends BaseExpandableListAdapter{
	private Context context;	
	private List<String> groups;	//main category
	private List<List<String>> children;	//sub category
	private Db db;
	
	public CategoryAdapter(Context context, List<String> groups,
			List<List<String>> children) {
        this.context = context;
        this.groups = groups;
        this.children = children;
        db = new Db(context);
        db.openDB();
    }
	
	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return children.get(groupPosition).get(childPosition);
	}
	
	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
            View convertView, ViewGroup parent) {
		String text = children.get(groupPosition).get(childPosition);
		return getview(text);		
	}
	
	public TextView getview(String s){
		AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(  				
				ViewGroup.LayoutParams.FILL_PARENT, 54); 
		TextView view = new TextView(context);
		view.setLayoutParams(layoutParams);
		view.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
		view.setPadding(54, 0, 0, 0);  
		view.setTextColor(0xff000000);
		view.setText(s);  
		
		return view;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return children.get(groupPosition).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return groups.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return groups.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, 
			View convertView, ViewGroup parent) {
		String text = groups.get(groupPosition);		
		return getview(text);
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}	
}
