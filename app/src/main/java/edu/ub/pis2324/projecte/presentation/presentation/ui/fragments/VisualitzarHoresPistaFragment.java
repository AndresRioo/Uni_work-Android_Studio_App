package edu.ub.pis2324.projecte.presentation.presentation.ui.fragments;

import androidx.lifecycle.ViewModelProvider;

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
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;

import edu.ub.pis2324.projecte.R;
import edu.ub.pis2324.projecte.databinding.FragmentVisualitzarHoresPistaBinding;
import edu.ub.pis2324.projecte.presentation.AppContainer;
import edu.ub.pis2324.projecte.presentation.MyApplication;
import edu.ub.pis2324.projecte.presentation.domain.model.entities.Partit;
import edu.ub.pis2324.projecte.presentation.domain.model.entities.Reserva;
import edu.ub.pis2324.projecte.presentation.presentation.ui.adapters.HorasPistaRecyclerViewAdapter;
import edu.ub.pis2324.projecte.presentation.presentation.ui.adapters.ReservasRecyclerViewAdapter;
import edu.ub.pis2324.projecte.presentation.presentation.viewmodels.fragments.VisualitzarHoresPistaViewModel;
import edu.ub.pis2324.projecte.presentation.domain.model.entities.Pista;

public class VisualitzarHoresPistaFragment extends Fragment {
    private VisualitzarHoresPistaViewModel viewModel;
    private ArrayList<Pista> llistaPistes = new ArrayList<>();
    private AppContainer appContainer;
    private NavController navController;
    private String data;
    private String esport;
    private FragmentVisualitzarHoresPistaBinding binding;

    public static VisualitzarHoresPistaFragment newInstance() {
        return new VisualitzarHoresPistaFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentVisualitzarHoresPistaBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        appContainer = ((MyApplication) getActivity().getApplication()).getAppContainer();
        navController = Navigation.findNavController(view);

        Bundle bundle = getArguments();
        if (bundle != null) {
            data = bundle.getString("data");
            esport = bundle.getString("esport");
        }

        SimpleDateFormat formatoEntrada = new SimpleDateFormat("yyMMdd");
        SimpleDateFormat formatoSalida = new SimpleDateFormat("dd/MM/yy");
        binding.txtHoresPista.setText("Hores disponibles per a " + esport + " el dia ");
        try {
            Date fecha = formatoEntrada.parse(data);
            if (fecha != null) {
                String fechaFormateada = formatoSalida.format(fecha);
                binding.txtHoresPista.setText("Hores disponibles per a " + esport + " el dia " + fechaFormateada);
            } else {
                Toast.makeText(getContext(), "Error intern. No trobem la data.", Toast.LENGTH_SHORT).show();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        initViewModel();
        initLlistaPistes();
    }

    private void initLlistaPistes(){
        llistaPistes.clear();
        viewModel.initLlistaPistes(esport,data);
    }

    private void initViewModel() {
        /* Init viewmodel */
        VisualitzarHoresPistaViewModel.Factory factory = new VisualitzarHoresPistaViewModel.Factory(
                getActivity().getApplication(),
                appContainer.visualitzarHoresPistaUseCase
        );
        viewModel = new ViewModelProvider(this,factory).get(VisualitzarHoresPistaViewModel.class);
        /* Init observers */
        initObservers();
    }

    private void initRecyclerView(){
        RecyclerView recyclerView = binding.recyclerViewPista;
        HorasPistaRecyclerViewAdapter adapter;


        adapter = new HorasPistaRecyclerViewAdapter(new HorasPistaRecyclerViewAdapter.OnProductClickListener() {
            
            @Override
            public void onProductClick(Pista pista) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("pista", pista);
                navController.navigate(R.id.action_VisualizarHorasPista_to_ReservarPista,bundle);
            }
        });

        adapter.updateData(llistaPistes);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void initObservers() {
        viewModel.getInitLlistaPistes().observe(getViewLifecycleOwner(), initLlistaPistesState -> {
            switch (initLlistaPistesState.getStatus()) {
                case LOADING:
                    binding.txtLlistaBuida.setVisibility(View.VISIBLE);
                    break;
                case SUCCESS:
                    assert initLlistaPistesState.getData() != null;

                    binding.txtLlistaBuida.setVisibility(View.INVISIBLE);
                    llistaPistes = initLlistaPistesState.getData();
                    initRecyclerView();

                    break;
                case ERROR:
                    assert initLlistaPistesState.getError() != null;

                    binding.txtLlistaBuida.setText(initLlistaPistesState.getError().getMessage());

                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + initLlistaPistesState.getStatus());
            }
        });
    }

}