package com.java.wuguohao;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.java.wuguohao.bean.NewsData;
import com.java.wuguohao.bean.NewsEvent;
import com.java.wuguohao.bean.NewsMap;
import com.java.wuguohao.bean.NewsScholar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import static java.lang.Math.min;

public class DataHandler {
    private Context mContext;
    private BitmapFileSaver bitmapSaver;
    private Bitmap bitmap = null;

    public Bitmap getBitmap() { return bitmap; }

    public void readImage(final String img) {
        URL my_url;
        HttpURLConnection httpUrl = null;
        BufferedInputStream bis = null;
        try{
            my_url = new URL(img);
            httpUrl = (HttpURLConnection) my_url.openConnection();
            httpUrl.connect();
            bis = new BufferedInputStream(httpUrl.getInputStream());
            bitmap = BitmapFactory.decodeStream(bis);
            bis.close();
            httpUrl.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    public void readEvent(final int page, final int size, final String type, Context mContext_){
        mContext = mContext_;
        ReadByGet r;
        r = new ReadByGet(mContext.getString(R.string.news_event_url)+"?type="+type+"&page="+page+"&size="+size, 1);
        Thread t1 = new Thread(r);
        t1.start();
        try {
            t1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
    public void readData(final String place, Context mContext_){
        mContext = mContext_;
        ReadByGet r;
        r = new ReadByGet(mContext.getString(R.string.news_data_url), 2);
        r.setStringArg(place);
        Thread t2 = new Thread(r);
        t2.start();
        try {
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public List<String> readMap(final String entity, Context mContext_){
        mContext = mContext_;
        ReadByGet r;
        r = new ReadByGet(mContext.getString(R.string.news_map_url)+"?entity="+entity, 3);
        r.setStringArg(entity);
        Thread t3 = new Thread(r);
        t3.start();
        try {
            t3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return r.getMapUrls();
    }
    public void readScholar(Context mContext_){
        mContext = mContext_;
        ReadByGet r;
        r = new ReadByGet(mContext.getString(R.string.news_scholar_url), 4);
        Thread t5 = new Thread(r);
        t5.start();
        try {
            t5.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private class ReadByGet extends Thread {
        String thisurl;
        int url_id;
        String strArg;
        List<String> mapUrls;

        ReadByGet(String url, Integer targetID){
            thisurl = url;
            url_id = targetID;
        }

        public void setStringArg(String arg) { this.strArg = arg; }

        public List<String> getMapUrls() { return mapUrls; }

        @Override
        public void run() {
            try {
                URL url = new URL(thisurl);
                //使用方法打开链接,并使用connection接受返回值
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setAllowUserInteraction(false);
                connection.setInstanceFollowRedirects(true);
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(15000);
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.connect();
                int responseCode = connection.getResponseCode();
                // 发起请求
                connection.connect();
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.e("HTTPS", "[DataActivity line 69] NOT OK");
                }
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));
                StringBuffer buffer = new StringBuffer();
                int line;
                try {
                    while ((line = reader.read()) != -1) {
                        buffer.append((char)line);
                    }
                } catch (IOException e) {
                    System.out.println("IOException at DataActivity line 102");
                }
                reader.close();
                connection.disconnect();

                switch (url_id){
                    case 1:
                        parseNewsEvent(buffer.toString());
                        break;
                    case 2:
                        parseNewsData(buffer.toString());
                        break;
                    case 3:
                        parseNewsMap(buffer.toString());
                    case 4:
                        parseNewsScholar(buffer.toString());
                    default:
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        void parseNewsEvent(String jsonData){
            try
            {
                JSONObject jsonObject = new JSONObject(jsonData);
                JSONArray datas = jsonObject.optJSONArray("data");
                JSONObject pagination = jsonObject.optJSONObject("pagination"); //todo parse pagination
                String status = jsonObject.optString("status");          //todo parse status

                for (int i=0; i < datas.length(); i++)    {
                    JSONObject data = datas.optJSONObject(i);
                    String id_ = data.optString("_id");
                    String aminer_id = data.optString("aminer_id");
                    //author
                    JSONArray authors_name = data.optJSONArray("authors");
                    //author
                    String authors = "";
                    if (authors_name != null) {
                        for (int j = 0; j < authors_name.length(); j++) {
                            JSONObject authors_name_obj = authors_name.optJSONObject(j);
                            authors = authors + authors_name_obj.getString("name") + " ";
                        }
                    }
                    String category = data.optString("category");
                    String content = data.optString("content");
                    String date = data.optString("date");
                    String doi = data.optString("doi");
                    //entities
                    JSONArray entitiess = data.optJSONArray("entities");
                    JSONObject entities_obj = new JSONObject();
                    entities_obj.put("entities",entitiess);
                    String entities = entities_obj.toString();
                    //todo create geoinfo
                    JSONArray geoinfo_ = data.optJSONArray("geoInfo");
                    JSONObject geoinfo_obj = new JSONObject();
                    geoinfo_obj.put("geoinfo",geoinfo_);
                    String geoinfo = geoinfo_obj.toString();
                    long id = data.optInt("id");
                    String influence = data.optString("influence");
                    String lang = data.optString("lang");
                    String pdf = data.optString("pdf");
                    //regionIDs
                    JSONArray regionIDs_ = data.optJSONArray("regionIDs");
                    JSONObject regionIDs_obj = new JSONObject();
                    regionIDs_obj.put("regionIDs",regionIDs_);
                    String regionIDs = regionIDs_obj.toString();
                    //related_events
                    JSONArray related_events_ = data.optJSONArray("related_events");
                    JSONObject related_events_obj = new JSONObject();
                    related_events_obj.put("related_events",related_events_);
                    String related_events = related_events_obj.toString();
                    String seg_text = data.optString("seg_text");
                    String source = data.optString("source");
                    String tflag = data.optString("tflag");     //todo may wrong ! tflag is int
                    String time = data.optString("time");
                    String title = data.optString("title");
                    String type = data.optString("type");
                    JSONArray urlss = data.optJSONArray("urls");
                    JSONObject urlss_obj = new JSONObject();
                    urlss_obj.put("urls",urlss);
                    String urls = urlss_obj.toString();
                    String year = data.optString("year");       //todo may wrong ! year is int
                    if (NewsEvent.find(NewsEvent.class, "_id=?", id_).isEmpty()) {
                        NewsEvent newsEvent = new NewsEvent(id_, aminer_id, authors, category, content, date, doi, entities,
                                geoinfo, id, influence, lang, pdf, regionIDs, related_events,
                                seg_text, source, tflag, time, title, type, urls, year, false);
                        newsEvent.save();
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }

        }
        void parseNewsData(String jsonData) {
            if (NewsData.find(NewsData.class, "place=?", strArg).isEmpty()) {
                try {
                    JSONObject jsonObject = new JSONObject(jsonData);
                    JSONObject value = jsonObject.getJSONObject(strArg);
                    String begin = value.getString("begin");
                    JSONArray data = value.getJSONArray("data");
                    String confirmed = "";
                    String cured = "";
                    String dead = "";
                    for (int i = 0; i < data.length(); i++) {
                        JSONArray tmp = data.getJSONArray(i);
                        confirmed = confirmed.concat(Integer.toString(tmp.getInt(0)));
                        cured = cured.concat(tmp.getString(2));
                        dead = dead.concat(tmp.getString(3));
                        confirmed = confirmed + " ";
                        cured = cured + " ";
                        dead = dead + " ";
                    }
                    NewsData newsData = new NewsData(strArg, begin, confirmed, cured, dead);
                    newsData.save();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        void parseNewsMap(String jsonData) {
            mapUrls = new ArrayList<>();
            try {
                JSONObject jsonObject = new JSONObject(jsonData);
                String code = jsonObject.getString("code");
                String msg = jsonObject.getString("msg");
                JSONArray data = jsonObject.getJSONArray("data");
                int len = data.length();
                for (int i = 0; i < len; i++) {
                    JSONObject map = data.getJSONObject(i);
                    String hot = map.getString("hot");
                    String label = map.getString("label");
                    String url = map.getString("url");
                    mapUrls.add(url);
                    //abstract info
                    JSONObject abstractInfo = map.getJSONObject("abstractInfo");
                    String enwiki = "";
                    String baidu = "";
                    String ziwiki = "";
                    if (map.has("enwiki")) {
                        enwiki = abstractInfo.getString("enwiki");
                    }
                    if (abstractInfo.has("baidu")) {
                        baidu = abstractInfo.getString("baidu");
                    }
                    if (abstractInfo.has("zhwiki")) {
                        ziwiki = abstractInfo.getString("zhwiki");
                    }
                    // COVID
                    JSONObject COVID = abstractInfo.getJSONObject("COVID");
                    String properity = "";
                    if (COVID.has("properties")) {
                        JSONObject properties = COVID.getJSONObject("properties");
                        properity = properties.toString();       //todo change to object
                    }

                    String relation = "";                           //todo split the stringf
                    String relation_url = "";
                    String relation_label = "";
                    String relation_forward = "";
                    if (COVID.has("relations")) {
                        JSONArray relations = COVID.getJSONArray("relations");
                        for (int j = 0; j < relations.length() - 1; j++) {
                            JSONObject relations_j = relations.getJSONObject(j);
                            relation = relation + relations_j.getString("relation") + " ";
                            relation_url = relation_url + relations_j.getString("url") + " ";
                            relation_label = relation_label + relations_j.getString("label") + " ";
                            relation_forward = relation_forward + relations_j.getString("forward") + " ";
                        }
                        JSONObject relations_j = relations.getJSONObject(relations.length() - 1);
                        relation = relation + relations_j.getString("relation") + " ";
                        relation_url = relation_url + relations_j.getString("url") + " ";
                        relation_label = relation_label + relations_j.getString("label") + " ";
                        relation_forward = relation_forward + relations_j.getString("forward") + " ";
                    }
                    String img = map.getString("img");
                    if (map.isNull("img")) {
                        img = "NoImage";
                    } else {
                        img = map.getString("img");
                    }

                    if (NewsMap.find(NewsMap.class, "url=?", url).isEmpty()) {
                        NewsMap newsMap = new NewsMap(hot, label, url, enwiki, baidu, ziwiki, properity,
                                relation, relation_url, relation_label, relation_forward, img);
                        newsMap.save();
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        void parseNewsScholar(String jsonData){
            try {
                JSONObject jsonObject = new JSONObject(jsonData);
                String status = jsonObject.getString("status");
                String message = jsonObject.getString("message");
                JSONArray data = jsonObject.getJSONArray("data");
                for(int i = 0;i < data.length();i++){
                    JSONObject item = data.getJSONObject(i);
                    String aff = "";
                    if(item.has("emails_u"))
                        aff = item.getString("aff");         //todo
                    String avatar;
                    if (item.isNull("avatar")) {
                        avatar = "NoAvatar";
                    } else {
                        avatar = item.getString("avatar");
                    }
                    String bind = item.getString("bind");
                    String id = item.getString("id");
                    String indices = item.getString("indices"); //todo
                    String name = item.getString("name");
                    String name_zh = item.getString("name_zh");
                    String num_followed = item.getString("num_followed");
                    String num_viewed = item.getString("num_viewed");
                    JSONObject profile = item.getJSONObject("profile");
                    //profile
                    String address = "";
                    if(profile.has("address"))
                        address = profile.getString("address");
                    String affiliation = "";
                    if(profile.has("affiliation"))
                        affiliation = profile.getString("affiliation");
                    String affiliation_zh = "";
                    if(profile.has("affiliation_zh"))
                        affiliation_zh = profile.getString("affiliation_zh");
                    String bio = "";
                    if(profile.has("bio"))
                        bio = profile.getString("bio");
                    String edu = "";
                    if(profile.has("edu"))
                        edu = profile.getString("edu");
                    String email = "";
                    if(profile.has("email"))
                        email = profile.getString("email");
                    String email_cr = "";
                    if(profile.has("email_cr"))
                        email_cr = profile.getString("email_cr");
                    String email_u = "";
                    if(profile.has("emails_u"))
                        email_u = profile.getString("emails_u"); //todo
                    String fax = "";
                    if(profile.has("fax"))
                        fax = profile.getString("fax");
                    String homepage = "";
                    if(profile.has("homepage"))
                        homepage = profile.getString("homepage");
                    String note = "";
                    if(profile.has("note"))
                        note = profile.getString("note");
                    String phone = "";
                    if(profile.has("phone"))
                        phone = profile.getString("phone");
                    String position = "";
                    if(profile.has("position"))
                        position = profile.getString("position");
                    String work = "";
                    if(profile.has("work"))
                        work = profile.getString("work");
                    //\profile
                    String score = item.getString("score");
                    String sourcetype = item.getString("sourcetype");
                    JSONArray tagss = null;
                    String tags = "";
                    if(item.has("tags")){
                        tagss = item.getJSONArray("tags");
                        for(int k = 0; k < min(tagss.length(), 8); k++){
                            tags = tags + tagss.get(k) + "@";
                        }
                    }
                    String tags_score = "";
                    if(item.has("tags_score"))
                        tags_score = item.getString("tags_score");
                    String index = item.getString("index");
                    String tab = item.getString("tab");
                    boolean isPassedAway = item.getBoolean("is_passedaway");
                    if (NewsScholar.find(NewsScholar.class, "_id=?", id).isEmpty()) {
                        NewsScholar newsScholar = new NewsScholar(
                                aff, avatar, bind, id, indices, name, name_zh, num_followed, num_viewed, address, affiliation, affiliation_zh,
                                bio, edu, email, email_cr, email_u, fax, homepage, note, phone, position, work, score, sourcetype, tags,
                                tags_score, index, tab, isPassedAway);
                        newsScholar.save();

                        readImage(avatar);
                        bitmapSaver.saveBitmap(id + "_avatar.png", getBitmap());
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    interface BitmapFileSaver {
        void saveBitmap(String name, Bitmap bitmap);
    }

    public void setBitmapFileSaver(BitmapFileSaver saver) {
        this.bitmapSaver = saver;
    }
}