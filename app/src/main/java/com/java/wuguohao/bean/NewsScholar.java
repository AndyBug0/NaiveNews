package com.java.wuguohao.bean;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.orm.SugarRecord;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class NewsScholar  extends SugarRecord {
    String aff;
    String avatar;
    String bind;
    String _id;
    String indices;
    String name;
    String name_zh;
    String num_followed;
    String num_viewed;
    String address;
    String affiliation;
    String affiliation_zh;
    String bio;
    String edu;
    String email;
    String email_cr;
    String email_u;
    String fax;
    String homepage;
    String note;
    String phone;
    String position;
    String work;
    String score;
    String source_type;
    String tags;
    String tags_score;
    String _index;
    String tab;
    boolean is_passed_away;

    public NewsScholar() {
    }

    public NewsScholar(
            String aff, String avatar, String bind, String id, String indices, String name, String name_zh, String num_followed, String num_viewed, String address, String affiliation, String affiliation_zh,
            String bio, String edu, String email, String email_cr, String email_u, String fax, String homepage, String note, String phone, String position, String work, String score, String sourcetype, String tags,
            String tags_score, String index, String tab, boolean is_passedaway) {
        this.aff = aff;
        this.avatar = avatar;
        this.bind = bind;
        this._id = id;
        this.indices = indices;
        this.name = name;
        this.name_zh = name_zh;
        this.num_followed = num_followed;
        this.num_viewed = num_viewed;
        this.address = address;
        this.affiliation = affiliation;
        this.affiliation_zh = affiliation_zh;
        this.bio = bio;
        this.edu = edu;
        this.email = email;
        this.email_cr = email_cr;
        this.email_u = email_u;
        this.fax = fax;
        this.homepage = homepage;
        this.note = note;
        this.phone = phone;
        this.position = position;
        this.work = work;
        this.score = score;
        this.source_type = sourcetype;
        this.tags = tags;
        this.tags_score = tags_score;
        this._index = index;
        this.tab = tab;
        this.is_passed_away = is_passedaway;
    }

    public String getName() { return (name_zh.equals("")) ? name : name_zh; }
    public String getID() { return _id; }
    public String getBio() { return bio; }
    public String getEdu() { return edu; }
    public String getWork() { return work; }
    public String getPosition() { return position; }
    public String getAffiliation() { return affiliation; }
    public String getFax() { return fax; }
    public String getHomepage() { return homepage; }
    public boolean getIsPassedAway() { return is_passed_away; }

    public Map<String, Float> getIndices() {
        try {
            JSONObject indice = new JSONObject(indices);
            Iterator iterator = indice.keys();
            HashMap<String, Float> returnMap = new HashMap<>();
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                String value = indice.getString(key);
                returnMap.put(key, Float.valueOf(value));
            }
            return returnMap;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> getTags() {
        List<String> tagList = new ArrayList<>();
        String [] tagArray = tags.split("@");
        for (String tag : tagArray) {
            tagList.add(tag);
        }
        return tagList;
    }

    public List<String> getEmail_u() {
        List<String> returnValue = new ArrayList<>();
        try {
            if (!email_u.isEmpty()) {
                JSONArray e_u = new JSONArray(email_u);
                for (int i = 0; i < e_u.length(); i++) {
                    JSONObject e = e_u.getJSONObject(i);
                    String value = e.getString("address");
                    returnValue.add(value);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return returnValue;
    }
}
