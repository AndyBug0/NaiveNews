package com.java.wuguohao.ui.scholar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.java.wuguohao.R;
import com.java.wuguohao.bean.NewsScholar;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

public class ScholarPageActivity extends AppCompatActivity {
    private RelativeLayout baseInfo;

    private TextView tagsContent;
    private TextView bioView;
    private TextView eduView;
    private TextView workView;

    private LinearLayout fax;
    private LinearLayout homePage;
    private LinearLayout email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme_NoActionBar);
        setContentView(R.layout.scholar_detail);
        ImageView backButton = (ImageView) findViewById(R.id.scholar_detail_back);
        backButton.setColorFilter(getResources().getColor(R.color.white));
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        String id = getIntent().getStringExtra("id");
        NewsScholar scholar = NewsScholar.find(NewsScholar.class, "_id=?", id).get(0);

        List<String> tags = scholar.getTags();
        String bio = scholar.getBio();
        String edu = scholar.getEdu();
        String work = scholar.getWork();
        String faxNumber = scholar.getFax();
        String homepageUrl = scholar.getHomepage();
        List<String> emailAddress = scholar.getEmail_u();

        initBaseInfo(scholar);
        initTagsView(tags);
        initExperienceView(bio, edu, work);
        initContactView(faxNumber, homepageUrl, emailAddress);
    }

    private TextView createTag(String text, Context context) {
        TextView tag = new TextView(context);
        tag.setBackground(ContextCompat.getDrawable(context, R.drawable.round_text_with_back));
        tag.setText(text);
        tag.setPadding(5, 5, 5 , 5);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(3,3,3,3);//4个参数按顺序分别是左上右下
        tag.setLayoutParams(layoutParams);
        tag.setGravity(Gravity.CENTER);
        return tag;
    }

    private void initBaseInfo(NewsScholar scholar) {
        baseInfo = (RelativeLayout) findViewById(R.id.scholar_base_info);
        Bitmap bitmap = readBitmap(scholar.getID() + "_avatar.png");
        ((ImageView) baseInfo.findViewById(R.id.scholar_abstract_avatar)).setImageBitmap(bitmap);
        ((TextView) baseInfo.findViewById(R.id.scholar_abstract_name)).setText(scholar.getName());
        ((TextView) baseInfo.findViewById(R.id.scholar_abstract_position)).setText(scholar.getPosition());
        ((TextView) baseInfo.findViewById(R.id.scholar_abstract_affiliation)).setText(scholar.getAffiliation());

        Map<String, Float> indice = scholar.getIndices();
        String h_index = "" + Math.round(indice.get("hindex"));
        String activity = indice.get("activity").toString();
        String star = indice.get("newStar").toString();
        String citations = "" + Math.round(indice.get("citations"));
        ((TextView) baseInfo.findViewById(R.id.scholar_abstract_h_index)).setText(h_index);
        ((TextView) baseInfo.findViewById(R.id.scholar_abstract_activity)).setText(activity);
        ((TextView) baseInfo.findViewById(R.id.scholar_abstract_star)).setText(star);
        ((TextView) baseInfo.findViewById(R.id.scholar_abstract_citations)).setText(citations);
    }

    private void initTagsView(List<String> tags) {
        tagsContent = (TextView) findViewById(R.id.scholar_tag_content);
        if (tags.isEmpty()) {
            findViewById(R.id.scholar_tag_layout).setVisibility(View.GONE);
        } else {
            String content = "";
            for (String tag : tags) {
                content = content + tag + " 、 ";
            }
            tagsContent.setText(content);
        }
    }

    private void initExperienceView(String bio, String edu, String work) {
        bioView = (TextView) findViewById(R.id.scholar_detail_bio);
        eduView = (TextView) findViewById(R.id.scholar_detail_edu);
        workView = (TextView) findViewById(R.id.scholar_detail_work);

        if (!bio.equals("")) {
            bioView.setText(bio);
        } else {
            findViewById(R.id.scholar_bio_layout).setVisibility(View.GONE);
        }
        if (!edu.equals("")) {
            eduView.setText(edu);
        } else {
            findViewById(R.id.scholar_edu_layout).setVisibility(View.GONE);
        }
        if (!work.equals("")) {
            workView.setText(work);
        } else {
            findViewById(R.id.scholar_work_layout).setVisibility(View.GONE);
        }
    }

    private void initContactView(String faxNumber, String homepageUrl, List<String> emailAddresses) {
        fax = (LinearLayout) findViewById(R.id.scholar_detail_fax);
        homePage = (LinearLayout) findViewById(R.id.scholar_detail_homepage);
        email = (LinearLayout) findViewById(R.id.scholar_detail_email);

        if (!faxNumber.equals("")) {
            ((TextView) fax.findViewById(R.id.scholar_detail_fax_number)).setText(faxNumber);
        } else {
            fax.setVisibility(View.GONE);
            findViewById(R.id.contact_split_line1).setVisibility(View.GONE);
        }
        if (!homepageUrl.equals("")) {
            ((TextView) homePage.findViewById(R.id.scholar_detail_homepage_url)).setText(homepageUrl);
        } else {
            homePage.setVisibility(View.GONE);
            findViewById(R.id.contact_split_line2).setVisibility(View.GONE);
        }
        if (!emailAddresses.isEmpty()) {
            String emails = "";
            for (String email : emailAddresses) {
                emails = emails + email + "\n";
            }
            ((TextView) email.findViewById(R.id.scholar_detail_email_address)).setText(emails);
        } else {
            email.setVisibility(View.GONE);
        }
    }

    private Bitmap readBitmap(String path) {
        Bitmap bitmap = null;
        try {
            FileInputStream inputStream = openFileInput(path);
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
