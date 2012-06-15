package domain.category;

import java.util.ArrayList;
import java.util.List;

import services.database.Db;

import android.content.Context;

/*
 * Model and controller of category
 */

public class Category{
	private CategoryAdapter categoryAdapter;  
	private List<String> groups;	//main category
	private List<List<String>> children;	//sub category
	private Db db;
	private Context context;
	private int type;
	
	String expenseMain[] = {"食", "衣", "住", "行", "育", "樂"};
	String expenseSub[][] = {{"早餐", "午餐", "晚餐", "消夜", "零食飲料"},
							{"置裝費"},
							{"房租", "日常用品", "水電瓦斯"},
							{"油錢", "大眾運輸"},
							{"書報雜誌", "補習費"},
							{"運動健身", "寵物", "旅遊", "休閒娛樂"}
							};
	String incomeMain[] = {"工作", "投資", "博弈"};
	String incomeSub[][] = {{"薪水", "獎金"},
							{"股票", "債券", "外匯"},
							{"發票", "彩券"}};
	
	public Category(Context context, int type) {
		this.context = context;
		this.type = type;
		initial();
	}
	
	public void initial() {
		groups = new ArrayList<String>();
		children = new ArrayList<List<String>>();
		categoryAdapter = new CategoryAdapter(context, groups, children);
		db = new Db(context);
		db.openDB();
		List<CategoryData> categoryDate = db.getCategory();
        for (int i = 0; i < categoryDate.size(); i++) {
        	if (categoryDate.get(i).getCashflowType() == type) {
        		if (categoryDate.get(i).getType() == 0) {
        			addGroup(categoryDate.get(i).getName());        			
        		}
        		else {
        			addChild(categoryDate.get(i).getName(), 
        					categoryDate.get(i).getParent());
        		}        		
        	}
        }
        if (categoryDate.size() == 0) {
        	if (type == 0) {
        		for (String name: expenseMain) {
        			groups.add(name);
        		}
        		for (int i = 0; i < expenseSub.length; i++) {
        			List<String> sub = new ArrayList<String>();
        			for (int j = 0; j < expenseSub[i].length; j++) {
        				sub.add(expenseSub[i][j]);
        			}
        			children.add(sub);
        		}
        	} 
        	else {
        		for (String name: incomeMain) {
        			groups.add(name);
        		}
        		for (int i = 0; i < incomeSub.length; i++) {
        			List<String> sub = new ArrayList<String>();
        			for (int j = 0; j < incomeSub[i].length; j++) {
        				sub.add(incomeSub[i][j]);
        			}
        			children.add(sub);
        		}
        	}
        }
	}
	
	public void removeChild(int groupPosition, int childPosition) {    	
		children.get(groupPosition).remove(childPosition);
		categoryAdapter.notifyDataSetChanged();
    }
    
    public void removeGroup(int groupPosition) {
    	groups.remove(groupPosition);
    	children.remove(groupPosition);
    	categoryAdapter.notifyDataSetChanged();
    } 
    
    public void addGroup(String name) {
    	groups.add(name);
    	categoryAdapter.notifyDataSetChanged(); 
        
        //先加一組才不會錯
    	children.add(new ArrayList<String>());
    }
    
    public void addChild(String name, int groupId) {   
		children.get(groupId).add(name);
		categoryAdapter.notifyDataSetChanged();
	}
    
    public void editGroup(int groupPosition, String name) {
    	groups.set(groupPosition, name);
    	categoryAdapter.notifyDataSetChanged();
    }
    
	public void editChild(int groupPosition, int childPosition, String name) {
		children.get(groupPosition).set(childPosition, name);
		categoryAdapter.notifyDataSetChanged();
	}
	
	public void updateDb() {	//type means 0:expense, 1:income		
		db.removeCategory(type);	//clear db data
		for (int i =0; i < groups.size(); i++) {
			db.insertCategory(groups.get(i), 0, 0, type);	//group doesn't have parent, parent is 0
		}
		
		for (int i = 0; i < children.size(); i++) {	//Group id
			for(int j =0; j < children.get(i).size(); j++) {	//Child id
				db.insertCategory(children.get(i).get(j), 2, i, type);
			}
		}
	}
	
	public void closeDb() {
		db.closeDB();
	}

	public CategoryAdapter getCategoryAdapter() {
		return categoryAdapter;
	}

	public void setCategoryAdapter(CategoryAdapter categoryAdapter) {
		this.categoryAdapter = categoryAdapter;
	}

	public List<String> getGroups() {
		return groups;
	}

	public void setGroups(List<String> groups) {
		this.groups = groups;
	}

	public List<List<String>> getChildren() {
		return children;
	}

	public void setChildren(List<List<String>> children) {
		this.children = children;
	}	
}
