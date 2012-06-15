package services.database;

import java.util.ArrayList;
import java.util.List;


import domain.cashflow.CashflowData;
import domain.category.CategoryData;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Db extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "cashflowManager.db";	//資料庫名稱
	private static final int DATABASE_VERSION = 1;	//資料庫版
	
	//income table
	private static final String INCOME_TABLE_NAME = "income";
    private static final String INCOME_COLUMN_1_NAME = "money"; //金額
    private static final String INCOME_COLUMN_2_NAME = "date"; //日期
    private static final String INCOME_COLUMN_3_NAME = "main"; //主分類
    private static final String INCOME_COLUMN_4_NAME = "sub"; //次分類
    private static final String INCOME_COLUMN_5_NAME = "memo";  //備註
	private static final String INCOME_CREATE_SCRIPT = 	//建table
		"create table " + INCOME_TABLE_NAME +
	    " (ID integer primary key autoincrement, "+
	    INCOME_COLUMN_1_NAME+" integer not null, "+
	    INCOME_COLUMN_2_NAME+" text, "+
	    INCOME_COLUMN_3_NAME+" text, "+
	    INCOME_COLUMN_4_NAME+" text, "+
	    INCOME_COLUMN_5_NAME+" text);";
	
	//expense table
	private static final String EXPENSE_TABLE_NAME = "expense";
    private static final String EXPENSE_COLUMN_1_NAME = "money"; //金額
    private static final String EXPENSE_COLUMN_2_NAME = "date"; //日期
    private static final String EXPENSE_COLUMN_3_NAME = "main"; //主分類
    private static final String EXPENSE_COLUMN_4_NAME = "sub"; //次分類
    private static final String EXPENSE_COLUMN_5_NAME = "memo";  //備註
	private static final String EXPENSE_CREATE_SCRIPT = 	//建table
		"create table " + EXPENSE_TABLE_NAME +
	    " (ID integer primary key autoincrement, "+
	    EXPENSE_COLUMN_1_NAME+" integer not null, "+
	    EXPENSE_COLUMN_2_NAME+" text, "+
	    EXPENSE_COLUMN_3_NAME+" text, "+
	    EXPENSE_COLUMN_4_NAME+" text, "+
	    EXPENSE_COLUMN_5_NAME+" text);";
	
	//category table
	private static final String CATEGORY_TABLE_NAME = "category";
    private static final String CATEGORY_COLUMN_1_NAME = "name"; //名稱
    private static final String CATEGORY_COLUMN_2_NAME = "type"; //種類(0:main, 1:sub)
    private static final String CATEGORY_COLUMN_3_NAME = "parent";	//child belong parent
    
    //income or expense(0:expense, 1:income)
    private static final String CATEGORY_COLUMN_4_NAME = "cftype";	
	private static final String CATEGORY_CREATE_SCRIPT = 	//建table
		"create table " + CATEGORY_TABLE_NAME +
	    " (ID integer primary key autoincrement, "+
	    CATEGORY_COLUMN_1_NAME+" text, "+
	    CATEGORY_COLUMN_2_NAME+" integer not null, "+
	    CATEGORY_COLUMN_3_NAME+" integer not null, "+
	    CATEGORY_COLUMN_4_NAME+" integer not null);";
	
	private SQLiteDatabase db;
 
	public Db(Context context) {	//建構子
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
 
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(INCOME_CREATE_SCRIPT);
		db.execSQL(EXPENSE_CREATE_SCRIPT);
		db.execSQL(CATEGORY_CREATE_SCRIPT);
	}
 
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//oldVersion=舊的資料庫版本；newVersion=新的資料庫版本
		db.execSQL("DROP TABLE IF EXISTS " + INCOME_TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + EXPENSE_TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + CATEGORY_TABLE_NAME);
		onCreate(db);
	}
	
	public void openDB() throws SQLException{
    	//Checking instance
        if(this.db == null){
            //Creating instance
            this.db = this.getWritableDatabase();
        }
    }
 
    //Close Database
    public void closeDB(){
        if(this.db != null){
            if(this.db.isOpen())
                this.db.close();
        }
    }
    
    /***************************
     ***********Income***********
     ***************************/
    
    //新增Income
    public void insertIncome(int money, String date, String main, String sub, String memo){
    	this.db.execSQL(
    			"insert into income(money, date, main, sub, memo) " +
    			"values('"+ money +"','"+ date +"','"+ main +"','"+ sub +"','"+ memo +"');");
    }
    
    //修改Income
    public void updateIncome(int id, int money, String date, String main, String sub, String memo){
    	this.db.execSQL(
    			"update income set money = '" + money + "' , date = '" + date + 
    			"' , main = '" + main + "', sub = '" + sub + "', memo = '" + memo + 
    			"' WHERE ID = '" + id +"' ;");
    }
    
    //刪除一筆Income
    public void removeIncome(int id){
    	this.db.execSQL("delete from income where ID = '" + id+"';");
    }    
    
    public CashflowData getIncome(int id) {
    	Cursor incomeCursor = this.db.rawQuery(
    			"select * from income where ID = '" + id+"';", null);
    	CashflowData income = new CashflowData();
    	int size = incomeCursor.getCount();
    	incomeCursor.moveToFirst();    	
    	for (int i = 0; i < size; i++) {
    		income.setAmount(incomeCursor.getInt(incomeCursor.getColumnIndexOrThrow("money")));
    		income.setDate(incomeCursor.getString(incomeCursor.getColumnIndexOrThrow("date")));
    		income.setMemo(incomeCursor.getString(incomeCursor.getColumnIndexOrThrow("memo")));
    		income.setId(incomeCursor.getInt(incomeCursor.getColumnIndexOrThrow("ID")));
    		income.setMain(incomeCursor.getString(incomeCursor.getColumnIndexOrThrow("main")));
    		income.setSub(incomeCursor.getString(incomeCursor.getColumnIndexOrThrow("sub")));
    		income.setType(0);
    		incomeCursor.moveToNext();
    	}
    	incomeCursor.close();
    	
    	return income;
    }
    
    /***************************
     ***********Expense***********
     ***************************/
    
    //新增Expense
    public void insertExpense(int money, String date, String main, String sub, String memo) {
    	this.db.execSQL(
    			"insert into expense(money, date, main, sub, memo) " +
    			"values('"+ money +"','"+ date +"','"+ main +"','"+ sub +"','"+ memo +"');");
    }
    
    //修改Expense
    public void updateExpense(int id, int money, String date, String main, String sub, String memo) {
    	this.db.execSQL(
    			"update expense set money = '" + money + "' , date = '" + date + 
    			"' , main = '" + main + "', sub = '" + sub + "', memo = '" + memo + 
    			"' WHERE ID = '" + id +"' ;");
    }
    
    //刪除一筆Expense
    public void removeExpense(int id) {
    	this.db.execSQL("delete from expense where ID = '" + id+"';");
    }   
    
    //get all cashflow(expense and income)
    public List<CashflowData> getCashflow() {
    	List<CashflowData> cashflwList = new ArrayList<CashflowData>();
    	int size = 0;
    	//expense
    	Cursor expenseCursor = this.db.rawQuery("select * from expense;", null); 
    	size = expenseCursor.getCount();
    	expenseCursor.moveToFirst();    	
    	for (int i = 0; i < size; i++) {
    		CashflowData cashflow = new CashflowData();
    		cashflow.setAmount(expenseCursor.getInt(expenseCursor.getColumnIndexOrThrow("money")));
    		cashflow.setDate(expenseCursor.getString(expenseCursor.getColumnIndexOrThrow("date")));
    		cashflow.setMemo(expenseCursor.getString(expenseCursor.getColumnIndexOrThrow("memo")));
    		cashflow.setId(expenseCursor.getInt(expenseCursor.getColumnIndexOrThrow("ID")));
    		cashflow.setMain(expenseCursor.getString(expenseCursor.getColumnIndexOrThrow("main")));
    		cashflow.setSub(expenseCursor.getString(expenseCursor.getColumnIndexOrThrow("sub")));
    		cashflow.setType(0);
    		cashflwList.add(cashflow);
    		expenseCursor.moveToNext();
    	}
    	expenseCursor.close();
    	
    	//income
    	Cursor incomeCursor = this.db.rawQuery("select * from income;", null);
    	size = incomeCursor.getCount();
    	incomeCursor.moveToFirst();    	
    	for (int i = 0; i < size; i++) {
    		CashflowData cashflow = new CashflowData();
    		cashflow.setAmount(incomeCursor.getInt(incomeCursor.getColumnIndexOrThrow("money")));
    		cashflow.setDate(incomeCursor.getString(incomeCursor.getColumnIndexOrThrow("date")));
    		cashflow.setMemo(incomeCursor.getString(incomeCursor.getColumnIndexOrThrow("memo")));
    		cashflow.setId(incomeCursor.getInt(incomeCursor.getColumnIndexOrThrow("ID")));
    		cashflow.setMain(incomeCursor.getString(incomeCursor.getColumnIndexOrThrow("main")));
    		cashflow.setSub(incomeCursor.getString(incomeCursor.getColumnIndexOrThrow("sub")));
    		cashflow.setType(1);
    		cashflwList.add(cashflow);
    		incomeCursor.moveToNext();
    	}
    	incomeCursor.close();
    	
    	return cashflwList;
    }
    
    public CashflowData getExpense(int id) {    	
    	Cursor expenseCursor = this.db.rawQuery(
    			"select * from expense where ID = '" + id+"';", null);
    	CashflowData expense = new CashflowData();
    	int size = expenseCursor.getCount();
    	expenseCursor.moveToFirst();    	
    	for (int i = 0; i < size; i++) {
    		expense.setAmount(expenseCursor.getInt(expenseCursor.getColumnIndexOrThrow("money")));
    		expense.setDate(expenseCursor.getString(expenseCursor.getColumnIndexOrThrow("date")));
    		expense.setMemo(expenseCursor.getString(expenseCursor.getColumnIndexOrThrow("memo")));
    		expense.setId(expenseCursor.getInt(expenseCursor.getColumnIndexOrThrow("ID")));
    		expense.setMain(expenseCursor.getString(expenseCursor.getColumnIndexOrThrow("main")));
    		expense.setSub(expenseCursor.getString(expenseCursor.getColumnIndexOrThrow("sub")));
    		expense.setType(0);
    		expenseCursor.moveToNext();
    	}
    	expenseCursor.close();
    	
    	return expense;
    }
    
    public List<CashflowData> getExpenseCategoryAmount(String main) {
    	List<CashflowData> cashflwList = new ArrayList<CashflowData>();
    	Cursor amountCursor = this.db.rawQuery(
    			"select * from expense where main = '" + main + "';",
    			null);
    	int size = amountCursor.getCount();
    	amountCursor.moveToFirst();    	
    	for (int i = 0; i < size; i++) {
    		CashflowData cashflow = new CashflowData();
    		cashflow.setAmount(amountCursor.getInt(amountCursor.getColumnIndexOrThrow("money")));
    		cashflow.setDate(amountCursor.getString(amountCursor.getColumnIndexOrThrow("date")));
    		cashflow.setMemo(amountCursor.getString(amountCursor.getColumnIndexOrThrow("memo")));
    		cashflow.setId(amountCursor.getInt(amountCursor.getColumnIndexOrThrow("ID")));
    		cashflow.setMain(amountCursor.getString(amountCursor.getColumnIndexOrThrow("main")));
    		cashflow.setSub(amountCursor.getString(amountCursor.getColumnIndexOrThrow("sub")));
    		cashflow.setType(1);
    		cashflwList.add(cashflow);
    		amountCursor.moveToNext();
    	}
    	amountCursor.close();
    	return cashflwList;
    }
    
    /***************************
     ***********Category***********
     ***************************/
    
    //新增category
    public void insertCategory(String name, int type, int parent, int cashflowType) {
    	this.db.execSQL(
    			"insert into category(name, type, parent, cftype) " +
    			"values('" + name +"','" + type +"','" + parent +"','" + cashflowType + "');");
    }
    
    //刪除category
    public void removeCategory(int cashflowType) {
    	this.db.execSQL("delete from category where cftype = '"+ cashflowType+"';");
    }
    
    public List<CategoryData> getCategory() {
    	Cursor categoryCursor = this.db.rawQuery("SELECT * FROM category ;" , null);
    	List<CategoryData> categoryList = new ArrayList<CategoryData>();
    	
    	categoryCursor.moveToFirst();
    	for( int i = 0 ; i < categoryCursor.getCount() ; i++){    		
    		CategoryData category = new CategoryData();
    		category.setName(categoryCursor.getString(categoryCursor.getColumnIndex("name")));
    		category.setType(categoryCursor.getInt(categoryCursor.getColumnIndex("type")));
    		category.setParent(categoryCursor.getInt(categoryCursor.getColumnIndex("parent")));
    		category.setCashflowType(categoryCursor.getInt(categoryCursor.getColumnIndex("cftype")));
    		categoryList.add(category);
    		categoryCursor.moveToNext();
    	}
    	categoryCursor.close();
    	
		return categoryList;    	
    }
}
