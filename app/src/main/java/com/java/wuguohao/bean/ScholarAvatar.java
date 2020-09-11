package com.java.wuguohao.bean;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.java.wuguohao.DataHandler;
import com.orm.SugarRecord;

import java.io.ByteArrayOutputStream;

public class ScholarAvatar extends SugarRecord {
    String _id;
    String avatar;

    public ScholarAvatar() {}
    public ScholarAvatar(String _id, String avatarUrl) {
        this._id = _id;
        if (!avatarUrl.equals("NoAvatar")) {
            saveAvatar(avatarUrl);
        } else {
            avatar = "None";
        }
    }
    public String getID() { return _id; }

    private void saveAvatar(final String avatarUrl) {
        final DataHandler dataHandler = new DataHandler();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                dataHandler.readImage(avatarUrl);
            }
        });

        try {
            t.start();
            t.join();
            saveBitmap(dataHandler.getBitmap());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void saveBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] bytes = stream.toByteArray();// 转为byte数组
            avatar = Base64.encodeToString(bytes,Base64.DEFAULT);
        } else {
            avatar = "None";
        }
    }

    public Bitmap getAvatarBitmap() {
        if (!avatar.equals("None")) {
            byte[] bytes= Base64.decode(avatar, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(bytes,0, bytes.length);
        }
        return null;
    }
}
