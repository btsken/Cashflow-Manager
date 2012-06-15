package ui.activity;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import domain.chart.BarChart;

public class BarChartActivity extends Activity {
	private XYMultipleSeriesRenderer renderer;
	private XYSeriesRenderer xyRenderer;
	private BarChart barChart;
	private TextView title;
	private LinearLayout barchart;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bar_chart);
		barChart = new BarChart(this);
        barChart.setDataset();
        setRenderer();

        int length = renderer.getSeriesRendererCount(); 
        for (int i = 0; i < length; i++) {
            SimpleSeriesRenderer ssr = renderer.getSeriesRendererAt(i);            
            ssr.setChartValuesTextAlign(Align.RIGHT);
            ssr.setChartValuesTextSize(12);
            ssr.setDisplayChartValues(true);
        }         
        
        setView();
    }
	
	public void setView() {
		title = (TextView) findViewById(R.id.textView1);
		barchart = (LinearLayout) findViewById(R.id.chart);
        GraphicalView mChartView = ChartFactory.getBarChartView(this, 
        		barChart.getDataset(), renderer, Type.DEFAULT); 
        barchart.addView(mChartView, new LayoutParams(LayoutParams.FILL_PARENT,
                LayoutParams.FILL_PARENT));
        title.setText(String.valueOf(barChart.getYear()) + "收支長條圖");
	}
	
	public void setRenderer() {
		renderer = new XYMultipleSeriesRenderer();
		xyRenderer = new XYSeriesRenderer();
		xyRenderer.setColor(0xff79db46);

        renderer.addSeriesRenderer(xyRenderer);        
        xyRenderer = new XYSeriesRenderer();
        xyRenderer.setColor(0xffff8463);
        renderer.addSeriesRenderer(xyRenderer);
     
        //顺序是:minX, maxX, minY, maxY
        double[] range = { 0, 12, 1, barChart.getMax()+500};
        renderer.setRange(range);
       
        // 设置合适的刻度,在轴上显示的数量是 MAX / labels
        renderer.setXLabels(12);
        renderer.setYLabels(10);
 
        // 设置x,y轴显示的排列,默认是 Align.CENTER
        renderer.setXLabelsAlign(Align.CENTER);
        renderer.setYLabelsAlign(Align.RIGHT);
 
        // 设置坐标轴,轴的颜色
        renderer.setAxesColor(Color.RED);
        // 显示网格
        renderer.setShowGrid(true);
        // 设置x,y轴上的刻度的颜色
        renderer.setLabelsColor(Color.BLACK);
 
        // 设置页边空白的颜色
        renderer.setMarginsColor(0xffe7ffc5);
        // 设置是否显示,坐标轴的轴,默认为 true
        renderer.setShowAxes(true);
 
        // 设置条形图之间的距离
        renderer.setBarSpacing(0.5);
	}
}
