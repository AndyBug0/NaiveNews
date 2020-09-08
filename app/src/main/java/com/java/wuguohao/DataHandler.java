package com.java.wuguohao;

import android.content.Context;
import android.util.Log;

import com.java.wuguohao.bean.NewsData;
import com.java.wuguohao.bean.NewsEvent;
import com.java.wuguohao.bean.NewsMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class DataHandler {
    private Context mContext;
    List<String> _id_list;

    public List<String> getIdList() { return _id_list; }

    public void readEvent(final int page, final int size, final String type, Context mContext_){
        mContext = mContext_;
        ReadByGet r;
        System.out.println(mContext.getString(R.string.news_event_url));
        r = new ReadByGet(mContext.getString(R.string.news_event_url)+"?type="+type+"&page="+page+"&size="+size, 1);
        Log.i("SUBTHREAD", mContext.getString(R.string.news_event_url));
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
        r.setPlace(place);
        Log.i("SUBTHREAD", mContext.getString(R.string.news_data_url));
        Thread t2 = new Thread(r);
        t2.start();
        try {
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void readMap(final int id, final String entity, Context mContext_){
        mContext = mContext_;
        ReadByGet r;
        r = new ReadByGet(mContext.getString(R.string.news_map_url)+"?entity="+entity,id);
        Log.i("SUBTHREAD", mContext.getString(R.string.news_map_url));
        Thread t3 = new Thread(r);
        t3.start();
        try {
            t3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private class ReadByGet extends Thread {
        String thisurl;
        int url_id;
        String place;

        ReadByGet(String url, Integer targetID){
            thisurl = url;
            url_id = targetID;
            _id_list = new ArrayList<>();
        }

        public void setPlace(String place) { this.place = place; }

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
//                        List<NewsEvent> e = NewsEvent.find(NewsEvent.class, "lang = ?", "en");
                        break;
                    case 2:
                        parseNewsData(buffer.toString(),"China");
                        break;
                    case 3:
                        parseNewsMap(buffer.toString());
//                        List<NewsMap> e = NewsMap.find(NewsMap.class, "label=?", "病毒");
//                        List<NewsMap> e = NewsMap.listAll(NewsMap.class);
//                        Log.e("fuckyou",e.toString());
                    case 4:

                    case 5:

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
                    _id_list.add(id_);
                    String aminer_id = data.optString("aminer_id");
                    JSONArray authors_name = data.optJSONArray("authors");
                    //author
                    JSONObject authors_name_obj = new JSONObject();
                    authors_name_obj.put("authors",authors_name);
                    String authors = authors_name_obj.toString();
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
        void parseNewsData(String jsonData,String place) {
            try{
                if (NewsData.find(NewsData.class, "place=?", place).isEmpty()) {
                    JSONObject jsonObject = new JSONObject(jsonData);
                    JSONObject value = jsonObject.getJSONObject(place);
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
                    NewsData newsData = new NewsData(place, begin, confirmed, cured, dead);
                    newsData.save();
                }
            } catch (JSONException e){
                e.printStackTrace();
            }
        }
        void parseNewsMap(String jsonData){
            try{
                JSONObject jsonObject = new JSONObject(jsonData);
                String code = jsonObject.getString("code");
                String msg  = jsonObject.getString("msg");
                JSONArray data = jsonObject.getJSONArray("data");
                int len = data.length();
                for(int i = 0;i < len;i++){
                    JSONObject map = data.getJSONObject(i);
                    String hot = map.getString("hot");
                    String label = map.getString("label");
                    Log.e("label",label);
                    String url = map.getString("url");
                    //abstract info
                    JSONObject abstractInfo = map.getJSONObject("abstractInfo");
                    String enwiki = "";
                    String baidu = "";
                    String ziwiki = "";
                    if(map.has("enwiki")){
                        enwiki = abstractInfo.getString("enwiki");
                    }
                    if(abstractInfo.has("baidu")) {
                        baidu = abstractInfo.getString("baidu");
                    }
                    if(abstractInfo.has("zhwiki")){
                        ziwiki = abstractInfo.getString("zhwiki");
                    }
                    // COVID
                    JSONObject COVID = abstractInfo.getJSONObject("COVID");
                    String properity = "";
                    if(COVID.has("properties")){
                        JSONObject properties = COVID.getJSONObject("properties");
                        properity = properties.toString();       //todo change to object
                    }

                    String relation = "";                           //todo split the stringf
                    String relation_url = "";
                    String relation_label = "";
                    String relation_forward = "";
                    if(COVID.has("relations")) {
                        JSONArray relations = COVID.getJSONArray("relations");
                        for (int j = 0; j < relations.length(); j++) {
                            JSONObject relations_j = relations.getJSONObject(i);
                            relation = relation + " " + relations_j.getString("relation");
                            relation_url = relation_url + " " + relations_j.getString("url");
                            relation_label = relation_label + " " + relations_j.getString("label");
                            relation_forward = relation_forward + " " + relations_j.getString("forward");
                        }
                    }
                    String img = map.getString("img");
                    NewsMap newsMap = new NewsMap( hot,  label,  url,  enwiki,  baidu,  ziwiki,  properity,
                            relation,  relation_url,  relation_label,  relation_forward,  img);
                    newsMap.save();
                }
            } catch (JSONException e){
                e.printStackTrace();
            }
        }
    }
}