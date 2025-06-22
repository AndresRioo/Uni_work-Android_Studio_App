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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import edu.ub.pis2324.projecte.R;
import edu.ub.pis2324.projecte.databinding.FragmentVisualitzarReservasBinding;

import edu.ub.pis2324.projecte.presentation.AppContainer;
import edu.ub.pis2324.projecte.presentation.MyApplication;
import edu.ub.pis2324.projecte.presentation.presentation.ui.adapters.ReservasRecyclerViewAdapter;
import edu.ub.pis2324.projecte.presentation.presentation.viewmodels.fragments.VisualitzarReservesViewModel;
import edu.ub.pis2324.projecte.presentation.domain.model.entities.Partit;
import edu.ub.pis2324.projecte.presentation.domain.model.entities.Pista;
import edu.ub.pis2324.projecte.presentation.domain.model.entities.Reserva;

public class VisualitzarReservesFragment extends Fragment {
    private ReservasRecyclerViewAdapter adapter;
    private VisualitzarReservesViewModel viewModel;
    private FragmentVisualitzarReservasBinding binding;
    private AppContainer appContainer;
    private NavController navController;
    private String correu;
    private ArrayList<? extends Reserva> llistaReserves = new ArrayList<>();
    private ArrayList<Pista> llistaPistes = new ArrayList<>();
    private ArrayList<Partit> llistaPartits = new ArrayList<>();
    private final String textCarrega = "Carregant les reserves";

    public static CanviarContrasenyaFragment newInstance() {
        return new CanviarContrasenyaFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //return inflater.inflate(R.layout.fragment_canviar_contrasenya, container, false);

        binding = FragmentVisualitzarReservasBinding.inflate(getLayoutInflater());
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
        initRecyclerView();
        initWidgetListeners();

        llistaReserves.clear();
        viewModel.initLlistaReserves(correu);
    }

    private void initWidgetListeners() {

        binding.txtAmbas.setSelected(true);
        binding.txtPistes.setSelected(false);
        binding.txtPartit.setSelected(false);

        binding.btnMostrarAmbas.setOnClickListener(ignoredView -> {
            binding.txtAmbas.setSelected(true);
            binding.txtPistes.setSelected(false);
            binding.txtPartit.setSelected(false);
            llistaReserves.clear();
            viewModel.initLlistaReserves(correu);
        });

        binding.btnMostrarPistes.setOnClickListener(ignoredView -> {
            binding.txtAmbas.setSelected(false);
            binding.txtPistes.setSelected(true);
            binding.txtPartit.setSelected(false);
            llistaPistes.clear();
            viewModel.initLlistaPistes(correu);
        });

        binding.btnMostrarPartit.setOnClickListener(ignoredView -> {
            binding.txtAmbas.setSelected(false);
            binding.txtPistes.setSelected(false);
            binding.txtPartit.setSelected(true);
            llistaPartits.clear();
            viewModel.initLlistaPartits(correu);
        });
    }

    private void initViewModel() {
        /* Init viewmodel */
        VisualitzarReservesViewModel.Factory factory = new VisualitzarReservesViewModel.Factory(
                getActivity().getApplication(),
                appContainer.visualitzarReservesUseCase
        );
        viewModel = new ViewModelProvider(this,factory).get(VisualitzarReservesViewModel.class);
        /* Init observers */
        initObservers();
    }


    private void initRecyclerView(){
        RecyclerView recyclerView = binding.recyclerViewReservas;

        adapter = new ReservasRecyclerViewAdapter( new ReservasRecyclerViewAdapter.OnProductClickListener() {
            @Override
            public void onProductClick(Reserva reserva) {

                Bundle bundle = new Bundle();
                bundle.putSerializable("reserva", reserva);

                if (reserva instanceof Partit) {
                    navController.navigate(R.id.action_MisReservas_to_VisualitzarInfoPartit, bundle);
                } else if (reserva instanceof Pista) {
                    navController.navigate(R.id.action_MisReservas_to_VisualitzarInfoPista, bundle);
                }

            }}
            );

        adapter.updateData(llistaReserves);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void initObservers() {

        viewModel.getInitLlistaReserves().observe(getViewLifecycleOwner(), initLlistaReservesState -> {
            switch (initLlistaReservesState.getStatus()) {
                case LOADING:
                    adapter.clearData();
                    binding.txtLlistaBuida.setText(textCarrega);
                    binding.txtLlistaBuida.setVisibility(View.VISIBLE);
                    binding.btnMostrarAmbas.setEnabled(false);
                    binding.btnMostrarPartit.setEnabled(false);
                    binding.btnMostrarPistes.setEnabled(false);
                    break;
                case SUCCESS:
                    assert initLlistaReservesState.getData() != null;

                    binding.txtLlistaBuida.setVisibility(View.INVISIBLE);
                    llistaReserves = initLlistaReservesState.getData();
                    adapter.updateData(llistaReserves);

                    binding.btnMostrarAmbas.setEnabled(true);
                    binding.btnMostrarPartit.setEnabled(true);
                    binding.btnMostrarPistes.setEnabled(true);

                    break;
                case ERROR:
                    assert initLlistaReservesState.getError() != null;

                    binding.txtLlistaBuida.setText(initLlistaReservesState.getError().getMessage());
                    binding.btnMostrarAmbas.setEnabled(true);
                    binding.btnMostrarPartit.setEnabled(true);
                    binding.btnMostrarPistes.setEnabled(true);

                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + initLlistaReservesState.getStatus());
            }
        });

        viewModel.getInitLlistaPistes().observe(getViewLifecycleOwner(), initLlistaPistesState -> {
            switch (initLlistaPistesState.getStatus()) {
                case LOADING:
                    adapter.clearData();
                    binding.txtLlistaBuida.setText(textCarrega);
                    binding.txtLlistaBuida.setVisibility(View.VISIBLE);
                    binding.btnMostrarAmbas.setEnabled(false);
                    binding.btnMostrarPartit.setEnabled(false);
                    binding.btnMostrarPistes.setEnabled(false);
                    break;
                case SUCCESS:
                    assert initLlistaPistesState.getData() != null;

                    binding.txtLlistaBuida.setVisibility(View.INVISIBLE);
                    llistaPistes = initLlistaPistesState.getData();
                    adapter.updateData(llistaPistes);

                    binding.btnMostrarAmbas.setEnabled(true);
                    binding.btnMostrarPartit.setEnabled(true);
                    binding.btnMostrarPistes.setEnabled(true);

                    break;
                case ERROR:
                    assert initLlistaPistesState.getError() != null;

                    binding.txtLlistaBuida.setText(initLlistaPistesState.getError().getMessage());
                    binding.btnMostrarAmbas.setEnabled(true);
                    binding.btnMostrarPartit.setEnabled(true);
                    binding.btnMostrarPistes.setEnabled(true);

                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + initLlistaPistesState.getStatus());
            }
        });

        viewModel.getInitLlistaPartits().observe(getViewLifecycleOwner(), initLlistaPartitsState -> {
            switch (initLlistaPartitsState.getStatus()) {
                case LOADING:
                    adapter.clearData();
                    binding.txtLlistaBuida.setText(textCarrega);
                    binding.txtLlistaBuida.setVisibility(View.VISIBLE);
                    binding.btnMostrarAmbas.setEnabled(false);
                    binding.btnMostrarPartit.setEnabled(false);
                    binding.btnMostrarPistes.setEnabled(false);
                    break;
                case SUCCESS:
                    assert initLlistaPartitsState.getData() != null;

                    binding.txtLlistaBuida.setVisibility(View.INVISIBLE);
                    llistaPartits = initLlistaPartitsState.getData();
                    adapter.updateData(llistaPartits);

                    binding.btnMostrarAmbas.setEnabled(true);
                    binding.btnMostrarPartit.setEnabled(true);
                    binding.btnMostrarPistes.setEnabled(true);

                    break;
                case ERROR:
                    assert initLlistaPartitsState.getError() != null;

                    binding.txtLlistaBuida.setText(initLlistaPartitsState.getError().getMessage());
                    binding.btnMostrarAmbas.setEnabled(true);
                    binding.btnMostrarPartit.setEnabled(true);
                    binding.btnMostrarPistes.setEnabled(true);

                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + initLlistaPartitsState.getStatus());
            }
        });
    }

}