package com.android.chat.chat;

import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class LowVisionBlind_Index extends AppCompatActivity {
    private TextToSpeech mTTS;
    Button bnt_aveugle;
    Button bnt_reg;


    CircleImageView profile_image;
    TextView username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_low_vision_blind__index);

        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
        profile_image.setImageResource(R.drawable.logo);
        username.setText("ChatWithMe");

        mTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = mTTS.setLanguage(Locale.FRANCE);

                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Language not supported");
                    } else {
                        mTTS.speak("si vous étes une personne non-voyante appuyer sur le bouton en haut de votre écran. Si vous étes une personne malvoyante taper sur le bouton en bas de votre écran", TextToSpeech.QUEUE_FLUSH, null);
                    }
                } else {
                    Log.e("TTS", "Initialization failed");
                }
            }
        });

        bnt_aveugle = (Button)findViewById(R.id.bt_avg);

        bnt_aveugle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LowVisionBlind_Index.this,BlindIndex.class);
                startActivity(intent);
            }
        });

        bnt_reg =(Button)findViewById(R.id.but_blemRegard);

        bnt_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             //   Intent intent=new Intent(LowVisionBlind_Index.this,IndexActivity.class);
               // startActivity(intent);

                Bundle data= new Bundle();
                data.putString("Accessibility", "yes");// ce paran est utilise pour indiquer a indexActivity que
                // Talkback doit etre active
                Intent a=new Intent(LowVisionBlind_Index.this,IndexActivity.class);
                a.putExtras(data);
                Log.d("nnnnnnnnnnnnnnnn", "blind: "+true);

                startActivity(a);
            }
        });

    }
}
