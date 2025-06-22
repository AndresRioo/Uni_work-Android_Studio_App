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

import edu.ub.pis2324.projecte.R;
import edu.ub.pis2324.projecte.databinding.FragmentOpcionsReservarBinding;

public class OpcionsReservarFragment extends Fragment {
    private FragmentOpcionsReservarBinding binding;
    private NavController navController;
    private String esport;

    public static OpcionsReservarFragment newInstance() {
        return new OpcionsReservarFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //return inflater.inflate(R.layout.fragment_reservar_o_unirse, container, false);
        binding = FragmentOpcionsReservarBinding.inflate(getLayoutInflater());
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

        /* Initializations */
        initWidgetListeners();
    }

    private void initWidgetListeners() {

        binding.btnUnirseAPartit.setOnClickListener(ignoredView -> {

            Bundle bundle = new Bundle();
            bundle.putString("esport", esport);
            navController.navigate(R.id.action_ReservarOUnirse_to_CalendariPartit, bundle);

        });

        binding.btnReservarUnaPista.setOnClickListener(ignoredView -> {

            Bundle bundle = new Bundle();
            bundle.putString("esport", esport);
            navController.navigate(R.id.action_ReservarOUnirse_to_CalendariPista, bundle);

        });
    }
}