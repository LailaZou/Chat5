package com.android.chat.chat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.chat.chat.Adapter.MessageAdapter;
import com.android.chat.chat.Model.Chat;
import com.android.chat.chat.Model.User;
import com.android.chat.chat.Notifications.Token;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

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

        FirebaseApp.initializeApp(this);
        auth=FirebaseAuth.getInstance();

        updateToken(FirebaseInstanceId.getInstance().getToken());

        Log.d("ttttttttttttttt", "token : "+ FirebaseInstanceId.getInstance().getToken());
        Log.d("ttttttttttttttt", "token : "+ auth.getCurrentUser().getUid());
        setContentView(R.layout.activity_main);



        if(tts==null){
            //initialize tts
            initializeTextToSpeech();
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(onNotice, new IntentFilter("Msg"));

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

    private void updateToken(String token){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(auth.getCurrentUser().getUid()).setValue(token1);
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
            Log.d("ffffffffffffffffff","waiting");

            final String message=result1.getParameters().get("message").toString().replace("\"","");
            final String username=result1.getParameters().get("username").toString().replace("\"","");

            final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
            ref.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()){
                   final User user = childSnapshot.getValue(User.class);

                    Log.d("////////////////////", "enter  "+dataSnapshot.getValue());

                    FirebaseUser firebaseUser = auth.getCurrentUser();
                    final String userid = firebaseUser.getUid();
                    reference = FirebaseDatabase.getInstance().getReference("Chats").push();
                        reference.child("sender").setValue(userid);
                        reference.child("receiver").setValue(user.getId());
                        reference.child("message").setValue(message);
                        reference.child("isseen").setValue(false);
                    final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chatlist")
                            .child(userid)
                            .child(user.getId());

                    chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (!dataSnapshot.exists()){
                                chatRef.child("id").setValue(user.getId());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    final DatabaseReference chatRefReceiver = FirebaseDatabase.getInstance().getReference("Chatlist")
                            .child(user.getId())
                            .child(userid);
                    chatRefReceiver.child("id").setValue(userid);

              //    break;
                   }
                    speak(" Message envoyer à " + username);



                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });



            speak(" message du contenu " + message + " envoyer a " + username);


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
                 //   speak("Bonjour. je suis prete que voulez-vous faire");
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


    private BroadcastReceiver onNotice= new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String pack = intent.getStringExtra("package");
            String title = intent.getStringExtra("title");
            String text = intent.getStringExtra("text");

            Log.d("xxxxxxxxxxxxxx", "package "+pack);
            Log.d("xxxxxxxxxxxxxx", "title "+title);
            Log.d("xxxxxxxxxxxxxx", "text "+text);


           String speech =title+" contenu de message est "+text;
            tts.speak(speech, TextToSpeech.QUEUE_FLUSH, null);


          /*  TableRow tr = new TableRow(getApplicationContext());
            tr.setLayoutParams(new TableRow.LayoutParams( TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            TextView textview = new TextView(getApplicationContext());
            textview.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT,1.0f));
            textview.setTextSize(20);
            textview.setTextColor(Color.parseColor("#0B0719"));
            textview.setText(Html.fromHtml(pack +"<br><b>" + title + " : </b>" + text));
            tr.addView(textview);
            tab.addView(tr);*/




        }
    };

    public void sendMessage(final User user, String message){

        FirebaseUser firebaseUser = auth.getCurrentUser();
        final String userid = firebaseUser.getUid();
        reference = FirebaseDatabase.getInstance().getReference("Chats").push();
        reference.child("sender").setValue(userid);
        reference.child("receiver").setValue(user.getId());
        reference.child("message").setValue(message);
        reference.child("isseen").setValue(false);


        final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(userid)
                .child(user.getId());

        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                    chatRef.child("id").setValue(user.getId());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final DatabaseReference chatRefReceiver = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(user.getId())
                .child(userid);
        chatRefReceiver.child("id").setValue(userid);






    }
}

