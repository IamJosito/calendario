package com.example.calendario;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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
    Button btnGuardar;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etTitulo = view.findViewById(R.id.etTitulo);
        etDescripcion = view.findViewById(R.id.etDescripcion);
        etFecha = view.findViewById(R.id.etFecha);
        btnGuardar = view.findViewById(R.id.button);

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
                        etTitulo.setText("");
                        etDescripcion.setText("");
                        etFecha.setText("");
                    }
                },1500);

            }
        });

        etFecha.setInputType(InputType.TYPE_NULL);
        etFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimeDialog(etFecha);
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

                        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yy-MM-dd HH:mm");

                        date_time_in.setText(simpleDateFormat.format(calendar.getTime()));
                    }
                };

                new TimePickerDialog(getContext(),timeSetListener,calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),false).show();
            }
        };

        new DatePickerDialog(getContext(),dateSetListener,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();

    }
}