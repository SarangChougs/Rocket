package com.android.chatapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Splash screen activity
 */
public class SplashScreen extends AppCompatActivity {

    ImageView Rocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        Rocket = findViewById(R.id.rocket);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String Email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            GlobalClass.LoggedInUser.setEmail(Email);
            FirebaseFirestore.getInstance().collection("users")
                    .whereEqualTo("email", Email)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                                GlobalClass.LoggedInUser.setName(documentSnapshot.get("name").toString());
                                GlobalClass.LoggedInUser.setUsername(documentSnapshot.get("username").toString());
                                GlobalClass.LoggedInUser.setId(documentSnapshot.get("id").toString());
                                GlobalClass.LoggedInUser.setEmail(documentSnapshot.get("email").toString());
                                GlobalClass.LoggedInUser.setPicUrl(documentSnapshot.get("picUrl").toString());
                            }
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    startActivity(new Intent(SplashScreen.this, ChatsActivity.class));
                                    finish();
                                }
                            }, 2000);
                        }
                    });
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                }
            }, 4000);
        }

        AppController.getInstance().setOnVisibilityChangeListener(new AppController.ValueChangeListener() {
            @Override
            public void onChanged(Boolean value) {
                System.out.println(value);
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    DateFormat df = new SimpleDateFormat("h:mm aa dd/MM/yy");
                    Date obj = new Date();
                    final Map<String, String> map = new HashMap<>();
                    if (value)
                        map.put("status", "" + df.format(obj));
                    else
                        map.put("status", "online");
                    String Email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                    FirebaseFirestore.getInstance().collection("users")
                            .whereEqualTo("email", Email)
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                                        FirebaseFirestore.getInstance().collection("user_status")
                                                .document(documentSnapshot.get("id").toString())
                                                .set(map);
                                    }
                                }
                            });
                }
            }
        });
    }
}