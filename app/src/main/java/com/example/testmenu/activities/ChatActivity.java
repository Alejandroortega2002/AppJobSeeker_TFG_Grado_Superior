package com.example.testmenu.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.example.testmenu.R;
import com.example.testmenu.entidades.Chat;
import com.example.testmenu.firebase.ChatsFirebase;

import java.util.Date;

public class ChatActivity extends AppCompatActivity {
    String mExtraIdUser1;
    String mExtraIdUser2;
    ChatsFirebase mChatsFirebase;
    View mActionBarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        showCustomToolbar(R.layout.custom_chat_toolbar);
        mExtraIdUser1 = getIntent().getStringExtra("idUser1");
        mExtraIdUser2 = getIntent().getStringExtra("idUser2");
        mChatsFirebase = new ChatsFirebase();

        createChat();
    }

    private void showCustomToolbar(int resource) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle("");
        actionbar.setDisplayShowHomeEnabled(true);
        actionbar.setDisplayShowCustomEnabled(true);
        LayoutInflater inflater =(LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mActionBarView = inflater.inflate(resource,null);
        actionbar.setCustomView(mActionBarView);
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