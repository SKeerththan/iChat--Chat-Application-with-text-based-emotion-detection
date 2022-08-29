package keerththan.sk.ichat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

import keerththan.sk.ichat.Adapter.ChatAdapter;
import keerththan.sk.ichat.Models.MessageModel;
import keerththan.sk.ichat.databinding.ActivityGroupChatBinding;

public class GroupChatActivity extends AppCompatActivity {

    ActivityGroupChatBinding binding;
    public static String  engTranslatedText="";
    String emotionLable="";
    Float emotionScore=0.00F;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityGroupChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());




        getSupportActionBar().hide();

        binding.backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupChatActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final ArrayList<MessageModel> messageModels = new ArrayList<>();





        String receiverId = getIntent().getStringExtra("userId");
        final ChatAdapter chatAdapter = new ChatAdapter(messageModels, this, receiverId);





        final String senderId = FirebaseAuth.getInstance().getUid();
        binding.userName.setText("World Chat");

        final ChatAdapter adapter = new ChatAdapter(messageModels, this);
        binding.chatRecyclerView.setAdapter(adapter);


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.chatRecyclerView.setLayoutManager(layoutManager);

        database.getReference().child("World Chat").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageModels.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    MessageModel model = dataSnapshot.getValue(MessageModel.class);
                    messageModels.add(model);

                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        binding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String messsage = binding.enterMessage.getText().toString();

                chatAdapter.translateToEnglishGroupchat(messsage);
                if (! Python.isStarted()) {
                    Python.start(new AndroidPlatform(GroupChatActivity.this));
                }
                Python py = Python.getInstance();
                PyObject pyobj =py.getModule("EmotionDetectionScript");
                PyObject obj =pyobj.callAttr("main",engTranslatedText);
                emotionLable = obj.asList().get(0).toString();
                emotionScore =obj.asList().get(1).toFloat();
                //float emotionScoreRounded= Math.round(emotionScore*10000)/100;
                emotionScore=emotionScore*100;


                final MessageModel model = new MessageModel(senderId ,messsage, engTranslatedText,emotionLable,emotionScore);
                model.setTimestamp(new Date().getTime());

                binding.enterMessage.setText("");
                database.getReference().child("World Chat").push().setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(GroupChatActivity.this, "Message Sent.", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });


    }
}