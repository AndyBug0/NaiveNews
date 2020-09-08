package com.java.wuguohao.bean;

import android.util.Log;

import com.orm.SugarRecord;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NewsMap extends SugarRecord {
    String hot;
    String label;
    String url;
    String enwiki;
    String baidu;
    String ziwiki;
    String properity;
    String relation;
    String relation_url;
    String relation_label;
    String relation_forward;
    String img;
    public NewsMap(){
        Log.e("NewsMap Constructor","Call the empty constructor");
    }
    public NewsMap(String hot, String label, String url, String enwiki, String baidu, String ziwiki, String properity,
                   String relation, String relation_url, String relation_label, String relation_forward, String img){
        this.hot = hot; this.label = label; this.url = url; this.enwiki = enwiki; this.baidu = baidu; this.ziwiki = ziwiki;
        this.properity = properity; this.relation = relation; this.relation_url = relation_url; this.relation_label = relation_label;
        this.relation_forward = relation_forward; this.img = img;
        Log.e("NewsMap Constructor",this.properity);
    }

    public String getLabel() {
        return label;
    }

    public List<String> getRelation() {
        String []relations = relation.split(" ");
        List<String> toInteger = new ArrayList<>();
        int len = relations.length;
        for(int i = 0;i < len;i++){
            toInteger.add(relations[i]);
        }
        return toInteger;
    }
    public List<String> getRelationUrl() {
        String []relationUrls = relation_url.split(" ");
        List<String> toInteger = new ArrayList<>();
        int len = relationUrls.length;
        for(int i = 0;i < len;i++){
            toInteger.add(relationUrls[i]);
        }
        return toInteger;
    }
    public List<String> getRelationLabel() {
        String []relationsLabel = relation_label.split(" ");
        List<String> toInteger = new ArrayList<>();
        int len = relationsLabel.length;
        for(int i = 0;i < len;i++){
            toInteger.add(relationsLabel[i]);
        }
        return toInteger;
    }
    public List<String> getRelationForward() {
        String []relationsForward = relation_forward.split(" ");
        List<String> toInteger = new ArrayList<>();
        int len = relationsForward.length;
        for(int i = 0;i < len;i++){
            toInteger.add(relationsForward[i]);
        }
        return toInteger;
    }
    public List<String> getProperities(){
        try{
            JSONObject properities = new JSONObject(properity);
            Iterator iterator = properities.keys();
            List<String> returnValue = new ArrayList<>();
            while(iterator.hasNext()) {
                String key = (String) iterator.next();
                String value = properities.getString(key);
                value = key + ":" + value;
                returnValue.add(value);
            }
            return returnValue;
        } catch (JSONException e){
            e.printStackTrace();
        }
        return null;
    }
}
