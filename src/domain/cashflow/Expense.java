package domain.cashflow;

import services.database.Db;
import android.content.Context;


public class Expense extends Cashflow{
	private Db db;
	private Context context;
	
	public Expense(Context c, int amount, String date, String memo, String main, String sub) {
		this.amount = amount;
		this.date = date;
		this.memo = memo;
		this.main = main;
		this.sub = sub;
		this.context = c;
		db = new Db(context);
		db.openDB();
	}
	
	public Expense(Context c) {
		this.context = c;
		db = new Db(context);
		db.openDB();
	}

	@Override
	public void insertCashflow(Cashflow cashflow) {
		db.insertExpense(amount, date, main, sub, memo);
	}

	@Override
	public void editCashflow(Cashflow cashflow, int id) {
		db.updateExpense(id, amount, date, main, sub, memo);
	}

	@Override
	public void removeCashflow(int id) {
		db.removeExpense(id);
	}

	@Override
	public CashflowData getCashflow(int id) {
		return db.getExpense(id);		
	}
}
