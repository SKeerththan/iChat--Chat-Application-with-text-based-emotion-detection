package keerththan.sk.ichat;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.languageid.FirebaseLanguageIdentification;
import com.google.firebase.ml.naturallanguage.languageid.FirebaseLanguageIdentificationOptions;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import keerththan.sk.ichat.Adapter.ChatAdapter;
import keerththan.sk.ichat.Models.MessageModel;
import keerththan.sk.ichat.databinding.ActivityChatDetailBinding;

public class ChatDetailActivity extends AppCompatActivity {
    ArrayList<MessageModel> messageModels;

    String recId;
    private boolean connected;
    Translate translate;
String emotionLable="";
Float emotionScore;


    private String originalText;
//    private String translatedText;



    FirebaseDatabase database;
    FirebaseAuth mAuth;
    ActivityChatDetailBinding binding;

    public static String engTranslatedText="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();


        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();


        final String senderId = mAuth.getUid();
        String receiverId = getIntent().getStringExtra("userId");
        String userName = getIntent().getStringExtra("userName");
        String profilePic = getIntent().getStringExtra("profilePic");


        binding.userName.setText(userName);
        Picasso.get().load(profilePic).placeholder(R.drawable.avatar).into(binding.profileImage);



        binding.backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatDetailActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        final ArrayList<MessageModel> messageModels = new ArrayList<>();
        final ChatAdapter chatAdapter = new ChatAdapter(messageModels, this, receiverId);

        binding.chatRecyclerView.setAdapter(chatAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.chatRecyclerView.setLayoutManager(layoutManager);
        final String senderRoom = senderId + receiverId;
        final String receiverRoom = receiverId + senderId;


        database.getReference().child("Chats").child(senderRoom).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageModels.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    MessageModel model = snapshot1.getValue(MessageModel.class);


                    model.setMessageId(snapshot1.getKey());

                    messageModels.add(model);

                }
                chatAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        binding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = binding.enterMessage.getText().toString();
                chatAdapter.translateToEnglish(message);

//                final MessageModel model = new MessageModel(senderId, message,engTranslatedText);
//                model.setTimestamp(new Date().getTime());


                if (! Python.isStarted()) {
                    Python.start(new AndroidPlatform(ChatDetailActivity.this));
                }
                Python py = Python.getInstance();
                PyObject pyobj =py.getModule("EmotionDetectionScript");
                PyObject obj =pyobj.callAttr("main",engTranslatedText);


                Log.i("TAG", obj.asList().get(0).toString());

                emotionLable = obj.asList().get(0).toString();
                emotionScore =obj.asList().get(1).toFloat();
                //float emotionScoreRounded= Math.round(emotionScore*10000)/100;
                emotionScore=emotionScore*100;

                final MessageModel model = new MessageModel(senderId, message,engTranslatedText,emotionLable,emotionScore);
                model.setTimestamp(new Date().getTime());






                binding.enterMessage.setText("");


                database.getReference().child("Chats").child(senderRoom).push().setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {


                    @Override
                    public void onSuccess(Void unused) {


                        database.getReference().child("Chats").child(receiverRoom).push().setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {

                            @Override
                            public void onSuccess(Void unused) {

                            }
                        });
                    }
                });

            }
        });







    }


//    public void translate(String inputChatMessage, String languageCode) {
//
//        //Get input text to be translated:
//        originalText = inputChatMessage;
//        Translation translation = translate.translate(inputChatMessage, Translate.TranslateOption.targetLanguage(languageCode), Translate.TranslateOption.model("base"));
//
//
//        //Translated text and original text are set to TextViews:
//
//        engTranslatedText = translation.getTranslatedText();
//        Log.i("TAG", "translation success::::::"+engTranslatedText);
//
//    }

}