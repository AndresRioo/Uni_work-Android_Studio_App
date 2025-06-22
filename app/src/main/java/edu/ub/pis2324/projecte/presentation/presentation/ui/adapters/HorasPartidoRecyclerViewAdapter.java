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
import edu.ub.pis2324.projecte.presentation.domain.model.entities.Partit;
import edu.ub.pis2324.projecte.presentation.domain.model.entities.Pista;
import edu.ub.pis2324.projecte.presentation.domain.model.entities.Reserva;

public class HorasPartidoRecyclerViewAdapter extends RecyclerView.Adapter<HorasPartidoRecyclerViewAdapter.MyViewHolder> {

    private ArrayList<Partit> llistaPartits;


    /* Escoltador de clics als productes */
    private HorasPartidoRecyclerViewAdapter.OnProductClickListener onProductClickListener;

    /* Interficie per retornar Product clicat a l’activitat */
    public interface OnProductClickListener {
        void onProductClick(Partit partit);
    }

    /* Constructor modificat amb escoltador */
    public HorasPartidoRecyclerViewAdapter( HorasPartidoRecyclerViewAdapter.OnProductClickListener onProductClickListener ){
        this.onProductClickListener = onProductClickListener;
    }

    // constructor vacio
    public HorasPartidoRecyclerViewAdapter() {
    }

    @NonNull
    @Override
    public HorasPartidoRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Darle a la vista el recycler view

        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.recycler_view_partidohora,parent,false);

        return new HorasPartidoRecyclerViewAdapter.MyViewHolder(view, new HorasPartidoRecyclerViewAdapter.MyViewHolder.OnItemPositionClickListener() {
            @Override
            public void onItemPositionClick(int position) {
                Partit product = llistaPartits.get(position);
                onProductClickListener.onProductClick(product); //ferli arribar a la activitat
            }});
    }

    @Override
    public void onBindViewHolder(@NonNull HorasPartidoRecyclerViewAdapter.MyViewHolder holder, int position) {
        Log.d("partitsAdapter", "Position: " + position);
        Partit partit = llistaPartits.get(position);

        // Informació del partit
        holder.txtHora.setText(partit.getHora() + ":00");
        holder.txtNivell.setText("Nivell: " + partit.getNivellDificultat());
        holder.txtParticipantes.setText(partit.getParticipantsActuals() + " / " + partit.getMaxParticipants());


    }

    @Override
    public int getItemCount() {
        // numero de elementos por pantalla
        return llistaPartits.size();
    }

    public void clearData() {
        llistaPartits.clear();
        notifyDataSetChanged();
    }

    public void updateData(ArrayList<Partit> newReserves) {
        llistaPartits = copyList(newReserves);
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

        TextView txtHora;
        TextView txtParticipantes;
        TextView txtNivell;
        CardView cardView;

        public interface OnItemPositionClickListener {
            void onItemPositionClick(int position);
        }


        // on create metodo
        public MyViewHolder(@NonNull View itemView, HorasPartidoRecyclerViewAdapter.MyViewHolder.OnItemPositionClickListener onItemPositionClickListener) {
            super(itemView);

            txtParticipantes = itemView.findViewById(R.id.txtParticipantsPartitRV);
            txtNivell = itemView.findViewById(R.id.txtNivellPartitRV);
            txtHora = itemView.findViewById(R.id.txtHoraPartitRV);
            cardView = itemView.findViewById(R.id.cardViewRVPartido);

            itemView.setOnClickListener(v-> {
                int pos = getAdapterPosition(); // mètode la classe
                onItemPositionClickListener.onItemPositionClick(pos);
            });

        }
    }
}