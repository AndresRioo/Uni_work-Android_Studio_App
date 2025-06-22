package edu.ub.pis2324.projecte.presentation.presentation.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.Toast;

import java.util.Calendar;

import edu.ub.pis2324.projecte.R;
import edu.ub.pis2324.projecte.databinding.FragmentVisualitzarCalendariPartitBinding;

public class VisualitzarCalendariPartitFragment extends Fragment {
    private FragmentVisualitzarCalendariPartitBinding binding;
    private NavController navController;
    private String esport;

    public static VisualitzarCalendariPartitFragment newInstance() {
        return new VisualitzarCalendariPartitFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentVisualitzarCalendariPartitBinding.inflate(getLayoutInflater());
        Bundle bundle = getArguments();
        if (bundle != null) {
            esport = bundle.getString("esport");
        }
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

        binding.txtCalendariPartits.setText("Selecciona el dia del partit de " + esport);

        // Configurar el MaterialCalendarView (opcional)
        setupCalendar();
    }

    private void setupCalendar(){

        Calendar calendario = Calendar.getInstance();
        long hoyEnMilisegundos = calendario.getTimeInMillis();

        // Sumar 1 mes al tiempo actual
        calendario.add(Calendar.DAY_OF_YEAR, 7);

        // Obtener la fecha y hora después de sumar 2 meses
        long unMesDespues = calendario.getTimeInMillis();

        CalendarView calendarView = binding.calendarView;

        binding.calendarView.setMinDate(hoyEnMilisegundos);
        binding.calendarView.setMaxDate(unMesDespues);


        // Configurar el listener para el cambio de fecha
        calendarView.setOnDateChangeListener((view,year, month, dayOfMonth) -> {
            // Mostrar el día seleccionado en un Toast
            String selectedDate1 = dayOfMonth + "/" + (month + 1) + "/" + year;
            String selectedDate2 = String.format("%02d%02d%02d", year % 100, month + 1, dayOfMonth);
            Toast.makeText(getContext(), "Dia seleccionat: " + selectedDate1, Toast.LENGTH_SHORT).show();

            Bundle bundle = new Bundle();
            bundle.putString("data", selectedDate2);
            bundle.putString("esport", esport);

            navController.navigate(R.id.action_CalendariPartit_to_VisualizarHorasPartido,bundle);

        });
    }

}