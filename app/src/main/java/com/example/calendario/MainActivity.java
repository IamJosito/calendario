package com.example.calendario;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PorterDuff;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Calendar;
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

    int ano, mes, dia;
    Calendar calendar;
    @Override
    protected void onStop() {
        super.onStop();

        calendar = Calendar.getInstance();
        ano = calendar.get(Calendar.YEAR);
        mes = calendar.get(Calendar.MONTH) + 1;
        dia = calendar.get(Calendar.DAY_OF_MONTH);

        SQLite sqLite = new SQLite(getApplicationContext(), "calendario", null, 1);
        SQLiteDatabase db = sqLite.getWritableDatabase();
        Cursor filasEventos = db.rawQuery("SELECT * FROM eventos WHERE fecha LIKE '"+ ano + "-"+ mes + "-"+ dia + "%'",null);
        String eventosHoy = "";

        int numEventos = 0;
        while(filasEventos.moveToNext()){
            eventosHoy += filasEventos.getString(1) + " ";
            eventosHoy += filasEventos.getString(2).split(" ")[1];
            numEventos++;
            if(numEventos < filasEventos.getCount()){
                eventosHoy +=", ";
            }
        }

        if(!eventosHoy.equals("")){
            int reqCode = 1;
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            showNotification(this, "Eventos hoy", "Tienes: " + eventosHoy, intent, reqCode);
        }

    }

    public void showNotification(Context context, String title, String message, Intent intent, int reqCode) {
        PendingIntent pendingIntent = PendingIntent.getActivity(context, reqCode, intent, PendingIntent.FLAG_ONE_SHOT);
        String CHANNEL_ID = "channel_name";// The id of the channel.
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message));
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Channel Name";// The user-visible name of the channel.
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            notificationManager.createNotificationChannel(mChannel);
        }
        notificationManager.notify(reqCode, notificationBuilder.build()); // 0 is the request code, it should be unique id

        Log.d("showNotification", "showNotification: " + reqCode);
    }
}