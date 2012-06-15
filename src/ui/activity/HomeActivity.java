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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import domain.cashflow.Cashflow;
import domain.cashflow.Expense;
import domain.cashflow.Income;
import domain.category.Category;
import domain.statistics.Statistics;

/*
 * home of program, access of all functions 
 */

public class HomeActivity extends Activity {
	private TableRow expendButton;
	private TextView incomeButton;
	private TextView chartButton;	
	private LinearLayout daily;
	private LinearLayout weekly;
	private LinearLayout monthly;
	protected AlertDialog expenseDialog;
	protected AlertDialog incomeDialog;
	protected AlertDialog chartDialog;
	private int mYear;
    private int mMonth;
    private int mDay;
    private EditText expenseDateEditText;
    private EditText incomeDateEditText;
    
    private TextView dailyDate;
    private TextView weeklyDate;
    private TextView monthlyDate;
    private TextView dailytotal;
    private TextView weeklyTotal;
    private TextView monthlyTotal;
    
    private final int MENU_BUTTON_1 = Menu.FIRST;
    private final int DATE_EXPENSE_ID = 0;	//expense
    private final int DATE_INCOME_ID = 1;	//income
    private List<List<String>> subList;	//sub category list of spinner
    private String mainSelectString = "";
    private String subSelectString = "";
    private Statistics statistics;
    private final String[] chartType = {"支出分類圓餅圖", "收支長條圖"};
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        statistics = new Statistics(this);
        findViews();
        setViews();        
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	refreshStatistics();
    }
    
    public void findViews() {
    	expendButton = (TableRow) findViewById(R.id.tableRow1);
    	incomeButton = (TextView) findViewById(R.id.textView2);
    	chartButton = (TextView) findViewById(R.id.textView3);
    	daily = (LinearLayout) findViewById(R.id.daily);
    	weekly = (LinearLayout) findViewById(R.id.weekly);
    	monthly = (LinearLayout) findViewById(R.id.monthly);
    	dailyDate = (TextView) findViewById(R.id.dayDate);
        weeklyDate = (TextView) findViewById(R.id.weekDate);
        monthlyDate = (TextView) findViewById(R.id.monthDate);
        dailytotal = (TextView) findViewById(R.id.dayIn);
        weeklyTotal = (TextView) findViewById(R.id.weekIn);
        monthlyTotal = (TextView) findViewById(R.id.monthIn);
    }
    
    public void setViews() {
    	expendButton.setOnClickListener(expenseClick);
    	incomeButton.setOnClickListener(incomeClick);
    	chartButton.setOnClickListener(chartClick);
    	daily.setOnClickListener(dailyClick);
    	weekly.setOnClickListener(weeeklyClick);
    	monthly.setOnClickListener(monthlyClick);
    }
    
    public void refreshStatistics() {
    	dailyDate.setText(statistics.getDailyTime());
    	dailytotal.setText("$" + statistics.getDailyTotal());
    	weeklyDate.setText(statistics.getWeeklyTime());
    	weeklyTotal.setText("$" + statistics.getWeeklyTotal());
    	monthlyDate.setText(statistics.getMonthlyTime());
    	monthlyTotal.setText("$" + statistics.getMonthlyTotal());
    }
    
    private TableRow.OnClickListener expenseClick = new TableRow.OnClickListener() {
    	public void onClick(View v){
    		newExpenseDialog();
    	}
    };
    
    private TextView.OnClickListener incomeClick = new TextView.OnClickListener() {
    	public void onClick(View v){
    		newIncomeDialog();
    	}
    };
    
    private TextView.OnClickListener chartClick = new TextView.OnClickListener() {
    	public void onClick(View v){
    		chartDialog();
    	}
    };
    
    private LinearLayout.OnClickListener dailyClick = new LinearLayout.OnClickListener() {
    	public void onClick(View v){
    		Intent intent = new Intent();
    		intent.setClass(HomeActivity.this, StatisticsActivity.class);
    		//設定傳送參數
    		Bundle bundle = new Bundle();
    		bundle.putString("date", "daily");
    		intent.putExtras(bundle);	//將參數放入intent    		 
    		startActivityForResult(intent, 0);	//呼叫page2並要求回傳值
    	}
    };
    
    private LinearLayout.OnClickListener weeeklyClick = new LinearLayout.OnClickListener() {
    	public void onClick(View v){
    		Intent intent = new Intent();
    		intent.setClass(HomeActivity.this, StatisticsActivity.class);
    		//設定傳送參數
    		Bundle bundle = new Bundle();
    		bundle.putString("date", "weekly");
    		intent.putExtras(bundle);	//將參數放入intent    		 
    		startActivityForResult(intent, 0);	//呼叫page2並要求回傳值
    	}
    };
    
    private LinearLayout.OnClickListener monthlyClick = new LinearLayout.OnClickListener() {
    	public void onClick(View v){
    		Intent intent = new Intent();
    		intent.setClass(HomeActivity.this, StatisticsActivity.class);
    		//設定傳送參數
    		Bundle bundle = new Bundle();
    		bundle.putString("date", "monthly");
    		intent.putExtras(bundle);	//將參數放入intent    		 
    		startActivityForResult(intent, 0);	//呼叫page2並要求回傳值
    	}
    };
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_BUTTON_1, 0, "管理分類")
        .setIcon(android.R.drawable.ic_menu_edit);
        return super.onCreateOptionsMenu(menu);
    }
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
        case MENU_BUTTON_1:    
        	Intent intent = new Intent();
        	intent.setClass(HomeActivity.this, CategoryActivity.class);       	 
        	startActivity(intent);	
            break;            
        default:
        	break;
        }
 
        return super.onOptionsItemSelected(item);
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
     
    public void newExpenseDialog(){
		//客製化視窗產生。
        LayoutInflater factory = LayoutInflater.from(this);
        View expenseView = factory.inflate(R.layout.insert_expense, null);
        expenseDialog = new AlertDialog.Builder(this).create();
    
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        
        final EditText moneyEditText = (EditText)expenseView.findViewById(R.id.moneyEditText);
        expenseDateEditText = (EditText)expenseView.findViewById(R.id.dateEditText);
        final EditText memoEditText = (EditText)expenseView.findViewById(R.id.memoEditText);
        Button nextButton = (Button)expenseView.findViewById(R.id.nextButton);
        Button saveButton = (Button)expenseView.findViewById(R.id.saveButton);
        Spinner mainSpinner = (Spinner) expenseView.findViewById(R.id.group);
        final Spinner subSpinner = (Spinner) expenseView.findViewById(R.id.child);
        
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
        		
        		if (subList.get(position).size() == 0) {
        			list.add(" ");
        		}
        		else {
        			for(int i = 0; i < subList.get(position).size(); i++) {
        				list.add(subList.get(position).get(i));
        			}
        		}

        		subAdapter.notifyDataSetChanged();
//        		subSelectString = subList.get(position).get(0);  
        		subSelectString = list.get(0);  
        	}
        	public void onNothingSelected(AdapterView<?>adapterView){
        	
        	} 
        });

        expenseDateEditText.setText(new StringBuffer()
        							.append(mYear).append("-")
        							.append(mMonth+1).append("-")
        							.append(mDay));
        
        expenseDialog.setView(expenseView, 0,0,0,0);
        
        expenseDateEditText.setOnClickListener(new OnClickListener(){     
        	@Override     
            public void onClick(View v) { 
        		showDialog(DATE_EXPENSE_ID);
        	}
        });
        
        nextButton.setOnClickListener(new OnClickListener(){     
        	@Override     
            public void onClick(View v) { 
        		Cashflow expense = new Expense(HomeActivity.this, 
        				Integer.valueOf(moneyEditText.getText().toString()),
        				expenseDateEditText.getText().toString(), 
        				memoEditText.getText().toString(), mainSelectString, subSelectString);
        		expense.insertCashflow(expense);	//type = 1, means income
        		memoEditText.setText("");
        		moneyEditText.setText("");
        		refreshStatistics();
        	}
        });
        
        saveButton.setOnClickListener(new OnClickListener(){     
        	@Override     
            public void onClick(View v) { 
        		Cashflow expense = new Expense(HomeActivity.this, 
        				Integer.valueOf(moneyEditText.getText().toString()),
        				expenseDateEditText.getText().toString(), 
        				memoEditText.getText().toString(), mainSelectString, subSelectString);
        		expense.insertCashflow(expense);	//type = 1, means income
        		expenseDialog.dismiss();
        		refreshStatistics();
        	}
        });
		//顯示dialog
        expenseDialog.show();
	}
    
    public void newIncomeDialog(){
		//客製化視窗產生。
        LayoutInflater factory = LayoutInflater.from(this);
        View incomeView = factory.inflate(R.layout.insert_income, null);
        incomeDialog = new AlertDialog.Builder(this).create();
      
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        
        final EditText moneyEditText = (EditText)incomeView.findViewById(R.id.moneyEditText);
        incomeDateEditText = (EditText)incomeView.findViewById(R.id.dateEditText);
        final EditText memoEditText = (EditText)incomeView.findViewById(R.id.memoEditText);
        Button nextButton = (Button)incomeView.findViewById(R.id.nextButton);
        Button saveButton = (Button)incomeView.findViewById(R.id.saveButton);
        Spinner mainSpinner = (Spinner) incomeView.findViewById(R.id.group);
        Spinner subSpinner = (Spinner) incomeView.findViewById(R.id.child);
        
        Category category = new Category(this, 1);
        subList = category.getChildren();
        final List<String> list = new ArrayList<String>();	//sub category dataset
        
        for(int i = 0; i < subList.get(0).size(); i++) {	//initial 
        	list.add(subList.get(0).get(i));
        }
        
        //sub category
        final ArrayAdapter<String> subAdapter = new ArrayAdapter<String>(this,
        		android.R.layout.simple_spinner_item,
        		list);        
        subAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);    
        subSpinner.setAdapter(subAdapter);
        subSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
        	public void onItemSelected(AdapterView<?>adapterView, 
        			View v, int position, long id){
        		subSelectString = adapterView.getItemAtPosition(position).toString();
        	}
        	public void onNothingSelected(AdapterView<?>adapterView){
        	
        	} 
        });
        
        //main category
        ArrayAdapter<String> mainAdapter = new ArrayAdapter<String>(this,
        		android.R.layout.simple_spinner_item,
        		category.getGroups());        
        mainAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mainSpinner.setAdapter(mainAdapter);        
        mainSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
        	public void onItemSelected(AdapterView<?>adapterView, 
        			View v, int position, long id){
        		mainSelectString = adapterView.getItemAtPosition(position).toString();
        		list.clear();	//clear data
        		Log.d("zzzzz", "msg");
        		if (subList.get(position).size() == 0) {
        			list.add(" ");
        		}
        		else {
	        		for(int i = 0; i < subList.get(position).size(); i++) {	//add 
	                	list.add(subList.get(position).get(i));
	        		}
        		}
        		Log.d("zzzzz", "msg111");
        		subAdapter.notifyDataSetChanged();

        		subSelectString = list.get(0);         			
        	
        	}
        	public void onNothingSelected(AdapterView<?>adapterView){
        	
        	} 
        });

        incomeDateEditText.setText(new StringBuffer()
        							.append(mYear).append("-")
        							.append(mMonth+1).append("-")
        							.append(mDay));
        
        incomeDialog.setView(incomeView, 0,0,0,0);
        
        incomeDateEditText.setOnClickListener(new OnClickListener(){     
        	@Override     
            public void onClick(View v) { 
        		showDialog(DATE_INCOME_ID);
        	}
        });
        
        nextButton.setOnClickListener(new OnClickListener(){     
        	@Override     
            public void onClick(View v) { 
        		Cashflow income = new Income(HomeActivity.this, 
        				Integer.valueOf(moneyEditText.getText().toString()),
        				incomeDateEditText.getText().toString(), 
        				memoEditText.getText().toString(), mainSelectString, subSelectString);
        		income.insertCashflow(income);	
        		memoEditText.setText("");
        		moneyEditText.setText("");
        		refreshStatistics();
        	}
        });
        
        saveButton.setOnClickListener(new OnClickListener(){     
        	@Override     
            public void onClick(View v) { 
        		Cashflow income = new Income(HomeActivity.this, 
        				Integer.valueOf(moneyEditText.getText().toString()),
        				incomeDateEditText.getText().toString(), 
        				memoEditText.getText().toString(), mainSelectString, subSelectString);
        		income.insertCashflow(income);	
        		incomeDialog.dismiss();
        		refreshStatistics();
        	}
        });
		//顯示dialog
        incomeDialog.show();
	}
    
    public void chartDialog() {
    	LayoutInflater factory = LayoutInflater.from(this);
        View chartView = factory.inflate(R.layout.choose_chart, null);
        ListView chooseList = (ListView)chartView.findViewById(R.id.listView1);
        chooseList.setAdapter(new ArrayAdapter<String>(this, 
        		android.R.layout.simple_expandable_list_item_1,
        		chartType));
        chooseList.setOnItemClickListener(new OnItemClickListener() {     
	        @Override  
	        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {            
				if (arg2 == 0) {
					Intent intent = new Intent();
		    		intent.setClass(HomeActivity.this, PieChartActivity.class);
		    		startActivity(intent);		
				}
				else {
					Intent intent = new Intent();
		    		intent.setClass(HomeActivity.this, BarChartActivity.class);
		    		startActivity(intent);		
				}
				chartDialog.dismiss();
	        }   
	    });  
        chartDialog = new AlertDialog.Builder(this).create();
        chartDialog.setTitle("選擇要查看的圖表");
        chartDialog.setView(chartView, 0,0,0,0);
        chartDialog.show();
    }
}