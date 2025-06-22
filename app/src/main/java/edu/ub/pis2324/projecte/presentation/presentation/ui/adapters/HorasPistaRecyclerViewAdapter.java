package edu.ub.pis2324.projecte.presentation.presentation.ui.adapters;

import android.content.Context;
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
import edu.ub.pis2324.projecte.presentation.domain.model.entities.Pista;
import edu.ub.pis2324.projecte.presentation.domain.model.entities.Reserva;

public class HorasPistaRecyclerViewAdapter extends RecyclerView.Adapter<HorasPistaRecyclerViewAdapter.MyViewHolder> {


    private ArrayList<Pista> llistaPistes;

    /* Escoltador de clics als productes */
    private HorasPistaRecyclerViewAdapter.OnProductClickListener onProductClickListener;

    /* Interficie per retornar Product clicat a l’activitat */
    public interface OnProductClickListener {
        void onProductClick(Pista pista);
    }

    /* Constructor modificat amb escoltador */
    public HorasPistaRecyclerViewAdapter( HorasPistaRecyclerViewAdapter.OnProductClickListener onProductClickListener ){
        this.onProductClickListener = onProductClickListener;
    }

    // constructor vacio
    public HorasPistaRecyclerViewAdapter() {
    }

    @NonNull
    @Override
    public HorasPistaRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Darle a la vista el recycler view

        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.recycler_view_pistahora,parent,false);

        return new HorasPistaRecyclerViewAdapter.MyViewHolder(view,
                new HorasPistaRecyclerViewAdapter.MyViewHolder.OnItemPositionClickListener() {
                    @Override
                    public void onItemPositionClick(int position) {
                        Pista product = llistaPistes.get(position);
                        onProductClickListener.onProductClick(product); //ferli arribar a la activitat
                    }});

    }

    @Override
    public void onBindViewHolder(@NonNull HorasPistaRecyclerViewAdapter.MyViewHolder holder, int position) {
        Log.d("pistesAdapter", "Position: " + position);
        Pista pista = llistaPistes.get(position);

        // Informació de la pista
        holder.txtHora.setText(pista.getHora() + ":00");

    }


    public void clearData() {
        llistaPistes.clear();
        notifyDataSetChanged();
    }

    public void updateData(ArrayList<Pista> newReserves) {
        llistaPistes = copyList(newReserves);
        notifyDataSetChanged();
    }

    private <T extends Reserva> ArrayList<T> copyList(ArrayList<T> src) {
        ArrayList<T> dest = new ArrayList<>();
        for (T item : src) {
            dest.add(item);
        }
        return dest;
    }


    @Override
    public int getItemCount() {
        // numero de elementos por pantalla
        return llistaPistes.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView txtHora;
        CardView cardView;

        public interface OnItemPositionClickListener {
            void onItemPositionClick(int position);
        }

        // on create metodo
        public MyViewHolder(@NonNull View itemView, HorasPistaRecyclerViewAdapter.MyViewHolder.OnItemPositionClickListener onItemPositionClickListener) {
            super(itemView);

            txtHora = itemView.findViewById(R.id.txtHoraPistaRV);
            cardView = itemView.findViewById(R.id.cardViewRVPista);

            itemView.setOnClickListener(v-> {
                int pos = getAdapterPosition(); // mètode la classe
                onItemPositionClickListener.onItemPositionClick(pos);
            });

        }
    }
}