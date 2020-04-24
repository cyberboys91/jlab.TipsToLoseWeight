package jlab.TipsToLoseWeight.Activity.Utils;

/*
 * Created by Javier on 22/03/2020.
 */

import java.util.Locale;
import java.util.Random;
import android.util.Base64;
import java.util.ArrayList;
import android.util.LruCache;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.database.sqlite.SQLiteDatabase;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class TipManager extends SQLiteAssetHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "tips.db";
    private static final String TIP_TABLE_NAME = "Tips";
    private static final String IMAGES_TABLE_NAME = "Images";
    private static final String ID_COLUMN = "id";
    private static final String TIP_ID_COLUMN = "idTip";
    private static final String TITLE_COLUMN = "Title";
    private static final String DESCRIPTION_COLUMN = "Description";
    private static final String CURIOSITY_COLUMN = "Curiosity";
    private static final String IMAGE_COLUMN = "Image";
    private static final String TIP_COLUMN_FOREING_KEY = "Tip";
    private static final String LANGUAGE_COLUMN = "Language";
    private static LruCache<Integer, Bitmap> bitmapCache = new LruCache<>(30);

    public TipManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public Tip getTip(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TIP_TABLE_NAME, null, ID_COLUMN + "= ?", new String[] {String.valueOf(id)}, null, null, null, null);
        Tip result = null;
        if (cursor.moveToFirst()) {
            result = new Tip(id, cursor.getString(cursor.getColumnIndex(TITLE_COLUMN)),
                    cursor.getString(cursor.getColumnIndex(DESCRIPTION_COLUMN)),
                    cursor.getString(cursor.getColumnIndex(CURIOSITY_COLUMN)),
                    cursor.getInt(cursor.getColumnIndex(TIP_ID_COLUMN)));
        }
        cursor.close();
        return result;
    }

    public ArrayList<String> getBase64Images (int tipId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(IMAGES_TABLE_NAME, null, TIP_COLUMN_FOREING_KEY + "= " + tipId, null, null, null, null, null);
        ArrayList<String> result = new ArrayList<>();
        while (cursor.moveToNext())
            result.add(cursor.getString(cursor.getColumnIndex(IMAGE_COLUMN)));
        cursor.close();
        return result;
    }

    public ArrayList<Bitmap> getBitmapImages (int tipId) {
        ArrayList<Bitmap> result = new ArrayList<>();
        for (String base64: getBase64Images(tipId))
            result.add(getBitmapFromBase64(base64));
        return result;
    }

    public Bitmap getImage (int tipId) {
        Bitmap result = bitmapCache.get(tipId);
        if(result == null) {
            ArrayList<String> images = getBase64Images(tipId);
            result = getBitmapFromBase64(images.get(new Random().nextInt(images.size())));
            bitmapCache.put(tipId, result);
        }
        return result;
    }

    private Bitmap getBitmapFromBase64 (String base64) {
        byte[] decodeString = Base64.decode(base64, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodeString, 0, decodeString.length);
    }

    private String getLanguage () {
        //TODO: Actualizar al añadir nuevos idiomas
        String language = Locale.getDefault().getLanguage();
        switch (language) {
            case "es":
            case "en":
                return language;
            default:
                return "en";
        }
    }

    public ArrayList<Tip> getAllDetails () {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TIP_TABLE_NAME, null, String.format("%s = ?", LANGUAGE_COLUMN),
                new String[]{getLanguage()}, null, null, null, null);
        ArrayList<Tip> result = new ArrayList<>();
        while (cursor.moveToNext())
            result.add(new Tip(cursor.getInt(cursor.getColumnIndex(ID_COLUMN)),
                    cursor.getString(cursor.getColumnIndex(TITLE_COLUMN)),
                    cursor.getString(cursor.getColumnIndex(DESCRIPTION_COLUMN)),
                    cursor.getString(cursor.getColumnIndex(CURIOSITY_COLUMN)),
                    cursor.getInt(cursor.getColumnIndex(TIP_ID_COLUMN))));
        cursor.close();
        return result;
    }
}
