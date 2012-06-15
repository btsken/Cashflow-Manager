package ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;
import android.widget.Spinner;
import android.widget.TextView;
import domain.category.Category;

/*
 * Manager Category Activity
 * To do category C, R, U, D
 */

public class CategoryActivity extends Activity{
	private ExpandableListView expenseList;
	private ExpandableListView incomeList;
	private Category expeneCategory;
	private Category incomeCategory;
	private TextView expenseTextView;
	private TextView incomeTextView;
	private AlertDialog groupDialog;
	private AlertDialog childDialog;
	private int type;	//0:expense, 1:income
	
	private final int MENU_BUTTON_1 = Menu.FIRST; 
	private final int MENU_BUTTON_2 = Menu.FIRST+1; 
	private final String longClickText = "想做甚麼?";
	private final String editText = "編輯";
	private final String deleteText = "刪除";
	private final String newMainCategoryText = "新增主分類";
	private final String newSubCategoryText = "新增次分類";
	private final String confirmTitleText = "確定刪除嗎?";
	private final String confirmMessageText = "次分類也會一併清掉喔";

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category);
        findViews();
        setViews();
        initial();
        
        expenseList.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
        	public void onCreateContextMenu(ContextMenu menu, View v,
					ContextMenuInfo menuInfo) {
				menu.setHeaderTitle(longClickText);				
				menu.add(0, MENU_BUTTON_1, 0, editText);
				menu.add(0, MENU_BUTTON_2, 0, deleteText);
			}			
		});   
        incomeList.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
        	public void onCreateContextMenu(ContextMenu menu, View v,
					ContextMenuInfo menuInfo) {
				menu.setHeaderTitle(longClickText);				
				menu.add(0, MENU_BUTTON_1, 0, editText);
				menu.add(0, MENU_BUTTON_2, 0, deleteText);
			}			
		});   
	}	
	
	public void setViews() {
		expenseTextView.setOnClickListener(expenseClick);
		incomeTextView.setOnClickListener(incomeClick);
    }
	
	private TextView.OnClickListener incomeClick = new TextView.OnClickListener() {
    	public void onClick(View v){
    		type = 1;
    		expenseList.setVisibility(View.GONE);
    		incomeList.setVisibility(View.VISIBLE);
    		expenseTextView.setBackgroundColor(0xff99a686);
    		incomeTextView.setBackgroundColor(0xff5c734c);
    	}
    };
    
    private TextView.OnClickListener expenseClick = new TextView.OnClickListener() {
    	public void onClick(View v){
    		type = 0;
    		expenseList.setVisibility(View.VISIBLE);
    		incomeList.setVisibility(View.GONE);
    		expenseTextView.setBackgroundColor(0xff5c734c);
    		incomeTextView.setBackgroundColor(0xff99a686);
    	}
    };
    
    @Override
	public boolean onContextItemSelected(MenuItem item) {
		boolean flag=false;		
		ExpandableListContextMenuInfo menuInfo=(ExpandableListContextMenuInfo)item.getMenuInfo();
		int type=ExpandableListView.getPackedPositionType(menuInfo.packedPosition);
		
		//child
		if(type==ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
			int	groupPos =ExpandableListView.getPackedPositionGroup(menuInfo.packedPosition);
			int	childPos =ExpandableListView.getPackedPositionChild(menuInfo.packedPosition);			
			flag= true;
			
			switch(item.getItemId()) {	
			case MENU_BUTTON_1:
				editChildDialog(groupPos, childPos);	
				break;     
			case MENU_BUTTON_2:
				expeneCategory.removeChild(groupPos, childPos);
				break;   
			}			
		}	
		
		//group 
		else if(type == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
            int groupPos = ExpandableListView.getPackedPositionGroup(menuInfo.packedPosition);             
            flag= true;
            
            switch(item.getItemId()) {	
			case MENU_BUTTON_1:
				editGroupDialog(groupPos);
				break;    
			case MENU_BUTTON_2:
				confirmDialog(groupPos);
				break;   
            }	
        }	
    
		return flag;
	}
	
	public void findViews() {
		expenseList = (ExpandableListView)findViewById(R.id.expandableListView1);
		incomeList = (ExpandableListView)findViewById(R.id.expandableListView2);
    	expenseTextView = (TextView)findViewById(R.id.expense);
    	incomeTextView = (TextView)findViewById(R.id.income);
    }
	
	public void initial() {
		type = 0;	//initial is expense category
		expeneCategory = new Category(this, 0);
		incomeCategory = new Category(this, 1);
		expenseList.setAdapter(expeneCategory.getCategoryAdapter());
		incomeList.setAdapter(incomeCategory.getCategoryAdapter());	
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_BUTTON_1, 0, newMainCategoryText)
        .setIcon(android.R.drawable.ic_menu_add);
        menu.add(0, MENU_BUTTON_2, 0, newSubCategoryText)
        .setIcon(android.R.drawable.ic_menu_add);
        return super.onCreateOptionsMenu(menu);
    }
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
        case MENU_BUTTON_1:    
        	newGroupDialog();
            break;            
        case MENU_BUTTON_2:    	
        	newChildDialog();
            break;
        default:
        	break;
        }
 
        return super.onOptionsItemSelected(item);
    }	
	
	public void newGroupDialog() {
		//客製化視窗產生。
        LayoutInflater factory = LayoutInflater.from(this);
        View customerView = factory.inflate(R.layout.new_group, null);
        groupDialog = new AlertDialog.Builder(this).create();
        
        Button newAccountButton = (Button)customerView.findViewById(R.id.newAccountButton);
        final EditText name = (EditText)customerView.findViewById(R.id.startDateEditText);        
        groupDialog.setView(customerView, 0,0,0,0);
        newAccountButton.setOnClickListener(new OnClickListener(){     
        	@Override     
            public void onClick(View v) {  
        		if (type == 0) { 
        			expeneCategory.addGroup(name.getText().toString());   
        		}
        		else {
        			incomeCategory.addGroup(name.getText().toString()); 
        		}
        		groupDialog.dismiss();
        	}
        });
		//顯示dialog
        groupDialog.show();
	}
    
    public void newChildDialog() {
		//客製化視窗產生。
        LayoutInflater factory = LayoutInflater.from(this);
        View customerView = factory.inflate(R.layout.new_child, null);
        childDialog = new AlertDialog.Builder(this).create();
        
        Button newAccountButton = (Button)customerView.findViewById(R.id.newAccountButton);
        final EditText name = (EditText) customerView.findViewById(R.id.nameEditText);
        final Spinner spinner = (Spinner)customerView.findViewById(R.id.group);
        ArrayAdapter<String>  adapterSpinner = new ArrayAdapter<String>
        									(this,android.R.layout.simple_spinner_item);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        
        //put main category into spinner
        if (type == 0) {        	
        	for(int i = 0; i < expeneCategory.getGroups().size(); i++) {
        		adapterSpinner.add(expeneCategory.getGroups().get(i));
        	}
        }
        else {
        	for(int i = 0; i < incomeCategory.getGroups().size(); i++) {
        		adapterSpinner.add(incomeCategory.getGroups().get(i));
        	}
        }
        
        spinner.setAdapter(adapterSpinner);
        childDialog.setView(customerView, 0,0,0,0);
        newAccountButton.setOnClickListener(new OnClickListener(){     
        	@Override     
            public void onClick(View v) {    
        		String groupName = (String) spinner.getSelectedItem();
            	int groupPosition = 0;
            	
            	//get spinner position
            	if (type == 0) { 
            		for(int i = 0; i < expeneCategory.getGroups().size(); i++) {
            			if (groupName.equals(expeneCategory.getGroups().get(i))) {
            				groupPosition = i;
            			}
            		}      	
            		
            		expeneCategory.addChild(name.getText().toString(), groupPosition);
            	}
            	else {
            		for(int i = 0; i < incomeCategory.getGroups().size(); i++) {
            			if (groupName.equals(incomeCategory.getGroups().get(i))) {
            				groupPosition = i;
            			}
            		}     	
            		
            		incomeCategory.addChild(name.getText().toString(), groupPosition);
            	}
            	childDialog.dismiss();
        	}
        });
		//顯示dialog
        childDialog.show();
	}
    
    public void editGroupDialog(final int groupPos) {
		//客製化視窗產生。
        LayoutInflater factory = LayoutInflater.from(this);
        View customerView = factory.inflate(R.layout.edit_group, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        
        Button newAccountButton = (Button)customerView.findViewById(R.id.newAccountButton);        
        final EditText name = (EditText)customerView.findViewById(R.id.startDateEditText);
                
        alertDialog.setView(customerView, 0,0,0,0);
        newAccountButton.setOnClickListener(new OnClickListener(){     
        	@Override     
            public void onClick(View v) { 
        		if (type == 0) { 
        			expeneCategory.editGroup(groupPos, name.getText().toString());        			
        		}
        		else {
        			incomeCategory.editGroup(groupPos, name.getText().toString()); 
        		}
        		alertDialog.dismiss();
        	}
        });
		//顯示dialog
		alertDialog.show();
	}
    
    public void editChildDialog(final int groupPos, final int childPos) {
		//客製化視窗產生。
        LayoutInflater factory = LayoutInflater.from(this);
        View customerView = factory.inflate(R.layout.edit_child, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        
        Button newAccountButton = (Button)customerView.findViewById(R.id.newAccountButton);
        final EditText name = (EditText) customerView.findViewById(R.id.nameEditText);
        alertDialog.setView(customerView, 0,0,0,0);
        newAccountButton.setOnClickListener(new OnClickListener(){     
        	@Override     
            public void onClick(View v) {  
        		if (type == 0) {         			
        			expeneCategory.editChild(groupPos, childPos, name.getText().toString());
        		}
        		else {
        			incomeCategory.editChild(groupPos, childPos, name.getText().toString());
        		}
            	alertDialog.dismiss();
        	}
        });
		//顯示dialog
		alertDialog.show();
	}
    
    private void confirmDialog(final int groupPos) { 
    	new AlertDialog.Builder(this)
		.setTitle(confirmTitleText)
		.setMessage(confirmMessageText)
		.setPositiveButton(deleteText,
			new DialogInterface.OnClickListener() { 
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (type == 0) {      						
						expeneCategory.removeGroup(groupPos);
					}
					else {
						incomeCategory.removeGroup(groupPos);
					}
				}
			}
		)
		.show();
    } 
	
	@Override
    public void onDestroy() {
		super.onDestroy();
		Log.d("destory", "destory");
		expeneCategory.updateDb();
		incomeCategory.updateDb();
		expeneCategory.closeDb();
		incomeCategory.closeDb();
    }
}
