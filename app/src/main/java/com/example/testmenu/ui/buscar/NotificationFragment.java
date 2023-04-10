package com.example.testmenu.ui.buscar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.testmenu.databinding.FragmentNotificarBinding;

public class NotificationFragment extends Fragment {
    /*Declaramos su Xml correspondiente a traves del ViewBiding*/

    private FragmentNotificarBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        /*Instanciamos la clase ViewModel correspondiente*/
        NotificarViewModel notificarViewModel =
                new ViewModelProvider(this).get(NotificarViewModel.class);

    /*Una vez inflado, con el metodo getRoot() podemos concretar los identificadores de nuestro diseño biding*/
        binding = FragmentNotificarBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textNotifications;
        notificarViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}