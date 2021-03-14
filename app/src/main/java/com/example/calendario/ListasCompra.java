package com.example.calendario;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.github.johnpersano.supertoasts.library.Style;
import com.github.johnpersano.supertoasts.library.SuperActivityToast;
import com.github.johnpersano.supertoasts.library.utils.PaletteUtils;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListasCompra#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListasCompra extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ListasCompra() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ListasCompra.
     */
    // TODO: Rename and change types and number of parameters
    public static ListasCompra newInstance(String param1, String param2) {
        ListasCompra fragment = new ListasCompra();
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
        return inflater.inflate(R.layout.fragment_listas_compra, container, false);
    }

    Button btnGuardarProd, btnBorrarProd, btnBorrarTodosProd;
    EditText etNomProd;
    NumberPicker cantidadProd;
    LinearLayout verProductos;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        btnGuardarProd = view.findViewById(R.id.btnGuardarProd);
        btnBorrarProd = view.findViewById(R.id.btnBorrarProd);
        btnBorrarTodosProd = view.findViewById(R.id.btnBorrarTodosProds);
        etNomProd = view.findViewById(R.id.etNomProd);
        cantidadProd = view.findViewById(R.id.numberPicker);

        cantidadProd.setMinValue(1);
        cantidadProd.setMaxValue(999);


        SQLite sqLite = new SQLite(getContext(), "calendario", null, 1);

        this.recargarProdScroll();

        btnGuardarProd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!etNomProd.getText().toString().isEmpty()){
                    String nomProd = etNomProd.getText().toString().substring(0,1).toUpperCase() + etNomProd.getText().toString().substring(1).toLowerCase();
                    int cantidad = cantidadProd.getValue();
                    sqLite.guardarProducto(nomProd,cantidad);
                    ListasCompra.this.recargarProdScroll();
                }else{
                    SuperActivityToast.create(getActivity(), new Style(), Style.TYPE_STANDARD)
                            .setText("El nombre no puede estar vacío.")
                            .setDuration(Style.DURATION_VERY_SHORT)
                            .setFrame(Style.FRAME_LOLLIPOP)
                            .setColor(PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_RED))
                            .setAnimations(Style.ANIMATIONS_POP).show();
                }
            }
        });

        btnBorrarProd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(productos.size() > 0){
                    sqLite.borrarProductos(productos);
                    ListasCompra.this.recargarProdScroll();
                    productos.clear();
                }else{
                    SuperActivityToast.create(getActivity(), new Style(), Style.TYPE_STANDARD)
                            .setText("No hay productos seleccionados.")
                            .setDuration(Style.DURATION_VERY_SHORT)
                            .setFrame(Style.FRAME_LOLLIPOP)
                            .setColor(PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_RED))
                            .setAnimations(Style.ANIMATIONS_POP).show();
                }
            }
        });

        btnBorrarTodosProd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                sqLite.borrarTodosProductos();
                                ListasCompra.this.recargarProdScroll();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("¿Quieres borrar todos los productos?").setPositiveButton("Si", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        });
    }

    ArrayList<Integer> productos = new ArrayList<>();
    private void recargarProdScroll(){
        verProductos = getView().findViewById(R.id.verProductos);
        verProductos.removeAllViews();
        SQLite sqLite = new SQLite(getContext(), "calendario", null, 1);
        SQLiteDatabase db = sqLite.getWritableDatabase();
        Cursor filasProductos = db.rawQuery("SELECT * FROM listaCompra",null);

        while (filasProductos.moveToNext()){
            TextView txt = new TextView(getContext());
            txt.setText(filasProductos.getString(1) + " × " + filasProductos.getInt(2));
            txt.setTag(filasProductos.getInt(0));
            txt.setTextColor(ContextCompat.getColor(getContext(), R.color.calendar_fg_dark_color));
            txt.setTextSize(18);

            txt.setOnClickListener(new View.OnClickListener() {
                int clickCount = 0;
                @Override
                public void onClick(View v) {
                    int posicionDelProducto = 0;
                    if(clickCount == 0){
                        txt.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.selector_color));
                        clickCount = 1;
                        productos.add((int) txt.getTag());
                        posicionDelProducto = productos.size()-1;

                    }else{
                        txt.setBackgroundResource(0);
                        clickCount = 0;
                        productos.remove(posicionDelProducto);
                    }
                }
            });

            txt.setOnLongClickListener(new View.OnLongClickListener() {
                int clickCount = 0;
                @Override
                public boolean onLongClick(View v) {
                    if(clickCount == 0){
                        txt.setPaintFlags(txt.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        clickCount++;
                    }else{
                        txt.setPaintFlags(txt.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
                        clickCount--;
                    }
                    return true;
                }
            });
            verProductos.addView(txt);
        }
    }
}