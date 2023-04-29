package com.example.testmenu.fragmentMenu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.testmenu.databinding.FragmentChatBinding;

public class ChatFragment extends Fragment {
    /*Declaramos su Xml correspondiente a traves del ViewBiding*/

    private FragmentChatBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
//        /*Instanciamos la clase ViewModel correspondiente*/
//        ChatViewModel chatViewModel =
//                new ViewModelProvider(this).get(ChatViewModel.class);

    /*Una vez inflado, con el metodo getRoot() podemos concretar los identificadores de nuestro diseño biding*/
        binding = FragmentChatBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

      //   TextView textView = binding.textNotifications;
//        chatViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}