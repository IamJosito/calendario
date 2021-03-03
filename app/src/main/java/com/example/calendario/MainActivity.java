package com.example.calendario;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.graphics.PorterDuff;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    TabLayout tableLayout;
    ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tableLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);

        ArrayList<String> arrayList = new ArrayList<>();

        arrayList.add("Calen. Horiz.");
        arrayList.add("Calen. Normal");
        arrayList.add("Eventos");
        arrayList.add("Lista de la compra");
        arrayList.add("Opciones");

        prepareViewPager(viewPager,arrayList);

        tableLayout.setupWithViewPager(viewPager);

    }

    private void prepareViewPager(ViewPager viewPager, ArrayList<String> arrayList) {
        MainAdapter adapter = new MainAdapter(getSupportFragmentManager());


        MainScreenHorizontal mainScreenHorizontal = new MainScreenHorizontal();
        MainScreenNormal mainScreenNormal = new MainScreenNormal();
        Eventos eventos = new Eventos();
        ListasCompra lista = new ListasCompra();

        adapter.addFragment(mainScreenHorizontal,arrayList.get(0));
        adapter.addFragment(mainScreenNormal,arrayList.get(1));
        adapter.addFragment(eventos,arrayList.get(2));
        adapter.addFragment(lista,arrayList.get(3));


        viewPager.setAdapter(adapter);


    }
}