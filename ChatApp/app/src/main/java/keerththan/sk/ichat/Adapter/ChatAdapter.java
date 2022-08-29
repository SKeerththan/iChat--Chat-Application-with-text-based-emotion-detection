package keerththan.sk.ichat.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import keerththan.sk.ichat.ChatDetailActivity;
import keerththan.sk.ichat.GroupChatActivity;
import keerththan.sk.ichat.Models.MessageModel;
import keerththan.sk.ichat.Models.Users;
import keerththan.sk.ichat.R;

public class ChatAdapter extends RecyclerView.Adapter {
    ArrayList<MessageModel> messageModels;
    Context context;
    String recId;
    FirebaseDatabase database;
    FirebaseAuth mAuth;

    Translate translate;

    int SENDER_VIEW_TYPE = 1;
    int RECEIVER_VIEW_TYPE = 2;
    private String originalText;
    private String translatedText;
    private boolean connected;

    public ChatAdapter(ArrayList<MessageModel> messageModels, Context context) {
        this.messageModels = messageModels;
        this.context = context;

    }

    public ChatAdapter(ArrayList<MessageModel> messageModels, Context context, String recId) {
        this.messageModels = messageModels;
        this.context = context;
        this.recId = recId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == SENDER_VIEW_TYPE) {
            View view = LayoutInflater.from(context).inflate(R.layout.sample_sender, parent, false);
            return new SenderViewHolder(view);

        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.sample_reciver, parent, false);
            return new RecieverViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (messageModels.get(position).getuId().equals(FirebaseAuth.getInstance().getUid())) {

            return SENDER_VIEW_TYPE;
        } else {
            {
                return RECEIVER_VIEW_TYPE;
            }
        }


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        MessageModel messageModel = messageModels.get(position);


        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                new AlertDialog.Builder(context).setTitle("Delete").setMessage("Are you sure you want to delete this message?").setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        String senderRoom = FirebaseAuth.getInstance().getUid() + recId;
                        database.getReference().child("Chats").child(senderRoom).child(messageModel.getMessageId()).setValue(null);

                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
                return false;
            }
        });


        if (holder.getClass() == SenderViewHolder.class) {
            ((SenderViewHolder) holder).senderMsg.setText(messageModel.getMessage());

            Date date = new Date(messageModel.getTimestamp());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("h:mm a");
            String strDate = simpleDateFormat.format(date);
            ((SenderViewHolder) holder).senderTime.setText(strDate);


        } else {

            database = FirebaseDatabase.getInstance();
            mAuth = FirebaseAuth.getInstance();


            database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Users users = snapshot.getValue(Users.class);
                    if (!users.getTranslateLanguage().isEmpty()) {
                        //((RecieverViewHolder) holder).recieverMsg.setText(translatedText);
                        if (checkInternetConnection()) {

                            //If there is internet connection, get translate service and start translation:
                            getTranslateService();
                            String[] languageCode = users.getTranslateLanguage().split("-");

                            translate(messageModel.getMessage(), languageCode[1]);
                            Log.i("TAG", "Connection success" + languageCode[1]);

                        } else {

                            //If not, display "no connection" warning:
                            Log.i("TAG", "Connection failure");
                        }

                        database.getReference().child("Users").child(messageModel.getuId()).child("username").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                ((RecieverViewHolder) holder).recieverMsg.setText(snapshot.getValue().toString()+": "+translatedText);
                                ((RecieverViewHolder) holder).emotionLableText.setText(messageModel.getSendMessageEmotionLable()+": "+(Math.round(messageModel.getSendMessageEmotionScore()*100)/100) +"% ");
                                ((RecieverViewHolder) holder).progressBar.setProgress(messageModel.getSendMessageEmotionScore().intValue());

                                Date date = new Date(messageModel.getTimestamp());
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("h:mm a");
                                String strDate = simpleDateFormat.format(date);
                                ((RecieverViewHolder) holder).receiverTime.setText(strDate);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });










//                        database.getReference().child("Chats").child(senderRoom).push().setValue(messageModel).addOnSuccessListener(new OnSuccessListener<Void>() {
//
//
//                            @Override
//                            public void onSuccess(Void unused) {
//
//
//                                database.getReference().child("Chats").child(receiverRoom).push().setValue(messageModel).addOnSuccessListener(new OnSuccessListener<Void>() {
//
//                                    @Override
//                                    public void onSuccess(Void unused) {
//
//                                    }
//                                });
//                            }
//                        });

                    } else {
                        ((RecieverViewHolder) holder).recieverMsg.setText(messageModel.getMessage());
                        Date date = new Date(messageModel.getTimestamp());
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("h:mm a");
                        String strDate = simpleDateFormat.format(date);
                        ((RecieverViewHolder) holder).receiverTime.setText(strDate);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }
    }


    public boolean checkInternetConnection() {

        //Check internet connection:
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        //Means that we are connected to a network (mobile or wi-fi)
        connected = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;

        return connected;
    }

    public void getTranslateService() {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //   InputStream is = context.getResources().openRawResource(R.raw.credentials);
        try (InputStream is = context.getResources().openRawResource(R.raw.credentials);) {


            //Get credentials:

            final GoogleCredentials myCredentials = GoogleCredentials.fromStream(is);

            //Set credentials and get translate service:
            TranslateOptions translateOptions = TranslateOptions.newBuilder().setCredentials(myCredentials).build();
            translate = translateOptions.getService();
            Log.i("TAG", "getTranslateService successfully finishes");

        } catch (IOException ioe) {
            ioe.printStackTrace();

        }
    }

    public void translate(String inputChatMessage, String languageCode) {

        //Get input text to be translated:
        originalText = inputChatMessage;
        Translation translation = translate.translate(inputChatMessage, Translate.TranslateOption.targetLanguage(languageCode), Translate.TranslateOption.model("base"));


        //Translated text and original text are set to TextViews:
        Log.i("TAG", "translation success");
        translatedText = translation.getTranslatedText();

    }

    public void translateToEnglish(String inputChatMessage) {
        checkInternetConnection();
        getTranslateService();

        //Get input text to be translated:
        String languageCode ="en";
        originalText = inputChatMessage;
        Translation translation = translate.translate(inputChatMessage, Translate.TranslateOption.targetLanguage(languageCode), Translate.TranslateOption.model("base"));


        //Translated text and original text are set to TextViews:
        Log.i("TAG", "translation success");
        ChatDetailActivity.engTranslatedText = translation.getTranslatedText();

    }
    public void translateToEnglishGroupchat(String inputChatMessage) {
        checkInternetConnection();
        getTranslateService();

        //Get input text to be translated:
        String languageCode ="en";
        originalText = inputChatMessage;
        Translation translation = translate.translate(inputChatMessage, Translate.TranslateOption.targetLanguage(languageCode), Translate.TranslateOption.model("base"));


        //Translated text and original text are set to TextViews:
        Log.i("TAG", "translation success");
        GroupChatActivity.engTranslatedText = translation.getTranslatedText();

    }


    @Override
    public int getItemCount() {
        return messageModels.size();
    }


    public class RecieverViewHolder extends RecyclerView.ViewHolder {
        TextView recieverMsg, receiverTime,emotionLableText;
        ProgressBar progressBar;

        public RecieverViewHolder(@NonNull View itemView) {
            super(itemView);
            recieverMsg = itemView.findViewById(R.id.reciverText);
            receiverTime = itemView.findViewById(R.id.receiverTime);
            emotionLableText=itemView.findViewById(R.id.emotionLableText);
            progressBar=itemView.findViewById(R.id.progressBar);

        }
    }

    public class SenderViewHolder extends RecyclerView.ViewHolder {
        TextView senderMsg, senderTime;

        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            senderMsg = itemView.findViewById(R.id.senderText);
            senderTime = itemView.findViewById(R.id.senderTime);

        }
    }


}
