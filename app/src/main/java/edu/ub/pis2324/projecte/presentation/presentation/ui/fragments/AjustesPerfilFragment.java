package edu.ub.pis2324.projecte.presentation.presentation.ui.fragments;

import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import edu.ub.pis2324.projecte.R;
import edu.ub.pis2324.projecte.databinding.FragmentAjustesPerfilBinding;
import edu.ub.pis2324.projecte.presentation.presentation.ui.activities.LogInActivity;
import edu.ub.pis2324.projecte.presentation.presentation.viewmodels.fragments.AjustesPerfilViewModel;
import edu.ub.pis2324.projecte.presentation.MyApplication;
import edu.ub.pis2324.projecte.presentation.AppContainer;
import androidx.appcompat.app.AlertDialog;

public class AjustesPerfilFragment extends Fragment {
    private AjustesPerfilViewModel viewModel;
    private FragmentAjustesPerfilBinding binding;
    private AppContainer appContainer;
    private NavController navController;
    private String correu;

    public static CanviarContrasenyaFragment newInstance() {
        return new CanviarContrasenyaFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //return inflater.inflate(R.layout.fragment_canviar_contrasenya, container, false);
        binding = FragmentAjustesPerfilBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        appContainer = ((MyApplication) getActivity().getApplication()).getAppContainer();
        navController = Navigation.findNavController(view);

        if (getActivity() != null) {
            SharedPreferences preferences = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
            correu = preferences.getString("correu", null);
        } else {
            Toast.makeText(getContext(), "Error intern. No trobem el correu.", Toast.LENGTH_SHORT).show();
            correu = null;
        }

        /* Initializations */
        initViewModel();
        initWidgetListeners();
    }

    private void initWidgetListeners() {

        // Informació client
        binding.txtCorreu.setText("Correu: " + correu);
        viewModel.getNomPerCorreu(correu);

        binding.btnCanviarContrasenya.setOnClickListener(item -> navController.navigate(R.id.action_AjustesPerfil_to_CanviarContrasenya));

        binding.btnMevesReserves.setOnClickListener(item -> navController.navigate(R.id.action_AjustesPerfil_to_MisReservas));


        binding.btnTancarSessio.setOnClickListener(item -> {

            // Construcció del quadre d'advertència
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Tancar sessió");
            builder.setMessage("Estàs segur de que vols tancar la sessió?");

            builder.setPositiveButton("Sí", (dialog, which) -> {
                // Eliminar el correu de la memòria local
                SharedPreferences.Editor editor = requireContext().getSharedPreferences("login", Context.MODE_PRIVATE).edit();
                editor.clear();
                editor.apply();

                // Obrir l'activitat de login
                Intent intent = new Intent(requireContext(), LogInActivity.class);
                startActivity(intent);

                // Limpiar la pila de retroceso para que no se pueda volver atrás desde la actividad de login
                navController.popBackStack();

                // Finalitzar l'activitat actual
                if (getActivity() != null) getActivity().finish();
            });

            builder.setNegativeButton("Cancel·lar", (dialog, which) -> {
                // No es fa res, es tanca el diàleg
            });


            // Mostrar el quadre d'advertència
            AlertDialog dialog = builder.create();
            dialog.show();
        });


        binding.btnEliminarCompte.setOnClickListener(item -> {

            // Construcció del quadre d'advertència
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Eliminar compte");
            builder.setMessage("Estàs segur de que vols eliminar el compte?");

            builder.setPositiveButton("Sí", (dialog, which) -> viewModel.eliminarClient(correu));

            builder.setNegativeButton("Cancel·lar", (dialog, which) -> {}); // Es tanca el diàleg


            // Mostrar el quadre d'advertència
            AlertDialog dialog = builder.create();
            dialog.show();
        });
    }


    private void initViewModel() {
        AjustesPerfilViewModel.Factory factory = new AjustesPerfilViewModel.Factory(
                getActivity().getApplication(),
                appContainer.ajustesPerfilUseCase
        );
        viewModel = new ViewModelProvider(this, factory).get(AjustesPerfilViewModel.class);

        initObservers();
    }

    private void initObservers() {

        viewModel.getNomPerCorreuState().observe(getViewLifecycleOwner(), nomState -> {

            switch (nomState.getStatus()) {
                case LOADING:
                    binding.btnCanviarContrasenya.setEnabled(false);
                    binding.btnMevesReserves.setEnabled(false);
                    binding.btnTancarSessio.setEnabled(false);
                    binding.btnEliminarCompte.setEnabled(false);
                    break;

                case SUCCESS:
                    assert nomState.getData() != null;

                    binding.txtNom.setText("Nom: " + nomState.getData());

                    binding.btnCanviarContrasenya.setEnabled(true);
                    binding.btnMevesReserves.setEnabled(true);
                    binding.btnTancarSessio.setEnabled(true);
                    binding.btnEliminarCompte.setEnabled(true);
                    break;

                case ERROR:
                    assert nomState.getError() != null;

                    binding.txtNom.setText("Nom: " + nomState.getData());
                    Toast.makeText(getContext(), nomState.getError().getMessage(), Toast.LENGTH_SHORT).show();

                    binding.btnCanviarContrasenya.setEnabled(true);
                    binding.btnMevesReserves.setEnabled(true);
                    binding.btnTancarSessio.setEnabled(true);
                    binding.btnEliminarCompte.setEnabled(true);
                    break;

                default:
                    throw new IllegalStateException("Unexpected value: " + nomState.getStatus());
            }
        });


        viewModel.getEliminarClientState().observe(getViewLifecycleOwner(), eliminarState -> {

            switch (eliminarState.getStatus()) {
                case LOADING:
                    binding.btnCanviarContrasenya.setEnabled(false);
                    binding.btnMevesReserves.setEnabled(false);
                    binding.btnTancarSessio.setEnabled(false);
                    binding.btnEliminarCompte.setEnabled(false);
                    break;

                case SUCCESS:
                    assert eliminarState.getData() != null;

                    Toast.makeText(getContext(), eliminarState.getData(), Toast.LENGTH_SHORT).show();

                    // Eliminar el correu de la memòria local
                    SharedPreferences.Editor editor = requireContext().getSharedPreferences("login", Context.MODE_PRIVATE).edit();
                    editor.clear();
                    editor.apply();

                    // Obrir l'activitat de login
                    Intent intent = new Intent(requireContext(), LogInActivity.class);
                    startActivity(intent);

                    // Limpiar la pila de retroceso para que no se pueda volver atrás desde la actividad de login
                    navController.popBackStack();

                    // Finalitzar l'activitat actual
                    if (getActivity() != null) getActivity().finish();

                    binding.btnCanviarContrasenya.setEnabled(true);
                    binding.btnMevesReserves.setEnabled(true);
                    binding.btnTancarSessio.setEnabled(true);
                    binding.btnEliminarCompte.setEnabled(true);
                    break;

                case ERROR:
                    assert eliminarState.getError() != null;

                    Toast.makeText(getContext(), eliminarState.getError().getMessage(), Toast.LENGTH_SHORT).show();

                    binding.btnCanviarContrasenya.setEnabled(true);
                    binding.btnMevesReserves.setEnabled(true);
                    binding.btnTancarSessio.setEnabled(true);
                    binding.btnEliminarCompte.setEnabled(true);
                    break;

                default:
                    throw new IllegalStateException("Unexpected value: " + eliminarState.getStatus());
            }
        });
    }

}