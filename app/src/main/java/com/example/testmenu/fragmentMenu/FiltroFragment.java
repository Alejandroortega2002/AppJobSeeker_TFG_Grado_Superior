package com.example.testmenu.fragmentMenu;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.testmenu.activities.SectoresActivity;
import com.example.testmenu.databinding.FragmentFiltroBinding;

public class FiltroFragment extends Fragment {
    /*Declaramos su Xml correspondiente a traves del ViewBiding*/

    private FragmentFiltroBinding binding;

    private CardView educacion, deporte, comercio, alimentacion, ade, transporte, construccion, limpieza, informatica, servicios;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
//        /*Instanciamos la clase ViewModel correspondiente*/
//        CreacionViewModel creacionViewModel =
//                new ViewModelProvider(this).get(CreacionViewModel.class);

        /*Una vez inflado, con el metodo getRoot() podemos concretar los identificadores de nuestro diseño biding*/
        binding = FragmentFiltroBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        educacion = binding.cardViewSEducacion;
        deporte = binding.cardViewSActFD;
        comercio = binding.cardViewSComercio;
        alimentacion = binding.cardViewSAlimentacionYHost;
        ade = binding.cardViewSAdministracionYGestion;
        transporte = binding.cardViewSTransporte;
        construccion = binding.cardViewSIndustriaYConstruccion;
        limpieza = binding.cardViewSLimpieza;
        informatica = binding.cardViewSInformatica;
        servicios = binding.cardViewSServicios;

        educacion.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), SectoresActivity.class);
            startActivity(intent);
        });

        deporte.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), SectoresActivity.class);
            startActivity(intent);
        });

        comercio.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), SectoresActivity.class);
            startActivity(intent);
        });

        alimentacion.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), SectoresActivity.class);
            startActivity(intent);
        });

        ade.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), SectoresActivity.class);
            startActivity(intent);
        });

        transporte.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), SectoresActivity.class);
            startActivity(intent);
        });

        construccion.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), SectoresActivity.class);
            startActivity(intent);
        });

        limpieza.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), SectoresActivity.class);
            startActivity(intent);
        });

        informatica.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), SectoresActivity.class);
            startActivity(intent);
        });

        servicios.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), SectoresActivity.class);
            startActivity(intent);
        });


//        final TextView textView = binding.textHome;
//        creacionViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}