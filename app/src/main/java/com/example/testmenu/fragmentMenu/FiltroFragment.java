package com.example.testmenu.fragmentMenu;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.testmenu.activities.SectoresActivity;
import com.example.testmenu.databinding.FragmentFiltroBinding;

public class FiltroFragment extends Fragment {

    private FragmentFiltroBinding binding;

    private CardView educacion, deporte, comercio, alimentacion, ade, transporte, construccion, limpieza, informatica, servicios;

    private TextView txtSEducacion,txtSActFD,txtSComercio,txtSAlimentacionYHost,txtSAdministracionYGestion,txtSTransporte,txtSIndustriaYConstruccion,txtSLimpieza,txtSInformatica,txtSServicios;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

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
        txtSEducacion = binding.txtSEducacion;
        txtSActFD = binding.txtSActFD;
        txtSComercio = binding.txtSComercio;
        txtSAlimentacionYHost = binding.txtSAlimentacionYHost;
        txtSAdministracionYGestion = binding.txtSAdministracionYGestion;
        txtSTransporte = binding.txtSTransporte;
        txtSIndustriaYConstruccion = binding.txtSIndustriaYConstruccion;
        txtSLimpieza = binding.txtSLimpieza;
        txtSInformatica = binding.txtSInformatica;
        txtSServicios = binding.txtSServicios;

        educacion.setOnClickListener(view -> {
            pasarDeActivity(txtSEducacion.getText().toString());
        });

        deporte.setOnClickListener(view -> {
            pasarDeActivity(txtSActFD.getText().toString());
        });

        comercio.setOnClickListener(view -> {
            pasarDeActivity(txtSComercio.getText().toString());
        });

        alimentacion.setOnClickListener(view -> {
            pasarDeActivity(txtSAlimentacionYHost.getText().toString());
        });

        ade.setOnClickListener(view -> {
            pasarDeActivity(txtSAdministracionYGestion.getText().toString());
        });

        transporte.setOnClickListener(view -> {
            pasarDeActivity(txtSTransporte.getText().toString());
        });

        construccion.setOnClickListener(view -> {
            pasarDeActivity(txtSIndustriaYConstruccion.getText().toString());
        });

        limpieza.setOnClickListener(view -> {
            pasarDeActivity(txtSLimpieza.getText().toString());
        });

        informatica.setOnClickListener(view -> {
            pasarDeActivity(txtSInformatica.getText().toString());
        });

        servicios.setOnClickListener(view -> {
            pasarDeActivity(txtSServicios.getText().toString());
        });

        return root;
    }

    public void pasarDeActivity(String sector){
        Intent intent = new Intent(getContext(), SectoresActivity.class);
        intent.putExtra("sector", sector);
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}