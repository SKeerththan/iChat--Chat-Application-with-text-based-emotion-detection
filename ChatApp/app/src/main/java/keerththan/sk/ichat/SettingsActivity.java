package keerththan.sk.ichat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.cloud.translate.Language;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import keerththan.sk.ichat.Models.Users;
import keerththan.sk.ichat.databinding.ActivitySettingsBinding;

public class SettingsActivity extends AppCompatActivity {


    Spinner spinner;
    // String userLanguage;
    ActivitySettingsBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    String translateLanguage;
    FirebaseStorage storage;
    List<String> languageList;
    List<Language> languages;
    Users users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        StrictMode.ThreadPolicy gfgPolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(gfgPolicy);
        Translate translate = TranslateOptions.newBuilder().setApiKey("AIzaSyCocDIugbfDFLZFHLqU6_5kh56EwSPi-eE").build().getService();

        getSupportActionBar().hide();
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();


        languages = translate.listSupportedLanguages();
        languageList = new ArrayList<String>();
        for (Language language : languages) {
            // System.out.printf("Name: %s, Code: %s\n", language.getName(), language.getCode());
            languageList.add(language.getName() + "-" + language.getCode());
        }


//        database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                 users = snapshot.getValue(Users.class);
//                languageList.add(users.getTranslateLanguage());
//
//
//                //languageList.add(0, users.getTranslateLanguage());
//                // userLanguage = users.getTranslateLanguage();
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
        //  Log.i("TAG","Your defaul language is " + users.getTranslateLanguage());


        binding.backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });


        binding.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!binding.etStatus.getText().toString().equals("") && !binding.txtUsername.getText().toString().equals("")) {


                    String status = binding.etStatus.getText().toString();
                    String username = binding.txtUsername.getText().toString();
                    //String translateLanguage = binding.translateLanguage.getText().toString();


                    HashMap<String, Object> obj = new HashMap<>();
                    obj.put("username", username);
                    obj.put("status", status);
                    obj.put("translateLanguage", translateLanguage);

                    database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).updateChildren(obj);

                    Toast.makeText(SettingsActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(SettingsActivity.this, "Please Enter Username and Status ", Toast.LENGTH_SHORT).show();
            }
        });


        database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users = snapshot.getValue(Users.class);
                Picasso.get().load(users.getProfilePic()).placeholder(R.drawable.avatar).into(binding.profileImage);

                binding.etStatus.setText(users.getStatus());
                binding.txtUsername.setText(users.getUsername());
                //x = users.getTranslateLanguage();


                Log.i("TAG", "Your defaul language is " + users.getTranslateLanguage());

                // languageList.add(0, users.getTranslateLanguage());
                // userLanguage = users.getTranslateLanguage();
                // languageList.add(0,users.getTranslateLanguage());

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        binding.plus.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                Log.d("success", "1");
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 25);
            }
        });
        languageList.add(0,"Choose a language..!");

        spinner = findViewById(R.id.languageDropdown);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, languageList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                translateLanguage = adapterView.getItemAtPosition(i).toString();

                //adapterView.getItemAtPosition(i).equals("Choose City");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data.getData() != null) {
            Uri sFile = data.getData();
            binding.profileImage.setImageURI(sFile);

            final StorageReference reference = storage.getReference().child("profile_pic").child(FirebaseAuth.getInstance().getUid());
            reference.putFile(sFile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).child("profilePic").setValue(uri.toString());


                        }

                    });
                }
            });
        }
    }


}