package edu.ub.pis2324.projecte.presentation.presentation.ui.fragments;

import androidx.appcompat.widget.Toolbar;

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
import edu.ub.pis2324.projecte.databinding.FragmentVisualitzarEsportsBinding;

public class VisualitzarEsportsFragment extends Fragment {
    //private AppContainer appContainer;
    private NavController navController;
    private FragmentVisualitzarEsportsBinding binding;

    public static VisualitzarEsportsFragment newInstance() {
        return new VisualitzarEsportsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //return inflater.inflate(R.layout.fragment_visualitzar_esports, container, false);

        binding = FragmentVisualitzarEsportsBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //appContainer = ((MyApplication) getActivity().getApplication()).getAppContainer();
        navController = Navigation.findNavController(view);

        /* Initializations */
        initWidgetListeners();
    }

    private void initWidgetListeners() {

        binding.btnBasquet.setOnClickListener(ignoredView -> {
            String esport = "BASQUET";

            // Set the title of the toolbar
            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                if (destination.getId() == R.id.ReservarOUnirse) {
                    Toolbar toolbar = requireActivity().findViewById(R.id.toolbar);
                    if (toolbar != null) {
                        toolbar.setTitle(esport);
                    }
                }
            });

            Bundle bundle = new Bundle();
            bundle.putString("esport", esport);

            navController.navigate(R.id.action_VisualitzaEsports_to_ReservarOUnirse, bundle);
        });

        binding.btnTenis.setOnClickListener(ignoredView -> {
            String esport = "TENNIS";

            // Set the title of the toolbar
            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                if (destination.getId() == R.id.ReservarOUnirse) {
                    Toolbar toolbar = requireActivity().findViewById(R.id.toolbar);
                    if (toolbar != null) {
                        toolbar.setTitle(esport);
                    }
                }
            });

            Bundle bundle = new Bundle();
            bundle.putString("esport", esport);

            navController.navigate(R.id.action_VisualitzaEsports_to_ReservarOUnirse, bundle);
        });

        binding.btnPadel.setOnClickListener(ignoredView -> {
            String esport = "PADEL";

            // Set the title of the toolbar
            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                if (destination.getId() == R.id.ReservarOUnirse) {
                    Toolbar toolbar = requireActivity().findViewById(R.id.toolbar);
                    if (toolbar != null) {
                        toolbar.setTitle(esport);
                    }
                }
            });

            Bundle bundle = new Bundle();
            bundle.putString("esport", esport);

            navController.navigate(R.id.action_VisualitzaEsports_to_ReservarOUnirse, bundle);
        });

        binding.btnFutbol7.setOnClickListener(ignoredView -> {
            String esport = "FUTBOL 7";

            // Set the title of the toolbar
            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                if (destination.getId() == R.id.ReservarOUnirse) {
                    Toolbar toolbar = requireActivity().findViewById(R.id.toolbar);
                    if (toolbar != null) {
                        toolbar.setTitle(esport);
                    }
                }
            });

            Bundle bundle = new Bundle();
            bundle.putString("esport", esport);

            navController.navigate(R.id.action_VisualitzaEsports_to_ReservarOUnirse, bundle);
        });

        binding.btnFutbolSala.setOnClickListener(ignoredView -> {
            String esport = "FUTBOL SALA";

            // Set the title of the toolbar
            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                if (destination.getId() == R.id.ReservarOUnirse) {
                    Toolbar toolbar = requireActivity().findViewById(R.id.toolbar);
                    if (toolbar != null) {
                        toolbar.setTitle(esport);
                    }
                }
            });

            Bundle bundle = new Bundle();
            bundle.putString("esport", esport);

            navController.navigate(R.id.action_VisualitzaEsports_to_ReservarOUnirse, bundle);
        });

        binding.btnVoleibol.setOnClickListener(ignoredView -> {
            String esport = "VOLEIBOL";

            // Set the title of the toolbar
            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                if (destination.getId() == R.id.ReservarOUnirse) {
                    Toolbar toolbar = requireActivity().findViewById(R.id.toolbar);
                    if (toolbar != null) {
                        toolbar.setTitle(esport);
                    }
                }
            });

            Bundle bundle = new Bundle();
            bundle.putString("esport", esport);

            navController.navigate(R.id.action_VisualitzaEsports_to_ReservarOUnirse, bundle);
        });
    }
}