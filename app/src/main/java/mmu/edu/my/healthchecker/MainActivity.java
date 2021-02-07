package mmu.edu.my.healthchecker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    TextView tcase;
    TextView thealed;
    TextView tdead;
    View youtube;
    TextView addressView;
    TextView dateToday;
    GridLayout mainGrid;
    CardView hospital;
    CardView symptoms;
    CardView tracker;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String UserID;
    String qAddress;
    boolean moved=false;
    String sessionId = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tcase = findViewById(R.id.totalCase);
        thealed = findViewById(R.id.totalHealed);
        tdead = findViewById(R.id.totalDeath);
        hospital =  findViewById(R.id.hospitalNearby);
        symptoms = findViewById(R.id.checkSymptoms);
        dateToday = findViewById(R.id.Date);
        tracker = findViewById(R.id.tracker);
        youtube = findViewById(R.id.video);
        addressView = findViewById(R.id.setAddress);
        mainGrid =  findViewById(R.id.gridLayout);
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        GetTotal p = new GetTotal();
        p.start();

        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        dateToday.setText(date);

        UserID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
        DocumentReference documentReference = fStore.collection("users").document(UserID);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    qAddress = documentSnapshot.getString("Quarantine Location");
                    Log.d("what1236",qAddress);
                    addressView.setText(qAddress);
                }else{
                    Toast.makeText(MainActivity.this, "Set Address", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("what1234","wsawd");
            }
        });

        sessionId = getIntent().getStringExtra("result");

        if(sessionId!=null){
            Log.d("result?:","NOTsame");
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Warning");
            builder.setMessage("You left Quarantine Area. Please return immediately");

            builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getApplicationContext(), "ok", Toast.LENGTH_SHORT).show();

                }
            });

            builder.setNegativeButton("no", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getApplicationContext(), "ok", Toast.LENGTH_SHORT).show();

                }
            });

            AlertDialog alert = builder.create();
            alert.show();
        }else{
            Toast.makeText(this, "In quarantine location", Toast.LENGTH_SHORT).show();
        }



        if (qAddress != null){
            addressView.setText(qAddress);
            //Log.d("what1",qAddress);
        }

        hospital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, hospital.class));
            }
        });

        symptoms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, symptomp.class));
            }
        });

        youtube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, video.class));
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

    @Override
    protected void onResume() {
        super.onResume();
        DocumentReference documentReference = fStore.collection("users").document(UserID);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    qAddress = documentSnapshot.getString("Quarantine Location");
                    Log.d("what123",qAddress);
                    addressView.setText(qAddress);
                }else{
                    Toast.makeText(MainActivity.this, "Set Address", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });



    }

    public void verify(View view) {
        if (qAddress != null){
            Log.d("verifying",qAddress);
            Intent mainIntent = new Intent(MainActivity.this, CheckLocation.class);
            mainIntent.putExtra("verifyAddress", qAddress);
            startActivity(mainIntent);
        }


        /*if(moved){
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Ramkhumar");
            builder.setMessage("1161102130");
            builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getApplicationContext(), "ok", Toast.LENGTH_SHORT).show();
                }
            });
            builder.setNegativeButton("no", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getApplicationContext(), "ok", Toast.LENGTH_SHORT).show();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }*/
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
                            //Log.d("case", e.text());
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
        //Log.d("vera", "maygod");
        Toast.makeText(MainActivity.this, "Logged Out", Toast.LENGTH_SHORT).show();
        finish();
    }
}