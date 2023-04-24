package com.example.testmenu.activities.fragmentMenu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.testmenu.databinding.FragmentInicioBinding;


public class InicioFragment extends Fragment {
    /*Declaramos su Xml correspondiente a traves del ViewBiding*/

    private FragmentInicioBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

//        /*Instanciamos la clase ViewModel correspondiente*/
//        InicioViewModel inicioViewModel =
//                new ViewModelProvider(this).get(InicioViewModel.class);

        /*Una vez inflado, con el metodo getRoot() podemos concretar los identificadores de nuestro diseño biding*/
        binding = FragmentInicioBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

      //  final TextView textView = binding.textInicio;
//        inicioViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
