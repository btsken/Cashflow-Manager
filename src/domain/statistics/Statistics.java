package domain.statistics;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import domain.cashflow.CashflowData;

import services.database.Db;

import android.content.Context;
import android.util.Log;

/*
 * fetch the data form database which date during one day or week or month
 */

public class Statistics {
	private ArrayList<HashMap<String, Object>> listItem;
	private Db db;
	private List<CashflowData> cashflowList;
	private DateFormat dateFormat;
	private Calendar c;
	private List<Integer> list = new ArrayList<Integer>();
	private String dailyTime;
	private String weeklyTime;
	private String monthlyTime;
	private int dailyTotal;
	private int weeklyTotal;
	private int monthlyTotal;
	
	public Statistics(Context context) {
		db = new Db(context);
		db.openDB();
		cashflowList = db.getCashflow();
		dateFormat = new SimpleDateFormat("yyyy-M-d");	
		c = new GregorianCalendar();	
	}
	
	public void setListItem(int pic[]) {	
		listItem = new ArrayList<HashMap<String,Object>>();
		cashflowList = db.getCashflow();
		for (int i = 0; i < list.size(); i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("main", cashflowList.get(list.get(i)).getMain());
			map.put("sub", cashflowList.get(list.get(i)).getSub());
			map.put("id", cashflowList.get(list.get(i)).getId());	
			if (cashflowList.get(list.get(i)).getType() == 0) {
				map.put("money", "- $" + cashflowList.get(list.get(i)).getAmount());	
				map.put("imageView", pic[0]);
			}
			else {
				map.put("money", "$" + cashflowList.get(list.get(i)).getAmount());		
				map.put("imageView", pic[1]);
			}
			map.put("date", cashflowList.get(list.get(i)).getDate());	
			map.put("type", cashflowList.get(list.get(i)).getType());
	
			listItem.add(map); 			
		}
	}

	public void setListItem(ArrayList<HashMap<String, Object>> listItem) {
		this.listItem = listItem;
	}

	public ArrayList<HashMap<String, Object>> getListItem() {
		return listItem;
	}
	
	public List<Integer> countDaily() {	
		cashflowList = db.getCashflow();		
		List<Integer> dialy = new ArrayList<Integer>();
		Date today = new Date();    
		int total = 0;
		for (int i = 0; i < cashflowList.size(); i++) {
			Log.d(dateFormat.format(today).toString(), cashflowList.get(i).getDate());
			if (cashflowList.get(i).getDate().equals(dateFormat.format(today).toString())) {
				dialy.add(i);	
				
				//count total money
				if (cashflowList.get(i).getType() == 0) {
					total -= cashflowList.get(i).getAmount();					
				}
				else {
					total += cashflowList.get(i).getAmount();					
				}
			}
		}
		dailyTime = dateFormat.format(today).toString()+" ~ "+dateFormat.format(today).toString();
		dailyTotal = total;
		
		return dialy;
	}
	
	public List<Integer> countWeekly() {
		cashflowList = db.getCashflow();
		Log.d("cashflowList.size()", String.valueOf(cashflowList.size()));
		String start;
		String end;
		int total = 0;
		Date today = new Date();  
		c.setFirstDayOfWeek(Calendar.SUNDAY);	//set first of week
		c.setTime(today);	//set today
    	c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek()); 	
    	start = dateFormat.format(c.getTime());
    	c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek() + 6); 
    	end = dateFormat.format(c.getTime());
    	
		List<Integer> weekly = new ArrayList<Integer>();
		try {
			Date startDate = dateFormat.parse(start);
			Date endDate = dateFormat.parse(end);
			for (int i = 0; i < cashflowList.size(); i++) {
				if ((dateFormat.parse(cashflowList.get(i).getDate()).after(startDate) &&
					dateFormat.parse(cashflowList.get(i).getDate()).before(endDate)) ||
					dateFormat.parse(cashflowList.get(i).getDate()).equals(startDate) ||
					dateFormat.parse(cashflowList.get(i).getDate()).equals(endDate)) {
					weekly.add(i);
					
					//count total money
					if (cashflowList.get(i).getType() == 0) {
						total -= cashflowList.get(i).getAmount();					
					}
					else {
						total += cashflowList.get(i).getAmount();					
					}
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		weeklyTime = start.toString()+" ~ "+end.toString();
		weeklyTotal = total;
		
		return weekly;
	}

	public List<Integer> countMonthly() {
		cashflowList = db.getCashflow();
		String start;
		String end;
		int total = 0;
		Date today = new Date();  
		c.setTime(today);
    	c.set(Calendar.DAY_OF_MONTH, 1); 
    	start = dateFormat.format(c.getTime());
    	c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH)); 
    	end = dateFormat.format(c.getTime());
    	
		List<Integer> monthly = new ArrayList<Integer>();
		try {
			Date startDate = dateFormat.parse(start);
			Date endDate = dateFormat.parse(end);
			for (int i = 0; i < cashflowList.size(); i++) {
				if ((dateFormat.parse(cashflowList.get(i).getDate()).after(startDate) &&
						dateFormat.parse(cashflowList.get(i).getDate()).before(endDate)) ||
						dateFormat.parse(cashflowList.get(i).getDate()).equals(startDate) ||
						dateFormat.parse(cashflowList.get(i).getDate()).equals(endDate)) {
					monthly.add(i);
					
					//count total money
					if (cashflowList.get(i).getType() == 0) {
						total -= cashflowList.get(i).getAmount();						
					}
					else {
						total += cashflowList.get(i).getAmount();					
					}
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		monthlyTime = start.toString()+" ~ "+end.toString();
		monthlyTotal = total;
		
		return monthly;
	}
	
	public void setType(String type) {
		
		//set listItem type
		if (type.equals("daily")) {
			list = countDaily();
        }
        else if (type.equals("weekly")) {
        	list = countWeekly();
        }
        else {
        	list = countMonthly();
        }
	}
	
	public String getDailyTime() {
		countDaily();
		return dailyTime;
	}
	
	public String getWeeklyTime() {
		countWeekly();
		return weeklyTime;
	}
	
	public String getMonthlyTime() {
		countMonthly();
		return monthlyTime;
	}
	
	public String getDailyTotal() {
		countDaily();
		return String.valueOf(dailyTotal);
	}
	
	public String getWeeklyTotal() {
		countWeekly();
		return String.valueOf(weeklyTotal);
	}
	
	public String getMonthlyTotal() {
		countMonthly();
		return String.valueOf(monthlyTotal);
	}
}
