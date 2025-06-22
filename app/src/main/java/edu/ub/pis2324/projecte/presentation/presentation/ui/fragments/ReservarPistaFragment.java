package edu.ub.pis2324.projecte.presentation.presentation.ui.fragments;

import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.SharedPreferences;
import android.widget.Toast;

import edu.ub.pis2324.projecte.R;
import edu.ub.pis2324.projecte.databinding.FragmentReservarPistaBinding;
import edu.ub.pis2324.projecte.presentation.AppContainer;
import edu.ub.pis2324.projecte.presentation.MyApplication;
import edu.ub.pis2324.projecte.presentation.presentation.viewmodels.fragments.ReservarPistaViewModel;
import edu.ub.pis2324.projecte.presentation.domain.model.entities.Pista;

public class ReservarPistaFragment extends Fragment {
    private ReservarPistaViewModel viewModel;
    private FragmentReservarPistaBinding binding;
    private AppContainer appContainer;
    private NavController navController;
    private String correu;
    private Pista pista;
    private String idPista;

    public static ReservarPistaFragment newInstance() {
        return new ReservarPistaFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentReservarPistaBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        appContainer = ((MyApplication) getActivity().getApplication()).getAppContainer();
        navController = Navigation.findNavController(view);

        Bundle bundle = getArguments();
        if (bundle != null) {
            pista = (Pista) bundle.getSerializable("pista");
            if (pista != null) {
                idPista = pista.getIdReserva().toString();
            } else {
                Toast.makeText(getContext(), "Error intern. No trobem la pista.", Toast.LENGTH_SHORT).show();
            }
        }

        if (getActivity() != null) {
            SharedPreferences preferences = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
            correu = preferences.getString("correu", null);
        } else {
            Toast.makeText(getContext(), "Error intern. No trobem el correu.", Toast.LENGTH_SHORT).show();
            correu = null;
        }

        initViewModel();
        initWidgetListeners();
    }

    private void initWidgetListeners() {

        // Informació de la pista
        binding.txtInfoData.setText(pista.getData());
        binding.txtInfoHora.setText(pista.getHora() + ":00");
        binding.txtInfoEsport.setText(pista.getEsport());
        binding.txtInfoMaterial.setText(pista.getMaterial());

        // Botó per reservar la pista
        binding.btnReservaPista.setOnClickListener(ignoredView -> viewModel.reservar(correu, idPista));
    }

    private void initViewModel() {
        /* Init viewmodel */
        ReservarPistaViewModel.Factory factory = new ReservarPistaViewModel.Factory(
                getActivity().getApplication(),
                appContainer.reservarPistaUseCase
        );
        viewModel = new ViewModelProvider(this,factory).get(ReservarPistaViewModel.class);
        /* Init observers */
        initObservers();
    }

    private void initObservers() {
        viewModel.getReservarPista().observe(getViewLifecycleOwner(), state -> {
            switch (state.getStatus()) {
                case LOADING:
                    binding.btnReservaPista.setEnabled(false);
                    break;
                case SUCCESS:
                    assert state.getData() != null;

                    Toast.makeText(getContext(), state.getData(), Toast.LENGTH_SHORT).show();
                    navController.navigate(R.id.action_ReservarPista_to_VisualitzaEsports);

                    break;
                case ERROR:
                    assert state.getError() != null;

                    Toast.makeText(getContext(), state.getError().getMessage(), Toast.LENGTH_SHORT).show();
                    binding.btnReservaPista.setEnabled(true);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + state.getStatus());
            }
        });
    }

}