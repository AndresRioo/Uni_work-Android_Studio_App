package edu.ub.pis2324.projecte.presentation.presentation.ui.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import edu.ub.pis2324.projecte.R;
import edu.ub.pis2324.projecte.presentation.domain.model.entities.Reserva;
import edu.ub.pis2324.projecte.presentation.domain.model.entities.Partit;
import edu.ub.pis2324.projecte.presentation.domain.model.entities.Pista;

public class ReservasRecyclerViewAdapter extends RecyclerView.Adapter<ReservasRecyclerViewAdapter.MyViewHolder> {

    private ArrayList<? extends Reserva> llistaReserves;


    /* Escoltador de clics als productes */
    private OnProductClickListener onProductClickListener;
    /* Interficie per retornar Product clicat a l’activitat */
    public interface OnProductClickListener {
        void onProductClick(Reserva reserva);
    }

    /* Constructor modificat amb escoltador */
    public ReservasRecyclerViewAdapter( OnProductClickListener onProductClickListener ){
        this.onProductClickListener = onProductClickListener;
    }

    // constructor vacio
    public ReservasRecyclerViewAdapter() {
    }








    @NonNull
    @Override
    public ReservasRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Darle a la vista el recycler view
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.recycler_view_reservas,parent,false);

        return new ReservasRecyclerViewAdapter.MyViewHolder(view,
                new MyViewHolder.OnItemPositionClickListener() {
                    @Override
                    public void onItemPositionClick(int position) {
                        Reserva product = llistaReserves.get(position);
                        onProductClickListener.onProductClick(product); //ferli arribar a la activitat
                    }});
    }

    @Override
    public void onBindViewHolder(@NonNull ReservasRecyclerViewAdapter.MyViewHolder holder, int position) {
        Log.d("reservesAdapter", "Position: " + position);
        Reserva reserva = llistaReserves.get(position);

        // Informació de la reserva
        holder.txtHora.setText(reserva.getHora() + ":00");
        holder.txtFecha.setText(reserva.getData());
        holder.txtEsport.setText(reserva.getEsport());

        // Definir el color en formato hexadecimal
        String colorVerdeReservaHex = "#90EE90";
        String colorAzulReservaHex = "#ADD8E6";

        // Convertir el color hexadecimal a un entero usando Color.parseColor()
        int colorVerde = Color.parseColor(colorVerdeReservaHex);
        int colorAzul = Color.parseColor(colorAzulReservaHex);

        if (reserva instanceof Partit) {
            holder.cardView.setBackgroundTintList(ColorStateList.valueOf(colorVerde));
        } else if (reserva instanceof Pista) {
            holder.cardView.setBackgroundTintList(ColorStateList.valueOf(colorAzul));
        }
    }

    @Override
    public int getItemCount() {
        // numero de elementos por pantalla
        return llistaReserves.size();
    }

    public void clearData() {
        llistaReserves.clear();
        notifyDataSetChanged();
    }

    public void updateData(ArrayList<? extends Reserva> newReserves) {
        llistaReserves = copyList(newReserves);
        notifyDataSetChanged();
    }

    private <T extends Reserva> ArrayList<T> copyList(ArrayList<T> src) {
        ArrayList<T> dest = new ArrayList<>();
        for (T item : src) {
            dest.add(item);
        }
        return dest;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        //ImageButton btnFlecha;
        TextView txtEsport;
        TextView txtFecha;
        TextView txtHora;
        CardView cardView;

        public interface OnItemPositionClickListener {
            void onItemPositionClick(int position);
        }

        // on create metodo
        public MyViewHolder(@NonNull View itemView, OnItemPositionClickListener onItemPositionClickListener) {
            super(itemView);

            //btnFlecha = itemView.findViewById(R.id.btnFlechaDerechaReservaRV);
            txtEsport = itemView.findViewById(R.id.txtDeporteReservaRV);
            txtFecha = itemView.findViewById(R.id.txtFechaReservaRV);
            txtHora = itemView.findViewById(R.id.txtHoraReservaRV);
            cardView = itemView.findViewById(R.id.cardViewRVreservas);

            itemView.setOnClickListener(v-> {
                int pos = getAdapterPosition(); // mètode la classe
                onItemPositionClickListener.onItemPositionClick(pos);
            });

        }
    }
}