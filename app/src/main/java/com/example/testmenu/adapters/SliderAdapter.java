package com.example.testmenu.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.testmenu.R;
import com.example.testmenu.entidades.SliderItem;
import com.smarteist.autoimageslider.SliderViewAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class SliderAdapter extends SliderViewAdapter<SliderAdapter.SliderAdapterVH> {

    private Context context;
    private List<SliderItem> mSliderItems = new ArrayList<>();

    public SliderAdapter(Context context, List<SliderItem> sliderItems) {
        this.context = context;
        mSliderItems = sliderItems;
    }


    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        // Inflar el layout slider_layout_item en una nueva vista
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_layout_item, parent, false);
        // Crear una nueva instancia de SliderAdapterVH y asignar la vista inflada
        SliderAdapterVH viewHolder = new SliderAdapterVH(view);
        // Obtener la referencia al ImageView en el layout
        viewHolder.imageViewSlider = view.findViewById(R.id.imageViewSlider);
        // Devolver la instancia de SliderAdapterVH
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(SliderAdapterVH viewHolder, final int position) {
        // Obtener el objeto SliderItem en la posición especificada
        SliderItem sliderItem = mSliderItems.get(position);
        // Verificar si la URL de la imagen no es nula y no está vacía
        if (sliderItem.getImageUrl() != null) {
            if (!sliderItem.getImageUrl().isEmpty()) {
                // Cargar la imagen desde la URL utilizando Picasso y asignarla al ImageView en el ViewHolder
                Picasso.get().load(sliderItem.getImageUrl()).into(viewHolder.imageViewSlider);
            }
        }
    }

    /**
     * Cuenta el tamano de los items
     *
     * @return mSliderItems valor del tamano
     */
    @Override
    public int getCount() {
        return mSliderItems.size();
    }

    class SliderAdapterVH extends SliderViewAdapter.ViewHolder {

        private View itemView;
        private ImageView imageViewSlider;


        public SliderAdapterVH(View itemView) {
            super(itemView);
            imageViewSlider = itemView.findViewById(R.id.imageSlider);
            this.itemView = itemView;
        }
    }

}
