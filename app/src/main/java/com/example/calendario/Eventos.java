package com.example.calendario;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.github.johnpersano.supertoasts.library.Style;
import com.github.johnpersano.supertoasts.library.SuperActivityToast;
import com.github.johnpersano.supertoasts.library.utils.PaletteUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Eventos#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Eventos extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Eventos() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Eventos.
     */
    // TODO: Rename and change types and number of parameters
    public static Eventos newInstance(String param1, String param2) {
        Eventos fragment = new Eventos();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_eventos, container, false);
    }

    EditText etTitulo, etDescripcion, etFecha;
    Button btnGuardar, btnVerTodosEvs, cancelar, btnEliminarTodos;
    ConstraintLayout datosEventos;
    TextView viewShadow;
    LinearLayout verEventos;
    int idTxt;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        datosEventos = view.findViewById(R.id.datosEventos);
        viewShadow = view.findViewById(R.id.viewShadow);

        datosEventos.setVisibility(View.INVISIBLE);

        etTitulo = view.findViewById(R.id.etTitulo);
        etDescripcion = view.findViewById(R.id.etDescripcion);
        etFecha = view.findViewById(R.id.etFecha);

        btnGuardar = view.findViewById(R.id.button);
        btnVerTodosEvs = view.findViewById(R.id.verTodosEvs);
        cancelar = view.findViewById(R.id.cancelar);
        btnEliminarTodos = view.findViewById(R.id.eliminarTodos);

        btnVerTodosEvs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Eventos.this.cargareEventos();
                datosEventos.setVisibility(View.VISIBLE);
                viewShadow.setAlpha(0.5f);
            }
        });

        SQLite sqLite = new SQLite(getContext(), "calendario", null, 1);

        btnEliminarTodos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                sqLite.borrarTodosEventos();
                                Eventos.this.cargareEventos();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("¿Quieres borrar todos los eventos?").setPositiveButton("Si", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();

            }
        });

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!etTitulo.getText().toString().isEmpty() || !etDescripcion.getText().toString().isEmpty() || !etFecha.getText().toString().isEmpty()){
                    SuperActivityToast.create(getActivity(), new Style(), Style.TYPE_PROGRESS_CIRCLE)
                            .setText("Guardando evento.")
                            .setDuration(Style.DURATION_VERY_SHORT)
                            .setFrame(Style.FRAME_LOLLIPOP)
                            .setColor(PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_DEEP_PURPLE))
                            .setAnimations(Style.ANIMATIONS_POP).show();

                    SuperActivityToast.create(getActivity(), new Style(), Style.TYPE_STANDARD)
                            .setText("Evento guardado.")
                            .setDuration(Style.DURATION_VERY_SHORT)
                            .setFrame(Style.FRAME_LOLLIPOP)
                            .setColor(PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_DEEP_PURPLE))
                            .setAnimations(Style.ANIMATIONS_POP).show();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            String titulo = etTitulo.getText().toString();
                            String fecha = etFecha.getText().toString();
                            String descripcion = etDescripcion.getText().toString();
                            sqLite.guardarEvento(titulo, fecha, descripcion);

                            etTitulo.setText("");
                            etDescripcion.setText("");
                            etFecha.setText("");
                        }
                    },1500);
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

        etFecha.setInputType(InputType.TYPE_NULL);
        etFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimeDialog(etFecha);
            }
        });

        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datosEventos.setVisibility(View.INVISIBLE);
                viewShadow.setAlpha(0f);
            }
        });
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

    private void cargareEventos(){
        SQLite sqLite = new SQLite(getContext(), "calendario", null, 1);
        verEventos = getView().findViewById(R.id.verEventos);
        verEventos.removeAllViews();
        SQLiteDatabase db = sqLite.getWritableDatabase();
        Cursor filasEventos = db.rawQuery("SELECT * FROM eventos",null);

        int calcOdd = 0;
        while (filasEventos.moveToNext()){
            //Una vez agregado, creamos unos parametros para asignarselos al imageView
            LinearLayout.LayoutParams lpTxt = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            //Ponemos el margen, usamos el | (int) ((int) X*getContext().getResources().getDisplayMetrics().density) | para convertilos a PX y podamos hacerlo bien
            lpTxt.setMargins(0, 0, 0 , 0);


            TextView txt = new TextView(getContext());

            txt.setLayoutParams(lpTxt);
            txt.setText(filasEventos.getString(1) + " - " + filasEventos.getString(2));

            txt.setTag(R.id.id,filasEventos.getInt(0));
            txt.setTag(R.id.titulo,filasEventos.getString(1));
            txt.setTag(R.id.fecha,filasEventos.getString(2));
            txt.setTag(R.id.descripcion,filasEventos.getString(3));

            txt.setTextColor(ContextCompat.getColor(getContext(), R.color.calendar_fg_dark_color));
            txt.setTextSize(24);

            if(calcOdd%2 == 0){
                txt.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.listTextColor));
            }else{
                txt.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.listTextColor3));
            }
            verEventos.post(new Runnable() {
                @Override
                public void run() {
                    txt.setPadding(10,10,verEventos.getMeasuredWidth()-txt.getMeasuredWidth()-10,10);
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
                                    Eventos.this.cargareEventos();
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
}