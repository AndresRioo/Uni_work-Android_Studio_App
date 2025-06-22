package edu.ub.pis2324.projecte.presentation.presentation.ui.fragments;

import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
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
import edu.ub.pis2324.projecte.databinding.FragmentReservarPartitBinding;
import edu.ub.pis2324.projecte.presentation.domain.model.entities.Partit;
import edu.ub.pis2324.projecte.presentation.presentation.viewmodels.fragments.ReservarPartitViewModel;
import edu.ub.pis2324.projecte.presentation.MyApplication;
import edu.ub.pis2324.projecte.presentation.AppContainer;

public class ReservarPartitFragment extends Fragment {
    private ReservarPartitViewModel viewModel;
    private FragmentReservarPartitBinding binding;
    private AppContainer appContainer;
    private NavController navController;
    private String correu;
    private Partit partit;
    private String idPartit;

    public static ReservarPartitFragment newInstance() {
        return new ReservarPartitFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentReservarPartitBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        appContainer = ((MyApplication) getActivity().getApplication()).getAppContainer();
        navController = Navigation.findNavController(view);

        Bundle bundle = getArguments();
        if (bundle != null) {
            partit = (Partit) bundle.getSerializable("partit");
            if (partit != null) {
                idPartit = partit.getIdReserva().toString();
            } else {
                Toast.makeText(getContext(), "Error intern. No trobem el partit.", Toast.LENGTH_SHORT).show();
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

        // Informació del partit
        binding.txtInfoData.setText(partit.getData());
        binding.txtInfoHora.setText(partit.getHora() + ":00");
        binding.txtInfoEsport.setText(partit.getEsport());
        binding.txtInfoMaterial.setText(partit.getMaterial());
        binding.txtInfoNivell.setText(partit.getNivellDificultat());
        binding.txtInfoParticipants.setText(partit.getParticipantsActuals() + " / " + partit.getMaxParticipants());

        // Botó per reservar la pista
        binding.btnUnirsePartido.setOnClickListener(ignoredView -> viewModel.reservar(correu, idPartit));
    }

    private void initViewModel() {
        /* Init viewmodel */
        ReservarPartitViewModel.Factory factory = new ReservarPartitViewModel.Factory(
                getActivity().getApplication(),
                appContainer.reservarPartitUseCase
        );
        viewModel = new ViewModelProvider(this,factory).get(ReservarPartitViewModel.class);
        /* Init observers */
        initObservers();
    }

    private void initObservers() {
        viewModel.getReservarPartit().observe(getViewLifecycleOwner(), state -> {
            switch (state.getStatus()) {
                case LOADING:
                    binding.btnUnirsePartido.setEnabled(false);
                    break;
                case SUCCESS:
                    assert state.getData() != null;

                    Toast.makeText(getContext(), state.getData(), Toast.LENGTH_SHORT).show();
                    navController.navigate(R.id.action_UnirsePartido_to_VisualitzaEsports);

                    break;
                case ERROR:
                    assert state.getError() != null;

                    Toast.makeText(getContext(), state.getError().getMessage(), Toast.LENGTH_SHORT).show();
                    binding.btnUnirsePartido.setEnabled(true);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + state.getStatus());
            }
        });
    }

}

