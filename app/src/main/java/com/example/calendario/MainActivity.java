package com.example.calendario;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.google.android.material.tabs.TabLayout;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "TAG:::";
    TabLayout tableLayout;
    ViewPager viewPager;
    int yearHorizontal = 0, monthHorizontal = 0, dayHorizontal = 0;
    int yearNormal, monthNormal, dayNormal;

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

        Log.d(TAG, "EL MES ES: " + monthHorizontal);

        prepareViewPager(viewPager, arrayList);

        tableLayout.setupWithViewPager(viewPager);

        tableLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    getIntent().putExtra("horizontalYear", yearNormal);
                    getIntent().putExtra("horizontalMonth", monthNormal);
                    getIntent().putExtra("horizontalDay", dayNormal);
                }

                if (tab.getPosition() == 1) {
                    getIntent().putExtra("normalYear", yearHorizontal);
                    getIntent().putExtra("normalMonth", monthHorizontal);
                    getIntent().putExtra("normalDay", dayHorizontal);
                }

                getWindow().getDecorView().setBackgroundResource(android.R.color.white);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    yearHorizontal = getIntent().getIntExtra("horizontalYear", 0);
                    dayHorizontal = getIntent().getIntExtra("horizontalDay", 0);
                    monthHorizontal = getIntent().getIntExtra("horizontalMonth", 0);
                }

                if (tab.getPosition() == 1) {
                    yearNormal = getIntent().getIntExtra("normalYear", 0);
                    dayNormal = getIntent().getIntExtra("normalDay", 0);
                    monthNormal = getIntent().getIntExtra("normalMonth", 0);
                    Log.d(TAG, "onTabUnselected: " + yearNormal);
                }

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void prepareViewPager(ViewPager viewPager, ArrayList<String> arrayList) {
        MainAdapter adapter = new MainAdapter(getSupportFragmentManager());

        MainScreenHorizontal mainScreenHorizontal = new MainScreenHorizontal();
        MainScreenNormal mainScreenNormal = new MainScreenNormal();
        Eventos eventos = new Eventos();
        ListasCompra lista = new ListasCompra();

        adapter.addFragment(mainScreenHorizontal, arrayList.get(0));
        adapter.addFragment(mainScreenNormal, arrayList.get(1));
        adapter.addFragment(eventos, arrayList.get(2));
        adapter.addFragment(lista, arrayList.get(3));

        viewPager.setAdapter(adapter);


    }

    int ano, mes, dia;
    Calendar calendar;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onStop() {
        super.onStop();

        calendar = Calendar.getInstance();
        ano = calendar.get(Calendar.YEAR);
        mes = calendar.get(Calendar.MONTH) + 1;
        dia = calendar.get(Calendar.DAY_OF_MONTH);

        SQLite sqLite = new SQLite(getApplicationContext(), "calendario", null, 1);
        SQLiteDatabase db = sqLite.getWritableDatabase();

        try {
            Cursor todosEventos = db.rawQuery("SELECT * FROM eventos", null);
            todosEventos.moveToFirst();
            String fecha = todosEventos.getString(2);
            System.out.println(fecha);
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            Date fecha1 = format.parse(fecha);
            Date fecha2 = new Date();

            long differenceBtwDates = fecha1.getTime() - fecha2.getTime();
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

            Intent intent = new Intent(MainActivity.this, Reminder.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);

            alarmManager.set(AlarmManager.RTC_WAKEUP, differenceBtwDates, pendingIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}