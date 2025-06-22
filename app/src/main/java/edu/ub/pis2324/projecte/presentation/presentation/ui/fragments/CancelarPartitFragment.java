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
import edu.ub.pis2324.projecte.databinding.FragmentCancelarPartitBinding;
import edu.ub.pis2324.projecte.presentation.AppContainer;
import edu.ub.pis2324.projecte.presentation.MyApplication;
import edu.ub.pis2324.projecte.presentation.presentation.viewmodels.fragments.CancelarPartitViewModel;
import edu.ub.pis2324.projecte.presentation.domain.model.entities.Partit;

import edu.ub.pis2324.projecte.presentation.utils.BottomNavigationUtil;


public class CancelarPartitFragment extends Fragment {
    private CancelarPartitViewModel viewModel;
    private FragmentCancelarPartitBinding binding;
    private AppContainer appContainer;
    private NavController navController;
    private String correu;
    private Partit partit;
    private String idPartit;

    public static CancelarPartitFragment newInstance() {
        return new CancelarPartitFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //return inflater.inflate(R.layout.fragment_visualitzar_info_partit, container, false);
        binding = FragmentCancelarPartitBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        appContainer = ((MyApplication) getActivity().getApplication()).getAppContainer();
        navController = Navigation.findNavController(view);

        Bundle bundle = getArguments();
        if (bundle != null) {
            partit = (Partit) bundle.getSerializable("reserva");
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

        /* Initializations */
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

        binding.btnSortir.setOnClickListener(ignoredView -> viewModel.cancelar(correu, idPartit));

    }

    private void initViewModel() {
        /* Init viewmodel */
        CancelarPartitViewModel.Factory factory = new CancelarPartitViewModel.Factory(
                getActivity().getApplication(),
                appContainer.cancelarPartitUseCase
        );
        viewModel = new ViewModelProvider(this,factory).get(CancelarPartitViewModel.class);
        /* Init observers */
        initObservers();
    }

    private void initObservers() {
        viewModel.getCancelarPartit().observe(getViewLifecycleOwner(), state -> {
            switch (state.getStatus()) {
                case LOADING:
                    binding.btnSortir.setEnabled(false);
                    break;
                case SUCCESS:
                    assert state.getData() != null;

                    Toast.makeText(getContext(), state.getData(), Toast.LENGTH_SHORT).show();

                    // Elimina todas las instancias anteriores de fragmentos de la pila
                    //navController.popBackStack(navController.getGraph().getStartDestination(), false);


                    BottomNavigationUtil.setSelectedItem(R.id.Home);
                    navController.popBackStack();
                    navController.navigate(R.id.VisualitzaEsports);



                    //BottomNavigationUtil.setSelectedItem(R.id.Home);
                    //navController.navigate(R.id.action_VisualitzarInfoPartit_to_VisualitzaEsports2);


                    break;
                case ERROR:
                    assert state.getError() != null;

                    Toast.makeText(getContext(), state.getError().getMessage(), Toast.LENGTH_SHORT).show();
                    binding.btnSortir.setEnabled(true);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + state.getStatus());
            }
        });
    }

}