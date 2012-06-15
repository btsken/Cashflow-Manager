package domain.chart;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import services.database.Db;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import domain.cashflow.CashflowData;

public class PieChart {
	private Db db;
	private List<CashflowData> cashflowList;
	private int[] colors = new int[] { 0xff00cd73, 0xff008148, 0xff2d9668, 
									   0xff3ecd8e, 0xff004e2c};
	private DefaultRenderer renderer;
	private CategorySeries categorySeries;
	private boolean hasData;
	private int mYear;
	private int mMonth;	
	
	public DefaultRenderer getRenderer() {
		return renderer;
	}
	
	public boolean getHasData() {
		return hasData;
	}
	
	public void setRenderer() {
		renderer.setChartTitleTextSize(18);
		renderer.setLabelsTextSize(20);
		renderer.setLabelsColor(Color.BLACK);
		renderer.setShowLegend(false);
		renderer.setChartTitle("支出分類圓餅圖");
	}

	public CategorySeries getCategorySeries() {
		return categorySeries;
	}

	public void setCategorySeries() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-M-d");	
		cashflowList = db.getCashflow();
		List<String> category = new ArrayList<String>();
		categorySeries = new CategorySeries("支出分類圓餅圖");		
		
		try {
			Date startDate = dateFormat.parse(
					getFirstDayOfMonth(mYear, mMonth-1));
			Date endDate = dateFormat.parse(
					getLastDayOfMonth(mYear, mMonth-1));
			Log.d(String.valueOf(startDate), String.valueOf(endDate));
			for (int i = 0; i < cashflowList.size(); i++) {
				if (((dateFormat.parse(cashflowList.get(i).getDate()).after(startDate) &&
					dateFormat.parse(cashflowList.get(i).getDate()).before(endDate)) ||
					dateFormat.parse(cashflowList.get(i).getDate()).equals(startDate) ||
					dateFormat.parse(cashflowList.get(i).getDate()).equals(endDate)) &&
					cashflowList.get(i).getType() == 0 &&
					!category.contains(cashflowList.get(i).getMain())) {
					category.add(cashflowList.get(i).getMain());					
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

		int[] castegoryColor = new int[category.size()];
		
		for (int i = 0; i < category.size(); i++) {
			castegoryColor[i] = colors[i%5];
			cashflowList = db.getExpenseCategoryAmount(category.get(i));
			int total = 0;	
			for (int j = 0; j < cashflowList.size(); j++) {					
				total += cashflowList.get(j).getAmount();					
			}
			categorySeries.add(category.get(i), total);
		}
		
		renderer = buildCategoryRenderer(castegoryColor);

		if (category.size() == 0) {
			hasData = false;
		}
		else {
			hasData = true;
		}
	}

	public PieChart(Context context) {
		db = new Db(context);
		db.openDB();
		hasData = false;
		final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH)+1;
	}

	protected DefaultRenderer buildCategoryRenderer(int[] colors) {
		DefaultRenderer renderer = new DefaultRenderer();
		for (int color : colors) {
			SimpleSeriesRenderer r = new SimpleSeriesRenderer();
			r.setColor(color);
			renderer.addSeriesRenderer(r);
		}
		return renderer;
	}
	
	public String getLastDayOfMonth(int year, int month) {   
		Calendar cal = Calendar.getInstance();   
        cal.set(Calendar.YEAR, year);   
        cal.set(Calendar.MONTH, month);   
        cal.set(Calendar.DAY_OF_MONTH,cal.getActualMaximum(Calendar.DATE)); 
        return new SimpleDateFormat("yyyy-M-d ").format(cal.getTime());
    } 
	
	public String getFirstDayOfMonth(int year, int month) {   
        Calendar cal = Calendar.getInstance();   
        cal.set(Calendar.YEAR, year);   
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH,cal.getMinimum(Calendar.DATE));
        return new SimpleDateFormat("yyyy-M-d ").format(cal.getTime());
    } 	
	
	public void nextMonth() {		
		if (mMonth == 12) {
			mMonth = 1;
			mYear++;
		}
		else {
			mMonth++;
		}	
	}
	
	public int getmYear() {
		return mYear;
	}

	public void setmYear(int mYear) {
		this.mYear = mYear;
	}

	public int getmMonth() {
		return mMonth;
	}

	public void setmMonth(int mMonth) {
		this.mMonth = mMonth;
	}

	public void previousMonth() {
		if (mMonth == 1) {
			mMonth = 12;
			mYear--;
		}
		else {
			mMonth--;
		}	
	}
}
