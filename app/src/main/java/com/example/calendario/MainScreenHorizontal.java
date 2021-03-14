package com.example.calendario;

import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.github.johnpersano.supertoasts.library.Style;
import com.github.johnpersano.supertoasts.library.SuperActivityToast;
import com.github.johnpersano.supertoasts.library.utils.PaletteUtils;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.HorizontalCalendarView;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainScreenHorizontal#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainScreenHorizontal extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MainScreenHorizontal() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainScreen.
     */
    // TODO: Rename and change types and number of parameters
    public static MainScreenHorizontal newInstance(String param1, String param2) {
        MainScreenHorizontal fragment = new MainScreenHorizontal();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_screen_horizontal, container, false);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        MenuInflater menuInflater = inflater;
        menuInflater.inflate(R.menu.main_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }

    HorizontalCalendar horizontalCalendar;
    LinearLayout verEventos;
    ConstraintLayout datosEventos;
    int ano, mes, dia, idTxt;
    Calendar calendar;
    private final String TAG = "TAG:::";
    TextView viewShadow;
    EditText etTituloEv, etFechaEv, etDescEv;
    Button modificar, cancelar;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        calendar = Calendar.getInstance();

        datosEventos = view.findViewById(R.id.datosEventos);
        viewShadow = view.findViewById(R.id.viewShadow);

        etTituloEv = view.findViewById(R.id.etTitulo);
        etFechaEv = view.findViewById(R.id.etFecha);
        etDescEv = view.findViewById(R.id.etDescripcion);

        modificar = view.findViewById(R.id.modificar);
        cancelar = view.findViewById(R.id.cancelar);

        datosEventos.setVisibility(View.INVISIBLE);

        ano = calendar.get(Calendar.YEAR);
        mes = calendar.get(Calendar.MONTH) + 1;
        dia = calendar.get(Calendar.DAY_OF_MONTH);

        etFechaEv.setInputType(InputType.TYPE_NULL);
        etFechaEv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimeDialog(etFechaEv);
            }
        });

        this.cargareEventos();
        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.MONTH, -1);

        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.MONTH, 1);

        horizontalCalendar = new HorizontalCalendar.Builder(view, R.id.calendarView)
                .range(startDate, endDate)
                .datesNumberOnScreen(5)
                .build();


        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {
                ano = date.get(Calendar.YEAR);
                mes = date.get(Calendar.MONTH) + 1;
                dia = date.get(Calendar.DAY_OF_MONTH);

                MainScreenHorizontal.this.cargareEventos();
            }
        });

        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datosEventos.setVisibility(View.INVISIBLE);
                viewShadow.setAlpha(0f);
            }
        });

        modificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLite sqLite = new SQLite(getContext(), "calendario", null, 1);
                if(!etTituloEv.getText().toString().isEmpty() || !etDescEv.getText().toString().isEmpty() || !etFechaEv.getText().toString().isEmpty()){

                    String titulo = etTituloEv.getText().toString();
                    String fecha = etFechaEv.getText().toString();
                    String descripcion = etDescEv.getText().toString();
                    sqLite.modificarEvento(idTxt,titulo, fecha, descripcion);

                    etTituloEv.setText("");
                    etDescEv.setText("");
                    etFechaEv.setText("");

                    SuperActivityToast.create(getActivity(), new Style(), Style.TYPE_STANDARD)
                            .setText("Evento modificado.")
                            .setDuration(Style.DURATION_VERY_SHORT)
                            .setFrame(Style.FRAME_LOLLIPOP)
                            .setColor(PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_DEEP_PURPLE))
                            .setAnimations(Style.ANIMATIONS_POP).show();

                    datosEventos.setVisibility(View.INVISIBLE);
                    viewShadow.setAlpha(0f);

                    MainScreenHorizontal.this.cargareEventos();

                }else{
                    SuperActivityToast.create(getActivity(), new Style(), Style.TYPE_STANDARD)
                            .setText("Debes rellenar todos los campos.")
                            .setDuration(Style.DURATION_VERY_SHORT)
                            .setFrame(Style.FRAME_LOLLIPOP)
                            .setColor(PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_RED))
                            .setAnimations(Style.ANIMATIONS_POP).show();
                }
            }
        });


    }

    private void cargareEventos(){
        SQLite sqLite = new SQLite(getContext(), "calendario", null, 1);
        verEventos = getView().findViewById(R.id.verEventos);
        verEventos.removeAllViews();
        SQLiteDatabase db = sqLite.getWritableDatabase();
        Cursor filasEventos = db.rawQuery("SELECT * FROM eventos WHERE fecha LIKE '"+ ano + "-"+ mes + "-"+ dia + "%'",null);

        int calcOdd = 0;
        while (filasEventos.moveToNext()){
            //Una vez agregado, creamos unos parametros para asignarselos al imageView
            LinearLayout.LayoutParams lpTxt = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            //Ponemos el margen, usamos el | (int) ((int) X*getContext().getResources().getDisplayMetrics().density) | para convertilos a PX y podamos hacerlo bien
            lpTxt.setMargins(0, 0, 0 , 0);


            TextView txt = new TextView(getContext());

            txt.setLayoutParams(lpTxt);
            txt.setText(filasEventos.getString(1) + " - " + filasEventos.getString(2).split(" ")[1]);

            txt.setTag(R.id.id,filasEventos.getInt(0));
            txt.setTag(R.id.titulo,filasEventos.getString(1));
            txt.setTag(R.id.fecha,filasEventos.getString(2));
            txt.setTag(R.id.descripcion,filasEventos.getString(3));

            txt.setTextColor(ContextCompat.getColor(getContext(), R.color.calendar_fg_dark_color));
            txt.setTextSize(24);

            if(calcOdd%2 == 0){
                txt.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.listTextColor));
            }else{
                txt.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.listTextColor2));
            }
            verEventos.post(new Runnable() {
                @Override
                public void run() {
                    txt.setPadding(10,10,verEventos.getMeasuredWidth()-txt.getMeasuredWidth()-10,10);
                }
            });

            txt.setOnClickListener(new View.OnClickListener() {
                String titulo = (String) txt.getTag(R.id.titulo);
                String fecha = (String) txt.getTag(R.id.fecha);
                String descripcion = (String) txt.getTag(R.id.descripcion);
                @Override
                public void onClick(View v) {
                    idTxt = (int) txt.getTag(R.id.id);
                    datosEventos.setVisibility(View.VISIBLE);
                    viewShadow.setAlpha(0.5f);
                    etTituloEv.setText(titulo);
                    etDescEv.setText(descripcion);
                    etFechaEv.setText(fecha);

                }
            });

            txt.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    idTxt = (int) txt.getTag(R.id.id);
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    sqLite.borrarEvento(idTxt);
                                    MainScreenHorizontal.this.cargareEventos();
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    //No button clicked
                                    break;
                            }
                        }
                    };
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("¿Quieres borrar este evento?").setPositiveButton("Si", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();
                    return true;
                }
            });

            verEventos.addView(txt);
            calcOdd++;
        }
    }

    private void showDateTimeDialog(final EditText date_time_in) {
        final Calendar calendar= Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR,year);
                calendar.set(Calendar.MONTH,month);
                calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);

                TimePickerDialog.OnTimeSetListener timeSetListener=new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
                        calendar.set(Calendar.MINUTE,minute);

                        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-M-d HH:mm");

                        date_time_in.setText(simpleDateFormat.format(calendar.getTime()));
                    }
                };

                new TimePickerDialog(getContext(),timeSetListener,calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),false).show();
            }
        };

        new DatePickerDialog(getContext(),dateSetListener,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();

    }




}