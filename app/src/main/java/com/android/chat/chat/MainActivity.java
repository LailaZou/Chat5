package com.android.chat.chat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Locale;

import ai.api.AIListener;
import ai.api.android.AIConfiguration;
import ai.api.android.AIService;
import ai.api.model.AIError;
import ai.api.model.AIResponse;
import ai.api.model.Result;

public class MainActivity extends AppCompatActivity implements AIListener {
    AIService aiService;
    TextView t;
    TextToSpeech tts;
    FirebaseAuth auth;
    DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth=FirebaseAuth.getInstance();

        //initialize tts
        initializeTextToSpeech();

        t= (TextView) findViewById(R.id.textView);
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO);

        if (permission != PackageManager.PERMISSION_GRANTED) {

            makeRequest();
        }
        final AIConfiguration config = new AIConfiguration("d4961a36a9074e96b635febd533d3a51",
                AIConfiguration.SupportedLanguages.French,
                AIConfiguration.RecognitionEngine.System);
        aiService = AIService.getService(this, config);
        aiService.setListener(this);

    }

    protected void makeRequest() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.RECORD_AUDIO},
                101);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 101: {

                if (grantResults.length == 0
                        || grantResults[0] !=
                        PackageManager.PERMISSION_GRANTED) {


                } else {

                }
                return;
            }
        }
    }


    public void buttonClicked(View view){
        //   Toast.makeText(MainActivity.this,"click",Toast.LENGTH_LONG).show();

        aiService.startListening();
        // Toast.makeText(MainActivity.this,"finish listening",Toast.LENGTH_LONG).show();


    }

    @Override
    public void onResult(AIResponse result) {

        Log.d("anu",result.toString());
        Result result1=result.getResult();
        // t.setText("Query "+result1.getResolvedQuery()+" action: "+result1.getAction());
        t.setText("result  "+result.getResult().getFulfillment().getSpeech());
        String resultat=result.getResult().getFulfillment().getSpeech();
        if(resultat.equals("waiting")){
            final String message=result1.getParameters().get("message").toString().replace("\"","");
            final String username=result1.getParameters().get("username").toString().replace("\"","");
            speak(" message du contenu "+message+" envoyer a "+username);

            final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
            ref.orderByChild("username").equalTo(username).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()){
                        FirebaseUser firebaseUser = auth.getCurrentUser();
                    final String userid = firebaseUser.getUid();
                    reference = FirebaseDatabase.getInstance().getReference("Chats").push();
                        reference.child("sender").setValue(userid);
                        reference.child("receiver").setValue(childSnapshot.getKey());
                        reference.child("message").setValue(message);
                  /*  HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("sender", userid);
                    Log.d(" receiver", childSnapshot.getKey());
                    hashMap.put("receiver", childSnapshot.getKey());
                    hashMap.put("message", message);
                    reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            speak(" Message envoyer à " + username);
                        }
                    });*/
                        speak(" Message envoyer à " + username);

                    }
            }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }else{
            speak(result.getResult().getFulfillment().getSpeech());

        }


    }

    @Override
    public void onError(AIError error) {

    }

    @Override
    public void onAudioLevel(float level) {

    }

    @Override
    public void onListeningStarted() {

    }

    @Override
    public void onListeningCanceled() {

    }

    @Override
    public void onListeningFinished() {

    }


    private void initializeTextToSpeech() {
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (tts.getEngines().size() == 0 ){
                    Toast.makeText(MainActivity.this, getString(R.string.tts_no_engines),Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    tts.setLanguage(Locale.FRENCH);
                    speak("Bonjour. je suis prete que voulez-vous faire");
                }
            }
        });
    }


    private void speak(String message) {
        if(Build.VERSION.SDK_INT >= 21){
            tts.speak(message,TextToSpeech.QUEUE_FLUSH,null,null);
        } else {
            tts.speak(message, TextToSpeech.QUEUE_FLUSH,null);
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        tts.shutdown();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        Reinitialize the recognizer and tts engines upon resuming from background such as after openning the browser
        initializeTextToSpeech();
    }


}

