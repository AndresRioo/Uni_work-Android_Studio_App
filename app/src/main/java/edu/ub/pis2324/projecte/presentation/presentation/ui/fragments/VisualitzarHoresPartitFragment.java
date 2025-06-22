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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import edu.ub.pis2324.projecte.R;
import edu.ub.pis2324.projecte.databinding.FragmentVisualitzarHoresPartitBinding;
import edu.ub.pis2324.projecte.presentation.AppContainer;
import edu.ub.pis2324.projecte.presentation.MyApplication;
import edu.ub.pis2324.projecte.presentation.domain.model.entities.Partit;
import edu.ub.pis2324.projecte.presentation.presentation.ui.adapters.HorasPartidoRecyclerViewAdapter;
import edu.ub.pis2324.projecte.presentation.presentation.viewmodels.fragments.VisualitzarHoresPartitViewModel;

public class VisualitzarHoresPartitFragment extends Fragment {
    private VisualitzarHoresPartitViewModel viewModel;
    private ArrayList<Partit> llistaPartits = new ArrayList<>();
    private AppContainer appContainer;
    private NavController navController;
    private String data;
    private String esport;
    private FragmentVisualitzarHoresPartitBinding binding;

    private String correu;

    public static VisualitzarHoresPartitFragment newInstance() {
        return new VisualitzarHoresPartitFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentVisualitzarHoresPartitBinding.inflate(getLayoutInflater());
        Bundle bundle = getArguments();
        if (bundle != null) {
            data = bundle.getString("data");
            esport = bundle.getString("esport");
        }

        if (getActivity() != null) {
            SharedPreferences preferences = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
            correu = preferences.getString("correu", null);
        } else {
            Toast.makeText(getContext(), "Error intern. No trobem el correu.", Toast.LENGTH_SHORT).show();
            correu = null;
        }


        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        appContainer = ((MyApplication) getActivity().getApplication()).getAppContainer();
        navController = Navigation.findNavController(view);

        SimpleDateFormat formatoEntrada = new SimpleDateFormat("yyMMdd");
        SimpleDateFormat formatoSalida = new SimpleDateFormat("dd/MM/yy");
        binding.txtHoresPartit.setText("Hores disponibles per a " + esport + " el dia ");
        try {
            Date fecha = formatoEntrada.parse(data);
            if (fecha != null) {
                String fechaFormateada = formatoSalida.format(fecha);
                binding.txtHoresPartit.setText("Hores disponibles per a " + esport + " el dia " + fechaFormateada);
            } else {
                Toast.makeText(getContext(), "Error intern. No trobem la data.", Toast.LENGTH_SHORT).show();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        /* Initializations */
        initViewModel();
        initLlistaPartits();
    }

    private void initLlistaPartits(){
        llistaPartits.clear();
        viewModel.initLlistaPartits(esport,data,correu);
    }

    private void initViewModel() {
        /* Init viewmodel */
        VisualitzarHoresPartitViewModel.Factory factory = new VisualitzarHoresPartitViewModel.Factory(
                getActivity().getApplication(),
                appContainer.visualitzarHoresPartitUseCase
        );
        viewModel = new ViewModelProvider(this,factory).get(VisualitzarHoresPartitViewModel.class);
        /* Init observers */
        initObservers();
    }

    private void initRecyclerView(){
        RecyclerView recyclerView = binding.recyclerViewPartido;
        HorasPartidoRecyclerViewAdapter adapter;

        adapter = new HorasPartidoRecyclerViewAdapter( new HorasPartidoRecyclerViewAdapter.OnProductClickListener() {
            @Override
            public void onProductClick(Partit partit) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("partit", partit);
                navController.navigate(R.id.action_VisualizarHorasPartido_to_UnirsePartido,bundle);            }
        });

        adapter.updateData(llistaPartits);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void initObservers() {
        viewModel.getInitLlistaPartits().observe(getViewLifecycleOwner(), initLlistaPartitsState -> {
            switch (initLlistaPartitsState.getStatus()) {
                case LOADING:
                    binding.txtLlistaBuida.setVisibility(View.VISIBLE);
                    break;
                case SUCCESS:
                    assert initLlistaPartitsState.getData() != null;

                    binding.txtLlistaBuida.setVisibility(View.INVISIBLE);
                    llistaPartits = initLlistaPartitsState.getData();
                    initRecyclerView();

                    break;
                case ERROR:
                    assert initLlistaPartitsState.getError() != null;

                    binding.txtLlistaBuida.setText(initLlistaPartitsState.getError().getMessage());
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + initLlistaPartitsState.getStatus());
            }
        });
    }

}