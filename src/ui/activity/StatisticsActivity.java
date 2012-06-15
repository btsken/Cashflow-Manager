package ui.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import domain.cashflow.Cashflow;
import domain.cashflow.Expense;
import domain.cashflow.Income;
import domain.category.Category;
import domain.statistics.Statistics;

/*
 * show Statistics view and actions
 */

public class StatisticsActivity extends Activity {
	private ListView list;
	private Statistics statistics;	
	private SimpleAdapter listItemAdapter;
	private int pic[] = {R.drawable.z2, R.drawable.money};
	private AlertDialog manageExpenseDialog;
	private AlertDialog manageIncomeDialog;
	private String mainSelectString = "";
    private String subSelectString = "";
    private List<List<String>> subList;	//sub category list of spinner
	private EditText expenseDateEditText;
	private EditText incomeDateEditText;
	private Cashflow expense;
	private Cashflow income;
	private String time;	//上一頁傳進來的時間區間
	private final int DATE_EXPENSE_ID = 0;	//expense
    private final int DATE_INCOME_ID = 1;	//income
    private int mYear;
    private int mMonth;
    private int mDay;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statistics);
        findViews();
        initial();
        setListView();  
	}
	
	public void findViews() {
		list = (ListView) findViewById(R.id.listView1);
	}
	
	public void initial() {
		Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();	//取得Bundle
        time = bundle.getString("date");	//輸出Bundle內容              
        statistics = new Statistics(this);         
	}
	
	public void setListView() {
		statistics.setType(time);
		statistics.setListItem(pic);
		listItemAdapter = new SimpleAdapter(this, statistics.getListItem(),
		        R.layout.list_parent,
		        new String[] {"main", "sub", "id", "imageView", "money", "date", "type"}, 
		        new int[] {R.id.ItemTitle, R.id.ItemText, R.id.Id,
							R.id.ItemImage, R.id.Money, R.id.date, R.id.type});	
		list.setAdapter(listItemAdapter);
		listItemAdapter.notifyDataSetChanged();
		list.setOnItemClickListener(new OnItemClickListener() {     
	        @Override  
	        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {            
				RelativeLayout lr=(RelativeLayout)arg1;  
				TextView id=(TextView)lr.getChildAt(2); 
				TextView type=(TextView)lr.getChildAt(6);

				if (type.getText().toString().equals("0")) {
					manageExpenseDialog(Integer.valueOf(id.getText().toString()));
				}
				else {
					manageIncomeDialog(Integer.valueOf(id.getText().toString()));
				}
	        }   
	    });  
	}	
		
	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == 0) {	//expense
			return new DatePickerDialog(this,
						expenseDateListener,
						mYear, mMonth, mDay);
		}
		else {	//income
			return new DatePickerDialog(this,
						incomeDateListener,
						mYear, mMonth, mDay);
		}       
	}
	
	private DatePickerDialog.OnDateSetListener incomeDateListener = 
		new DatePickerDialog.OnDateSetListener() {		
        public void onDateSet(DatePicker view, int year, 
        		int monthOfYear, int dayOfMonth) {
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;
            updateIncomeDate();
        }
    };
	
	private DatePickerDialog.OnDateSetListener expenseDateListener = 
		new DatePickerDialog.OnDateSetListener() {		
        public void onDateSet(DatePicker view, int year, 
        		int monthOfYear, int dayOfMonth) {
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;
            updateExpenseDate();
        }
    };
    
    private void updateExpenseDate() {
    	expenseDateEditText.setText( new StringBuilder()                      
			        .append(mYear).append("-")
			        .append(mMonth + 1).append("-")	// Month is 0 based so add 1
			        .append(mDay));
    }
    
    private void updateIncomeDate() {
    	incomeDateEditText.setText( new StringBuilder()                      
			        .append(mYear).append("-")
			        .append(mMonth + 1).append("-")	// Month is 0 based so add 1
			        .append(mDay));
    }
	
	public void manageExpenseDialog(final int id){
		expense = new Expense(this);
		//客製化視窗產生。
        LayoutInflater factory = LayoutInflater.from(this);
        View expenseView = factory.inflate(R.layout.manage_expense, null);
        manageExpenseDialog = new AlertDialog.Builder(this).create();
        
        final EditText moneyEditText = (EditText)expenseView.findViewById(R.id.moneyEditText);
        expenseDateEditText = (EditText)expenseView.findViewById(R.id.dateEditText);
        final EditText memoEditText = (EditText)expenseView.findViewById(R.id.memoEditText);
        Button editButton = (Button)expenseView.findViewById(R.id.nextButton);
        Button removeButton = (Button)expenseView.findViewById(R.id.saveButton);
        Spinner mainSpinner = (Spinner) expenseView.findViewById(R.id.group);
        final Spinner subSpinner = (Spinner) expenseView.findViewById(R.id.child);
       
        moneyEditText.setText(String.valueOf(expense.getCashflow(id).getAmount()));
        expenseDateEditText.setText(expense.getCashflow(id).getDate());
        memoEditText.setText(expense.getCashflow(id).getMemo());
        
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        
        final Category category = new Category(this, 0);	//expense category
        subList = category.getChildren();	//initial sublist
        final List<String> list = new ArrayList<String>();
        for(int i = 0; i < subList.get(0).size(); i++) {
        	list.add(subList.get(0).get(i));
        }
        
        //set adapter
        final ArrayAdapter<String> subAdapter = new ArrayAdapter<String>(this,
        		android.R.layout.simple_spinner_item,
        		list);        
        subAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);    
        subSpinner.setAdapter(subAdapter); 
        
        //sub category event
        subSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
        	public void onItemSelected(AdapterView<?>adapterView, 
        			View v, int position, long id){
        		subSelectString = adapterView.getItemAtPosition(position).toString();        		
        	}
        	public void onNothingSelected(AdapterView<?>adapterView){
        		 
        	} 
        });
        
        int subIntialPos = 0;
        for (int i = 0; i < list.size(); i++) {
        	if (list.get(i).equals(expense.getCashflow(id).getSub())) {
        		subIntialPos = i;
        		break;
        	}
        }
        
        subSpinner.setSelection(subIntialPos);

        //main category adapter
        ArrayAdapter<String> mainAdapter = new ArrayAdapter<String>(this,
        		android.R.layout.simple_spinner_item,
        		category.getGroups());        
        
        //main category event
        mainAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mainSpinner.setAdapter(mainAdapter);        
        mainSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
        	public void onItemSelected(AdapterView<?>adapterView, 
        			View v, int position, long id){
        		mainSelectString = adapterView.getItemAtPosition(position).toString();
        		list.clear();
        		
        		for(int i = 0; i < subList.get(position).size(); i++) {
                	list.add(subList.get(position).get(i));
                }

        		subAdapter.notifyDataSetChanged();
        		subSelectString = subList.get(position).get(0);  
        	}
        	public void onNothingSelected(AdapterView<?>adapterView){
        	
        	} 
        });
        
        int mainIntialPos = 0;
        for (int i = 0; i < category.getGroups().size(); i++) {
        	if (category.getGroups().get(i).equals(expense.getCashflow(id).getMain())) {
        		mainIntialPos = i;
        		break;
        	}
        }
        mainSpinner.setSelection(mainIntialPos);
        
        expenseDateEditText.setOnClickListener(new OnClickListener(){     
        	@Override     
            public void onClick(View v) { 
        		showDialog(DATE_EXPENSE_ID);
        	}
        });
 
        editButton.setOnClickListener(new OnClickListener(){     
        	@Override     
            public void onClick(View v) { 
        		Cashflow expense = new Expense(StatisticsActivity.this, 
        				Integer.valueOf(moneyEditText.getText().toString()),
        				expenseDateEditText.getText().toString(), 
        				memoEditText.getText().toString(), mainSelectString, subSelectString);
        		expense.editCashflow(expense, id);	
        		setListView();
        		manageExpenseDialog.dismiss();
        	}
        });
        
        removeButton.setOnClickListener(new OnClickListener(){     
        	@Override     
            public void onClick(View v) {         		
        		expense.removeCashflow(id);	
        		setListView();
        		manageExpenseDialog.dismiss();
        	}
        });
		//顯示dialog
        manageExpenseDialog.setView(expenseView, 0,0,0,0);
        manageExpenseDialog.show();
	}
	
	public void manageIncomeDialog(final int id){
		income = new Income(this);
		//客製化視窗產生。
        LayoutInflater factory = LayoutInflater.from(this);
        View incomeView = factory.inflate(R.layout.manage_income, null);
        manageIncomeDialog = new AlertDialog.Builder(this).create();
        
        final EditText moneyEditText = (EditText)incomeView.findViewById(R.id.moneyEditText);
        incomeDateEditText = (EditText)incomeView.findViewById(R.id.dateEditText);
        final EditText memoEditText = (EditText)incomeView.findViewById(R.id.memoEditText);
        Button editButton = (Button)incomeView.findViewById(R.id.nextButton);
        Button removeButton = (Button)incomeView.findViewById(R.id.saveButton);
        Spinner mainSpinner = (Spinner) incomeView.findViewById(R.id.group);
        final Spinner subSpinner = (Spinner) incomeView.findViewById(R.id.child);       
  
        moneyEditText.setText(String.valueOf(income.getCashflow(id).getAmount()));       
        incomeDateEditText.setText(income.getCashflow(id).getDate());
        memoEditText.setText(income.getCashflow(id).getMemo());
        
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        
        final Category category = new Category(this, 1);	//income category
        subList = category.getChildren();	//initial sublist
        final List<String> list = new ArrayList<String>();
        for(int i = 0; i < subList.get(0).size(); i++) {
        	list.add(subList.get(0).get(i));
        }

        //set adapter
        final ArrayAdapter<String> subAdapter = new ArrayAdapter<String>(this,
        		android.R.layout.simple_spinner_item,
        		list);        
        subAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);    
        subSpinner.setAdapter(subAdapter); 
        
        //sub category event
        subSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
        	public void onItemSelected(AdapterView<?>adapterView, 
        			View v, int position, long id){
        		subSelectString = adapterView.getItemAtPosition(position).toString();        		
        	}
        	public void onNothingSelected(AdapterView<?>adapterView){
        		 
        	} 
        });
        
        int subIntialPos = 0;
        for (int i = 0; i < list.size(); i++) {
        	if (list.get(i).equals(income.getCashflow(id).getSub())) {
        		subIntialPos = i;
        		break;
        	}
        }
        
        subSpinner.setSelection(subIntialPos);

        //main category adapter
        ArrayAdapter<String> mainAdapter = new ArrayAdapter<String>(this,
        		android.R.layout.simple_spinner_item,
        		category.getGroups());        
        
        //main category event
        mainAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mainSpinner.setAdapter(mainAdapter);        
        mainSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
        	public void onItemSelected(AdapterView<?>adapterView, 
        			View v, int position, long id){
        		mainSelectString = adapterView.getItemAtPosition(position).toString();
        		list.clear();
        		
        		for(int i = 0; i < subList.get(position).size(); i++) {
                	list.add(subList.get(position).get(i));
                }

        		subAdapter.notifyDataSetChanged();
        		subSelectString = subList.get(position).get(0);  
        	}
        	public void onNothingSelected(AdapterView<?>adapterView){
        	
        	} 
        });
        
        int mainIntialPos = 0;
        for (int i = 0; i < category.getGroups().size(); i++) {
        	if (category.getGroups().get(i).equals(income.getCashflow(id).getMain())) {
        		mainIntialPos = i;
        		break;
        	}
        }
        mainSpinner.setSelection(mainIntialPos);
        
        incomeDateEditText.setOnClickListener(new OnClickListener(){     
        	@Override     
            public void onClick(View v) { 
        		showDialog(DATE_INCOME_ID);
        	}
        });
 
        editButton.setOnClickListener(new OnClickListener(){     
        	@Override     
            public void onClick(View v) { 
        		Cashflow income = new Income(StatisticsActivity.this, 
        				Integer.valueOf(moneyEditText.getText().toString()),
        				incomeDateEditText.getText().toString(), 
        				memoEditText.getText().toString(), mainSelectString, subSelectString);
        		income.editCashflow(income, id);	
        		setListView();
        		manageIncomeDialog.dismiss();
        	}
        });
        
        removeButton.setOnClickListener(new OnClickListener(){     
        	@Override     
            public void onClick(View v) {         		
        		income.removeCashflow(id);	
        		setListView();
        		manageIncomeDialog.dismiss();
        	}
        });
		//顯示dialog
        manageIncomeDialog.setView(incomeView, 0,0,0,0);
        manageIncomeDialog.show();
	}
}
