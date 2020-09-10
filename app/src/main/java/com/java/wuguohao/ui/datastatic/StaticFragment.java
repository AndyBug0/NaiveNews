package com.java.wuguohao.ui.datastatic;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.java.wuguohao.DataHandler;
import com.java.wuguohao.R;
import com.java.wuguohao.bean.NewsData;
import com.java.wuguohao.news.NewsPageActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class StaticFragment extends Fragment {
    private View root;
    private Button button1;     //国内情况
    private Button button2;     //世界情况
    private TextView showPlace; //显示地区所在
    private LineChart lineChart;

    private ListView listView;
    private ArrayAdapter<String> adapter1;
    private ArrayAdapter<String> adapter2;
    private List<String> placeList1 = new ArrayList<>();
    private List<String> placeList2 = new ArrayList<>();
    private String place;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_static, container, false);
        lineChart = (LineChart) root.findViewById(R.id.lineChart);
        showPlace = (TextView) root.findViewById(R.id.static_place);
        place = "China|Beijing";    //初始值

        initPopListView();
        initButtonClicker();

        drawLineChart("China|Beijing", "中国 北京", lineChart);

        return root;
    }

    private void initPopListView() {
        listView = root.findViewById(R.id.static_pop_list);
        List<String> list1 = new ArrayList<>();
        List<String> list2 = new ArrayList<>();
        String [] array1 = getResources().getStringArray(R.array.province_in_China);
        String [] array2 = getResources().getStringArray(R.array.country_in_world);
        for (String str : array1) {
            String [] text = str.split("@");
            list1.add(text[0]);
            placeList1.add(text[1]);
        }
        for (String str : array2) {
            String [] text = str.split("@");
            list2.add(text[0]);
            placeList2.add(text[1]);
        }
        adapter1 = new ArrayAdapter<>(this.getContext(), android.R.layout.simple_list_item_1, list1);
        adapter2 = new ArrayAdapter<>(this.getContext(), android.R.layout.simple_list_item_1, list2);
        listView.setVisibility(View.GONE);
        listView.setClickable(false);
    }

    private void popList(int btn_id) {
        if (btn_id == 1) {
            listView.setAdapter(adapter1);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                    String selectedPlace = placeList1.get(i);
                    place = "China|" + selectedPlace;
                    hideList();
                    button1.setSelected(false);
                    button1.setTag(false);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            drawLineChart(place, "中国 " + adapter1.getItem(i), lineChart);
                        }
                    }, 50);

                }
            });
        } else if (btn_id == 2) {
            listView.setAdapter(adapter2);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                    place = placeList2.get(i);
                    hideList();
                    button2.setSelected(false);
                    button2.setTag(false);
                    new Handler().postDelayed((new Runnable() {
                        @Override
                        public void run() {
                            drawLineChart(place, adapter2.getItem(i), lineChart);
                        }
                    }), 50);
                }
            });
        }
        listView.setClickable(true);
        listView.setVisibility(View.VISIBLE);
    }

    private void hideList() {
        listView.setClickable(false);
        listView.setVisibility(View.GONE);
    }

    private void initButtonClicker() {
        button1 = (Button) root.findViewById(R.id.static_button_inChina);
        button2 = (Button) root.findViewById(R.id.static_button_other_country);
        button1.setTag(false);   //表示被按之前是否被选中
        button2.setTag(false);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isSelected = !((Boolean) button1.getTag()); //表示被按之后是否被选中
                button1.setTag(isSelected);
                button2.setTag(false);
                button2.setSelected(false);
                button2.setPressed(false);

                if (isSelected) {
                    button1.setSelected(true);
                    popList(1);
                } else {
                    button1.setSelected(false);
                    hideList();
                }
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isSelected = !((Boolean) button2.getTag());
                button2.setTag(isSelected);
                button1.setTag(false);
                button1.setSelected(false);
                button1.setSelected(false);

                if (isSelected) {
                    button2.setSelected(true);
                    popList(2);
                } else {
                    button2.setSelected(false);
                    hideList();
                }
            }
        });
    }

    private void drawLineChart(final String place, final String place_to_show, final LineChart lineChart) {
        root.findViewById(R.id.loading_info).setVisibility(View.VISIBLE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                List<NewsData> newsDataList = NewsData.find(NewsData.class, "place = ?", place);

                int connectTryCount = 0;
                while (newsDataList.isEmpty() && connectTryCount < 3) {
                    connectTryCount ++;
                    new DataHandler().readData(place, getActivity());
                    newsDataList = NewsData.find(NewsData.class, "place = ?", place);
                }
                if (newsDataList.isEmpty()) {   //找不到数据
                    Toast.makeText(StaticFragment.this.getContext(), "FAILED：加载失败", Toast.LENGTH_SHORT).show();
                    return;
                }
                root.findViewById(R.id.loading_info).setVisibility(View.GONE);
                showPlace.setText(place_to_show);
                NewsData newsData = newsDataList.get(0);
                List<Integer> confirmed;
                List<Integer> cured;
                List<Integer> dead;
                String begin = newsData.getBegin();
                confirmed = newsData.getConfirmed();
                cured = newsData.getCured();
                dead  = newsData.getDead();
                initLineChart(place, confirmed, cured, dead, lineChart);
            }
        }, 50);
    }

    /**
     * 初始化曲线图表
     *
     *  数据集
     */
    private void initLineChart(final String place, final List<Integer> confirmed, final List<Integer> cured, final List<Integer> dead, LineChart lineChart)
    {
        //显示边界
        lineChart.setDrawBorders(false);
        //设置数据
        List<Entry> entries_confirmed = new ArrayList<>();
        List<Entry> entries_cured = new ArrayList<>();
        List<Entry> entries_dead = new ArrayList<>();
        for (int i = 0; i < confirmed.size(); i++)
        {
            entries_confirmed.add(new Entry(i, confirmed.get(i)));
            entries_cured.add(new Entry(i, cured.get(i)));
            entries_dead.add(new Entry(i, dead.get(i)));
        }
        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        //一个LineDataSet就是一条线
        LineDataSet lineDataSet_confirmed = new LineDataSet(entries_confirmed, "confirmed");
        LineDataSet lineDataSet_cured = new LineDataSet(entries_cured, "cured");
        LineDataSet lineDataSet_dead = new LineDataSet(entries_dead, "dead");
        //线颜色
        lineDataSet_confirmed.setColor(Color.parseColor("#F15A4A"));
        lineDataSet_cured.setColor(Color.parseColor("#00FFFF"));
        lineDataSet_dead.setColor(Color.parseColor("#00FF33"));
        //线宽度
        lineDataSet_confirmed.setLineWidth(1.6f);
        lineDataSet_cured.setLineWidth(1.6f);
        lineDataSet_dead.setLineWidth(1.6f);
        //不显示圆点
        lineDataSet_confirmed.setDrawCircles(false);
        lineDataSet_cured.setDrawCircles(false);
        lineDataSet_dead.setDrawCircles(false);
        //线条平滑
        lineDataSet_confirmed.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        lineDataSet_cured.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        lineDataSet_dead.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);

        dataSets.add(lineDataSet_confirmed);
        dataSets.add(lineDataSet_cured);
        dataSets.add(lineDataSet_dead);
        //设置折线图填充
//        lineDataSet.setDrawFilled(true);
        LineData data = new LineData(dataSets);

        //无数据时显示的文字
        lineChart.setNoDataText("暂无数据");
        //折线图不显示数值
        data.setDrawValues(false);
        //得到X轴
        XAxis xAxis = lineChart.getXAxis();
        //设置X轴的位置（默认在上方)
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //设置X轴坐标之间的最小间隔
        xAxis.setGranularity(1f);
        //设置X轴的刻度数量，第二个参数为true,将会画出明确数量（带有小数点），但是可能值导致不均匀，默认（6，false）
        xAxis.setLabelCount(confirmed.size() / 6, false);
        //设置X轴的值（最小值、最大值、然后会根据设置的刻度数量自动分配刻度显示）
        xAxis.setAxisMinimum(0f);
        xAxis.setAxisMaximum((float) confirmed.size());
        //不显示网格线
        xAxis.setDrawGridLines(false);
        // 标签倾斜
        xAxis.setLabelRotationAngle(45);
        //设置X轴值为字符串
//        xAxis.setValueFormatter(new IAxisValueFormatter()
//        {
//            @Override
//            public String getFormattedValue(float value, AxisBase axis)
//            {
//                int IValue = (int) value;
//                CharSequence format = DateFormat.format("MM/dd",
//                        System.currentTimeMillis()-(long)(list.size()-IValue)*24*60*60*1000);
//                return format.toString();
//            }
//        });
        //得到Y轴
        YAxis yAxis = lineChart.getAxisLeft();
        YAxis rightYAxis = lineChart.getAxisRight();
        //设置Y轴是否显示
        rightYAxis.setEnabled(false); //右侧Y轴不显示
        //设置y轴坐标之间的最小间隔
        //不显示网格线
        yAxis.setDrawGridLines(false);
        //设置Y轴坐标之间的最小间隔
        yAxis.setGranularity(1);
        //设置y轴的刻度数量
        //+2：最大值n就有n+1个刻度，在加上y轴多一个单位长度，为了好看，so+2
        System.out.println("confirmed.size = " + confirmed.size());
        yAxis.setLabelCount(Collections.max(confirmed) + 2, false);
        //设置从Y轴值
        yAxis.setAxisMinimum(0f);
        //+1:y轴多一个单位长度，为了好看
        yAxis.setAxisMaximum(Collections.max(confirmed) + 1);

        //y轴
//        yAxis.setValueFormatter(new IAxisValueFormatter()
//        {
//            @Override
//            public String getFormattedValue(float value, AxisBase axis)
//            {
//                int IValue = (int) value;
//                return String.valueOf(IValue);
//            }
//        });
        //图例：得到Lengend
        Legend legend = lineChart.getLegend();
        //隐藏Lengend
        legend.setEnabled(true);
        //隐藏描述
        Description description = new Description();
        description.setText(place);
        description.setEnabled(true);
        lineChart.setDescription(description);
        lineChart.setTouchEnabled(true); // 设置是否可以触摸
        lineChart.setDragEnabled(true);// 是否可以拖拽
        lineChart.setScaleEnabled(true);// 是否可以缩放 x和y轴, 默认是true
        lineChart.setScaleXEnabled(true); //是否可以缩放 仅x轴
        lineChart.setScaleYEnabled(true); //是否可以缩放 仅y轴

        lineChart.setPinchZoom(true);  //设置x轴和y轴能否同时缩放。默认是否
        lineChart.setDoubleTapToZoomEnabled(true);//设置是否可以通过双击屏幕放大图表。默认是true

        //折线图点的标记
//        MyMarkerView mv = new MyMarkerView(this);
//        lineChart.setMarker(mv);
        //设置数据
        lineChart.setData(data);
        //图标刷新
        lineChart.invalidate();
    }
}