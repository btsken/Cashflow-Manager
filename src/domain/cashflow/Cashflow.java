package domain.cashflow;

/*
 * cashflow data and abstract function
 */

public abstract class Cashflow {
	protected String date;
	protected int amount;
	protected String memo;
	protected String main;
	protected String sub;
	
	public abstract void insertCashflow(Cashflow cashflow);
	public abstract void editCashflow(Cashflow cashflow, int id);
	public abstract void removeCashflow(int id);
	public abstract CashflowData getCashflow(int id);
}
