package com.example.testmenu.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.testmenu.R;
import com.example.testmenu.entidades.Chat;
import com.example.testmenu.firebase.ChatsFirebase;

import java.util.Date;

public class ChatActivity extends AppCompatActivity {
    String mExtraIdUser1;
    String mExtraIdUser2;
    ChatsFirebase mChatsFirebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mExtraIdUser1 = getIntent().getStringExtra("idUser1");
        mExtraIdUser2 = getIntent().getStringExtra("idUser2");
        mChatsFirebase = new ChatsFirebase();

        createChat();
    }

    private void createChat(){
        Chat chat = new Chat();
        chat.setIdUser1(mExtraIdUser1);
        chat.setIdUser2(mExtraIdUser2);
        chat.setWriting(false);
        chat.setTimmestamp(new Date().getTime());
        mChatsFirebase.create(chat);

    }
}