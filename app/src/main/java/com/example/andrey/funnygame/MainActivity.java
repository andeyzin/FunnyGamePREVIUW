package com.example.andrey.funnygame;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends YouTubeBaseActivity{

    public static Context context;

    public static String result;
    public static String videoId = "hello";
    public static String videoTitle;
    public static String previewLink;
    public static Bitmap preview;


    public static Button gen;
    public static Spinner duration;
    public static Spinner tag;
    public static EditText yourTag;
    public static YouTubePlayerView player;
    public static Machine task;
    final static String apiKey = "AIzaSyBtR6GsaU4fUKAI4tZuLJfljOD8fIiF0S8";
    public static DBHelper dbHelper;
    public boolean isFirstStart;

    public static RVAdapter adapter;
    public static List<VideoMyClass> videoMyClass;
    public static RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                //  Intro App Initialize SharedPreferences
                SharedPreferences getSharedPreferences = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());

                //  Create a new boolean and preference and set it to true
                isFirstStart = getSharedPreferences.getBoolean("firstStart", true);

                //  Check either activity or app is open very first time or not and do action
                if (isFirstStart) {

                    //  Launch application introduction screen
                    Intent i = new Intent(MainActivity.this, MyIntro.class);
                    startActivity(i);
                    SharedPreferences.Editor e = getSharedPreferences.edit();
                    e.putBoolean("firstStart", false);
                    e.apply();
                }
            }
        });
        t.start();

        player = (YouTubePlayerView)findViewById(R.id.player);

        context = this;
        dbHelper = new DBHelper(context);

        rv = (RecyclerView)findViewById(R.id.rv);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(false);

        initializeData();
        initializeAdapter();

        Machine task = new Machine();
        task.execute("startVideo", videoId, "any");
    }

    private void initializeData(){
        videoMyClass = new ArrayList<>();
        videoMyClass.add(new VideoMyClass(videoTitle, null, videoId));
    }

    private static void initializeAdapter(){
        adapter = new RVAdapter(videoMyClass);
        rv.setAdapter(adapter);
        rv.scrollToPosition(0);
    }


    public void onClick(View v) {
        RVAdapter.lastTagValue = RVAdapter.BigViewHolder.tags.getSelectedItem().toString();
        RVAdapter.lastDurValue = RVAdapter.BigViewHolder.duration.getSelectedItem().toString();
        RVAdapter.lastEdtValue = RVAdapter.BigViewHolder.myTag.getText().toString();

        switch(v.getId()){
            //
            //    GENERATE BUTTON
            //
            case R.id.gen:
                super.onStop();
                super.onDestroy();
                super.onCreate(Bundle.EMPTY);

                task = new Machine();
                task.execute("randomWord", getTag(), getDuration());
                break;

            case R.id.space:
                super.onStop();
                super.onDestroy();
                super.onCreate(Bundle.EMPTY);

                task = new Machine();
                task.execute("randomWord", videoId, getDuration());
                break;
        }

    }

    public static String getTag(){
        String stringTag;
        switch (RVAdapter.BigViewHolder.tags.getSelectedItem().toString()){
            case  "Свой тег":
                stringTag = RVAdapter.BigViewHolder.myTag .getText().toString();
                break;
            case "Без тега":
                Random r = new Random();
                String seq = "abcdefghijklmnopqrstuvwxyz0123456789-_";
                String randSeq = "";
                for (int i = 0; i <= 3; i++) {
                    System.out.println(i);
                    randSeq += seq.charAt(r.nextInt(seq.length()));
                }
                stringTag = randSeq;
                break;
            default:
                stringTag = RVAdapter.BigViewHolder.tags.getSelectedItem().toString();
        }
        return stringTag;
    }

    public static String getDuration(){
        String stringDuration;
        switch (RVAdapter.BigViewHolder.duration.getSelectedItem().toString()){
            case "Короткие":
                stringDuration = "short";
                break;
            case "Длинные":
                stringDuration = "long";
                break;
            default:
                stringDuration = "any";
        }
        return stringDuration;
    }


    public void shareBtn(View v) throws JSONException {
        final Intent intent = new Intent(Intent.ACTION_SEND);
        String textToSend="I found this random video (https://www.youtube.com/watch?v="+videoId + ") with LuckyTube!!";
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, textToSend);
        try {
            startActivity(Intent.createChooser(intent, "Описание действия"));
        }
        catch (android.content.ActivityNotFoundException e) {
            Toast.makeText(getApplicationContext(), "Some error", Toast.LENGTH_SHORT).show();
        }


    }


    public static void onFinishTask(final String result){
        MainActivity.result = result;
        System.out.println( result);
        try {
            JSONObject jsonObj = new JSONObject(MainActivity.result);
            videoId =  jsonObj.getJSONObject("id").getString("videoId");
            videoTitle = jsonObj.getJSONObject("snippet").getString("title");
            previewLink = jsonObj.getJSONObject("snippet").getJSONObject("thumbnails").getJSONObject("high").getString("url");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        videoMyClass.add(1, new VideoMyClass(videoTitle, preview, videoId));

        startVideo();
        initializeAdapter();

        // datebase
        try {
            SQLiteDatabase database = dbHelper.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(DBHelper.KEY_VIDEO_ID, videoId);
            contentValues.put(DBHelper.KEY_TITLE, videoTitle);
            database.insert(DBHelper.TABLE_CONTACTS, null, contentValues);
            Cursor cursor = database.query(DBHelper.TABLE_CONTACTS, null, null, null, null, null, null);

            if (cursor.moveToFirst()) {
                int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
                int vide0IDIndex = cursor.getColumnIndex(DBHelper.KEY_VIDEO_ID);
                int titleIndex = cursor.getColumnIndex(DBHelper.KEY_TITLE);
                do {
                   // Toast.makeText(context, cursor.getString(vide0IDIndex), Toast.LENGTH_SHORT).show();
                   // Toast.makeText(context, cursor.getString(titleIndex), Toast.LENGTH_SHORT).show();
                } while (cursor.moveToNext());
            }
            cursor.close();
            database.delete(DBHelper.TABLE_CONTACTS, null, null);
        } catch (Exception e){
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    public static int getIndex(Spinner spinner, String myString){
        for (int i=0; i<spinner.getCount(); i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                return i;
            }
        }

        return 0;
    }

    public static void startVideo(){
        player.initialize(apiKey, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) { if (!b) {
                try {
                    youTubePlayer.setFullscreenControlFlags(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_ORIENTATION);
                    youTubePlayer.loadVideo(videoId);
                } catch (Exception e) {}
            }}

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) { }
        });
    }
}