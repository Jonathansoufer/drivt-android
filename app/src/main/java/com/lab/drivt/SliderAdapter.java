package com.lab.drivt;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.Objects;

public class SliderAdapter extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;

    public SliderAdapter(Context context) {
        this.context = context;
    }

    public int[] slide_images = {
            R.drawable.car,
            R.drawable.navigation,
            R.drawable.marketing,
            R.drawable.dont_miss_ride
    };

    public String[] slide_headings = {
            "BEM VINDO",
            "PRECISAMOS DE SUA PERMISSÃO",
            "AGORA É CONOSCO",
            "NUNCA PERCA UMA CORRIDA"
    };

    public String[] slide_descs = {
            "Muito obrigado por escolher-nos como seu assistente pessoal de corridas. Conosco, não perde uma única corrida.",
            "Para poder extrair o máximo de nossa aplicativo, pedimos que nos dê acesso a localização e habilite a acessibilidade",
            "Já temos tudo o que precisamos para poder ser o seu parceiro nas corridas diárias",
            "Agora cada um faz o seu melhor. Nós lidamos com os vários aplicativos e você em conduzir e oferecer uma experiência singular para o seu passageiro."
//            context.getString(R.string.slide1Description),
//            context.getString(R.string.slide2Description),
//            context.getString(R.string.slide3Description),
//            context.getString(R.string.slide4Description),
    };

    @Override
    public int getCount() {
        return slide_headings.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (RelativeLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert layoutInflater != null;
        View view = layoutInflater.inflate(R.layout.slide_layout, container, false);

        ImageView slideImageView = (ImageView) view.findViewById(R.id.slide_image);
        TextView slideHeadings = (TextView) view.findViewById(R.id.slide1Title);
        TextView slideDescription = (TextView) view.findViewById(R.id.slideDescription);

        slideImageView.setImageResource(slide_images[position]);
        slideHeadings.setText(slide_headings[position]);
        slideDescription.setText(slide_descs[position]);

        container.addView(view);

        return view;
    };

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout)object);
    }
}
