package com.example.testmenu.activities.fragmentMenu;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.testmenu.R;
import com.example.testmenu.activities.PostActivity;
import com.example.testmenu.databinding.FragmentInicioBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class InicioFragment extends Fragment {
    View mView;
    FloatingActionButton mFab;

    public InicioFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_inicio, container, false);
        mFab = mView.findViewById(R.id.fab);
        mFab.setOnClickListener(view -> goToPost());

        return mView;

    }

    private void goToPost() {
        Intent intent = new Intent(getContext(), PostActivity.class);
        startActivity(intent);
    }
}