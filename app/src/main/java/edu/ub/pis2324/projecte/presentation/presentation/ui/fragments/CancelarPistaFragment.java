package edu.ub.pis2324.projecte.presentation.presentation.ui.fragments;

import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import android.content.SharedPreferences;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import edu.ub.pis2324.projecte.R;
import edu.ub.pis2324.projecte.databinding.FragmentCancelarPistaBinding;

import edu.ub.pis2324.projecte.presentation.MyApplication;
import edu.ub.pis2324.projecte.presentation.AppContainer;
import edu.ub.pis2324.projecte.presentation.domain.model.entities.Pista;
import edu.ub.pis2324.projecte.presentation.presentation.viewmodels.fragments.CancelarPistaViewModel;
import edu.ub.pis2324.projecte.presentation.utils.BottomNavigationUtil;

public class CancelarPistaFragment extends Fragment {
    private CancelarPistaViewModel viewModel;
    private FragmentCancelarPistaBinding binding;
    private AppContainer appContainer;
    private NavController navController;
    private String correu;
    private Pista pista;
    private String idPista;

    public static CancelarPistaFragment newInstance() {
        return new CancelarPistaFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //return inflater.inflate(R.layout.fragment_visualitzar_info_pista, container, false);
        binding = FragmentCancelarPistaBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        appContainer = ((MyApplication) getActivity().getApplication()).getAppContainer();
        navController = Navigation.findNavController(view);

        Bundle bundle = getArguments();
        if (bundle != null) {
            pista = (Pista) bundle.getSerializable("reserva");
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

        /* Initializations */
        initViewModel();
        initWidgetListeners();
    }

    private void initWidgetListeners() {

        // Informació de la pista
        binding.txtInfoData.setText(pista.getData());
        binding.txtInfoHora.setText(pista.getHora() + ":00");
        binding.txtInfoEsport.setText(pista.getEsport());
        binding.txtInfoMaterial.setText(pista.getMaterial());

        binding.btnCancela.setOnClickListener(ignoredView -> viewModel.cancelar(correu, idPista));
    }

    private void initViewModel() {
        /* Init viewmodel */
        CancelarPistaViewModel.Factory factory = new CancelarPistaViewModel.Factory(
                getActivity().getApplication(),
                appContainer.cancelarPistaUseCase
        );
        viewModel = new ViewModelProvider(this,factory).get(CancelarPistaViewModel.class);
        /* Init observers */
        initObservers();
    }

    private void initObservers() {
        viewModel.getCancelarPista().observe(getViewLifecycleOwner(), state -> {
            switch (state.getStatus()) {
                case LOADING:
                    binding.btnCancela.setEnabled(false);
                    break;
                case SUCCESS:
                    assert state.getData() != null;

                    Toast.makeText(getContext(), state.getData(), Toast.LENGTH_SHORT).show();

                    BottomNavigationUtil.setSelectedItem(R.id.Home);
                    navController.popBackStack();
                    NavOptions navOptions = new NavOptions.Builder()
                            .setPopUpTo(R.id.VisualitzarInfoPartit, true) // Provide the id of your nav_graph or start destination id
                            .build();
                    navController.navigate(R.id.VisualitzaEsports, null, navOptions);


                    break;
                case ERROR:
                    assert state.getError() != null;

                    Toast.makeText(getContext(), state.getError().getMessage(), Toast.LENGTH_SHORT).show();
                    binding.btnCancela.setEnabled(true);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + state.getStatus());
            }
        });
    }

}