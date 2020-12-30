package mmu.edu.my.healthchecker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    TextView tcase;
    TextView thealed;
    TextView tdead;
    TextView youtube;
    TextView addressView;
    TextView dateToday;
    GridLayout mainGrid;
    CardView hospital;
    CardView symptoms;
    CardView tracker;
    Handler myHandler;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tcase = findViewById(R.id.totalCase);
        thealed = findViewById(R.id.totalHealed);
        tdead = findViewById(R.id.totalDeath);
        hospital = (CardView) findViewById(R.id.hospitalNearby);
        symptoms = findViewById(R.id.checkSymptoms);
        dateToday = findViewById(R.id.Date);
        tracker = findViewById(R.id.tracker);
        youtube = findViewById(R.id.checkThis);
        addressView = findViewById(R.id.imageView2);
        mainGrid = (GridLayout) findViewById(R.id.gridLayout);

        GetTotal p = new GetTotal();
        p.start();

        String sessionId = getIntent().getStringExtra("address");
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        dateToday.setText(date);

        if(sessionId != null){
            addressView.setText(sessionId);
            Log.d("veraa", sessionId);
        }

        hospital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=hospital%20near%20me"));
                startActivity(browserIntent);
            }
        });

        symptoms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://forms.gle/gC65xupS4qWma6YD9"));
                startActivity(browserIntent);
            }
        });

        youtube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://youtube.com/playlist?list=PL5Jn1YJ9y525Ep0wK_Q2VIQba_ruS-bCy"));
                startActivity(browserIntent);
            }
        });

        tracker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, tracker.class);
                startActivity(intent);
            }
        });


    }

    public class GetTotal extends Thread{
        Elements message1;

        @Override
        public void run() {
            try{
                Document doc = Jsoup.connect("https://www.google.com/search?rlz=1C1CHBF_enMY913MY913&sxsrf=ALeKk01VgkOml4i4zkLLjW9PAUCPzAdgxQ%3A1608730603254&ei=60fjX5eFD5LD3LUPkIiIiAo&q=malaysia+covid&oq=mala&gs_lcp=CgZwc3ktYWIQAxgAMgoIABCxAxDJAxBDMgQIABBDMgQIABBDMgQIABBDMgcIABCxAxBDMgQIABBDMgcILhCxAxBDMgQIABBDMgoIABCxAxCDARBDMgQIABBDOgQIABBHOgQIIxAnOgcIABDJAxBDOgQILhBDOhAILhCxAxCDARDHARCjAhBDOgQILhAnOgoILhCxAxCDARBDUKPHhgJYjcmGAmC-1YYCaABwBngAgAFUiAGmApIBATSYAQCgAQGqAQdnd3Mtd2l6yAEIwAEB&sclient=psy-ab").get();
                message1 = doc.getElementsByAttributeValue("class","m7B03");

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        int i=0;
                        for(Element e : message1) {
                            Log.d("case", e.text());
                            if (i == 0) {
                                String[] splitStr1 = e.text().trim().split("\\s+");
                                tcase.setText(splitStr1[0]);

                            }
                            if (i == 1) {
                                String[] splitStr2 = e.text().trim().split("\\s+");
                                thealed.setText(splitStr2[0]);
                            }
                            if (i == 2) {
                                String[] splitStr3 = e.text().trim().split("\\s+");
                                tdead.setText(splitStr3[0]);
                            }
                            i++;
                        }
                    }
                });

            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }



    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(),Login.class));
        Log.d("vera", "maygod");
        Toast.makeText(MainActivity.this, "Logged Out", Toast.LENGTH_SHORT).show();
        finish();
    }
}