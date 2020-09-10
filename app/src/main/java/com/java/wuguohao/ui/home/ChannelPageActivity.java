package com.java.wuguohao.ui.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.java.wuguohao.R;

import java.util.HashSet;

public class ChannelPageActivity extends AppCompatActivity {
    private boolean isEdit = false;
    private Animation shake;

    private GridLayout myChannel;
    private GridLayout optionalChannel;

    private HashSet<String> myType;
    private HashSet<String> optionalType;

    @SuppressLint("UseCompatLoadingForDrawables")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.channel_page);
        shake = AnimationUtils.loadAnimation(ChannelPageActivity.this, R.anim.anim_shake);

        initGridLayout();
        initButtonView();

        SharedPreferences sharedPreferences = getSharedPreferences("channel", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        myType = (HashSet<String>) sharedPreferences.getStringSet("my_channel", new HashSet<String>());
        optionalType = (HashSet<String>) sharedPreferences.getStringSet("option_channel", new HashSet<String>());

        if (myType.isEmpty()) {
            myType.add("all");
            myType.add("news");
            myType.add("paper");
            editor.putStringSet("my_channel", myType);
            editor.putBoolean("set", true);
            editor.commit();
        }
        for (String type : myType) {
            myChannel.addView(createChannelView(type, myChannel.getContext(), true));
        }
        for (String type : optionalType) {
            optionalChannel.addView(createChannelView(type, optionalChannel.getContext(), false));
        }
    }

    private TextView createChannelView(final String type, Context context, boolean isMyChannel) {
        TextView view = new TextView(context);
        view.setBackground(getResources().getDrawable(R.drawable.round_view));
        view.setText(NewsFragment.titleMap.get(type));
        view.setBackgroundColor(getResources().getColor(R.color.grayLight));
        view.setPadding(5, 5, 5 , 5);
        view.setGravity(Gravity.CENTER);
        view.setTag(type);
        view.setWidth(300);
        view.setHeight(100);
        View.OnClickListener listener;
        if (type.equals("all")) {
            view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            view.getPaint().setFakeBoldText(true);
            view.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        } else {
            view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            view.setTextColor(getResources().getColor(R.color.black));
            if (isMyChannel) {
                listener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isEdit) {
                            String selectedType = view.getTag().toString();
                            myChannel.removeView(view);
                            myChannel.clearDisappearingChildren();
                            optionalChannel.addView(createChannelView(selectedType, optionalChannel.getContext(), false));

                            SharedPreferences sharedPreferences = getSharedPreferences("channel", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            myType.remove(selectedType);
                            optionalType.add(selectedType);
                            editor.putStringSet("my_channel", myType);
                            editor.putStringSet("option_channel", optionalType);
                            editor.putBoolean("set", true);
                            editor.commit();
                        } else {

                        }
                    }
                };
            } else {
                listener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String selectedType = view.getTag().toString();
                        optionalChannel.removeView(view);
                        optionalChannel.clearDisappearingChildren();
                        TextView newView = createChannelView(selectedType, myChannel.getContext(), true);
                        if (isEdit) {
                            shake.reset();
                            shake.setFillAfter(true);
                            newView.startAnimation(shake);
                        }
                        myChannel.addView(newView);

                        SharedPreferences sharedPreferences = getSharedPreferences("channel", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        optionalType.remove(selectedType);
                        myType.add(selectedType);
                        editor.putStringSet("my_channel", myType);
                        editor.putStringSet("option_channel", optionalType);
                        editor.putBoolean("set", true);
                        editor.commit();
                    }
                };
            }
            view.setOnClickListener(listener);
        }
        return view;
    }

    private void initGridLayout() {
        myChannel = (GridLayout) findViewById(R.id.my_channel);
        optionalChannel = (GridLayout) findViewById(R.id.option_channel);
    }

    private void initButtonView() {
        ImageView returnButton = (ImageView) findViewById(R.id.channel_return_button);
        final TextView editButton = (TextView) findViewById(R.id.channel_edit);

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isEdit) {
                    shake.reset();
                    shake.setFillAfter(true);
                    editButton.setText("完成");
                    int cnt = myChannel.getChildCount();
                    for (int i = 0; i < cnt; i ++) {
                        TextView channel = (TextView) myChannel.getChildAt(i);
                        if (!channel.getText().toString().equals("全部"))
                            channel.startAnimation(shake);
                    }
                    ((TextView) findViewById(R.id.my_channel_tip)).setText("点击删除频道");
                } else {
                    editButton.setText("编辑");
                    int cnt = myChannel.getChildCount();
                    for (int i = 0; i < cnt; i ++)
                        myChannel.getChildAt(i).clearAnimation();
                    ((TextView) findViewById(R.id.my_channel_tip)).setText("");
                }
                isEdit = !isEdit;
            }
        });

        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(1);
                finish();
            }
        });
        returnButton.setColorFilter(getResources().getColor(R.color.white));
    }

}
