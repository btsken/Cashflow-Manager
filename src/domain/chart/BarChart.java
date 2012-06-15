package domain.chart;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;

import services.database.Db;
import android.content.Context;
import domain.cashflow.CashflowData;

public class BarChart {
	private XYMultipleSeriesDataset dataset;
	private Db db;
	private List<Integer> expenseList;	//每個月的支出總合
	private List<Integer> incomeList;	//每個月的收入總合
	private int year;	
	
	public BarChart(Context context) {
		dataset = new XYMultipleSeriesDataset();
		db = new Db(context);
		db.openDB();
		final Calendar c = Calendar.getInstance();
		year = c.get(Calendar.YEAR);
	}
	
	public void setDataset() {
		CategorySeries expenseSeries = new CategorySeries("支出");
		CategorySeries incomeSeries = new CategorySeries("收入");
		
		expenseList = countTotal(0);
		incomeList = countTotal(1);
		
		for (int i = 0; i < expenseList.size(); i++) {
			expenseSeries.add(expenseList.get(i));
		}
		for (int i = 0; i < incomeList.size(); i++) {
			incomeSeries.add(incomeList.get(i));
		}
		dataset.addSeries(expenseSeries.toXYSeries());
		dataset.addSeries(incomeSeries.toXYSeries());
	}	
		
	public List<Integer> getExpenseList() {
		return expenseList;
	}

	public void setExpenseList(List<Integer> expenseList) {
		this.expenseList = expenseList;
	}

	public List<Integer> getIncomeList() {
		return incomeList;
	}

	public void setIncomeList(List<Integer> incomeList) {
		this.incomeList = incomeList;
	}

	public XYMultipleSeriesDataset getDataset() {
		return dataset;
	}

	public void setDataset(XYMultipleSeriesDataset dataset) {
		this.dataset = dataset;
	}

	public List<Integer> countTotal(int type) {
		List<CashflowData> cashflow = db.getCashflow();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-M-d");	
		List<Integer> list = new ArrayList<Integer>();		
		
		for (int i = 0; i < 12; i++) {
			list.add(0);
		}
		
		for (int i = 0; i < cashflow.size(); i++) {
			if (cashflow.get(i).getType() == type) {	//type 0 is expense, 1 is income
				try {
					for (int j = 0; j < 12; j++) {
						int total = list.get(j);
						if (dateFormat.parse(cashflow.get(i).getDate()).getMonth() == j &&
							dateFormat.parse(cashflow.get(i).getDate()).getYear() + 1900 == year) {	//if j month
							total += cashflow.get(i).getAmount();	//total of j month
							list.set(j, total);
						}
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}			
		}
		return list;
	}
	
	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMax() {
		List<Integer> list1 = expenseList;
		List<Integer> list2 = incomeList;
		Collections.sort(list1);
		Collections.sort(list2);
		if (list1.get(list1.size() - 1) > list2.get(list2.size() - 1)) {
			return list1.get(list1.size() - 1);
		}
		else {
			return list2.get(list2.size() - 1);
		}
	}
}
