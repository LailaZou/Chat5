package com.android.chat.chat;

import android.content.Intent;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.Locale;

public class Global_IndexActivity extends  AppCompatActivity {
    private TextToSpeech mTTS;
    private static int SPLASH_TIME_OUT = 60000;

    Button btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_global__index);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run () {

                Intent homeIntent = new Intent(Global_IndexActivity.this, LowVisionBlind_Index.class);
                startActivity(homeIntent);
        //        finish();

                   onDestroy();
            }

        },SPLASH_TIME_OUT);

      /*  new Handler().postDelayed(new Runnable(){
            @Override
            public void run () {
                Intent homeIntent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(homeIntent);
                finish();
            }

        },SPLASH_TIME_OUT);*/


        mTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = mTTS.setLanguage(Locale.FRANCE);

                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Language not supported");
                    } else {
                        mTTS.speak("Bonjour, vous êtes sur le point d'accéder à une application de Chatt pour toutes les catégories de personnes qu'elles soient normales, sourdes, malvoyantes et non-voyantes. " +
                                "Si vous  êtes atteint de basse vision, veuillez attendre quelques minutes pour que l'application vous redirige automatiquement." +
                                "Si ce n'est pas le cas veuillez cliquer sur le bouton ci-dessous.", TextToSpeech.QUEUE_FLUSH, null);

                    }
                } else {
                    Log.e("TTS", "Initialization failed");
                }
            }
        });

        btn= (Button)findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             //   Intent intent=new Intent(Global_IndexActivity.this,IndexActivity.class);
               // startActivity(intent);

                Bundle data= new Bundle();
                data.putString("null", null);
                Intent a=new Intent(Global_IndexActivity.this,IndexActivity.class);
                a.putExtras(data);
                startActivity(a);

                mTTS.stop();
               // finish();
                onDestroy();
            }
        });



        //String text =;



       /* mButtonSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speak();
            }
        });*/
    }

    /*private void speak() {
        //String text = mEditText.getText().toString();
        String text ="bonjour tout le mende";
        mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }*/

    @Override
    protected void onDestroy() {
        if (mTTS != null) {
            mTTS.stop();
            mTTS.shutdown();
        }

        super.onDestroy();
    }
}