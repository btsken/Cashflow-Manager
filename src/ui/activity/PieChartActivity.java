package ui.activity;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import domain.chart.PieChart;

public class PieChartActivity extends Activity {
	private TextView title;
	private ImageButton next;
	private ImageButton previous;
	private LinearLayout chartLatout;
	private PieChart chart;
	private GraphicalView mChartView;
	private DefaultRenderer renderer;
	private CategorySeries categorySeries;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pie_chart);
        findViews();
        initial();
        setViews();  
	}
	
	public void findViews() {
		title = (TextView) findViewById(R.id.textView1);
		next = (ImageButton) findViewById(R.id.imageView2);
		previous = (ImageButton) findViewById(R.id.imageView1);		
		chartLatout = (LinearLayout) findViewById(R.id.chart);
	}
	
	public void initial() {
		chart = new PieChart(this);
		refreshChart();
	}
	
	public void setViews() {
		previous.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				chart.previousMonth();
				refreshChart();				
			}
		});
		
		next.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				chart.nextMonth();
				refreshChart();
			}
		});		
	}
	
	public void refreshChart() {
		title.setText(String.valueOf(chart.getmYear()) + ", " + 
				String.valueOf(chart.getmMonth()) + "月");
		chart.setCategorySeries();
		chart.setRenderer();
		categorySeries = chart.getCategorySeries();	
		renderer = chart.getRenderer();

		mChartView = ChartFactory.getPieChartView(this, categorySeries, renderer);	
		chartLatout.removeAllViews();
		chartLatout.addView(mChartView, new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));	
		
		if (!chart.getHasData()) {
			Toast.makeText(PieChartActivity.this, "本月份無資料",
					Toast.LENGTH_LONG).show();	
		}
	}
}
