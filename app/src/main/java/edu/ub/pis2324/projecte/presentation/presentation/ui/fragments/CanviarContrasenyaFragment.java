package edu.ub.pis2324.projecte.presentation.presentation.ui.fragments;

import androidx.lifecycle.ViewModelProvider;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import edu.ub.pis2324.projecte.R;
import edu.ub.pis2324.projecte.databinding.FragmentCanviarContrasenyaBinding;
import edu.ub.pis2324.projecte.presentation.presentation.viewmodels.fragments.CanviarContrasenyaViewModel;
import edu.ub.pis2324.projecte.presentation.MyApplication;
import edu.ub.pis2324.projecte.presentation.AppContainer;

public class CanviarContrasenyaFragment extends Fragment {
    private CanviarContrasenyaViewModel viewModel;
    private FragmentCanviarContrasenyaBinding binding;
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
        binding = FragmentCanviarContrasenyaBinding.inflate(getLayoutInflater());
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
        initViewModel();
        initWidgetListeners();
    }

    private void initWidgetListeners() {
        binding.btnCanviarContrasenya.setOnClickListener(v -> {
            String contrasenya = binding.txtContrasenya.getText().toString();
            String novaContrasenya = binding.txtNovaContrasenya.getText().toString();
            String repeticioContrasenya = binding.txtRepeticioContrasenya.getText().toString();

            viewModel.canviarContrasenya(correu, contrasenya, novaContrasenya, repeticioContrasenya);
        });

        binding.btnVisualitzarContrasenya.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    binding.txtContrasenya.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    break;
                case MotionEvent.ACTION_UP:
                    binding.txtContrasenya.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    v.performClick();
                    break;
            }
            binding.txtContrasenya.setSelection(binding.txtContrasenya.getText().length());
            return true;
        });

        binding.btnVisualitzarNovaContrasenya.setOnTouchListener((v,event) -> {
            switch ( event.getAction() ) {
                case MotionEvent.ACTION_DOWN:
                    binding.txtNovaContrasenya.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    break;
                case MotionEvent.ACTION_UP:
                    binding.txtNovaContrasenya.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    v.performClick();
                    break;
            }
            binding.txtNovaContrasenya.setSelection(binding.txtNovaContrasenya.getText().length());
            return true;
        });

        binding.btnVisualitzarRepeticioContrasenya.setOnTouchListener((v,event) -> {
            switch ( event.getAction() ) {
                case MotionEvent.ACTION_DOWN:
                    binding.txtRepeticioContrasenya.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    break;
                case MotionEvent.ACTION_UP:
                    binding.txtRepeticioContrasenya.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    v.performClick();
                    break;
            }
            binding.txtRepeticioContrasenya.setSelection(binding.txtRepeticioContrasenya.getText().length());
            return true;
        });
    }

    private void initViewModel() {
        /* Init viewmodel */
        CanviarContrasenyaViewModel.Factory factory = new CanviarContrasenyaViewModel.Factory(
                getActivity().getApplication(),
                appContainer.canviarContrasenyaUseCase
        );
        viewModel = new ViewModelProvider(this,factory).get(CanviarContrasenyaViewModel.class);
        /* Init observers */
        initObservers();
    }

    private void initObservers() {
        viewModel.getCanviarContrasenyaState().observe(getViewLifecycleOwner(), canviarContrasenyaState -> {
            switch (canviarContrasenyaState.getStatus()) {
                case LOADING:
                    binding.btnCanviarContrasenya.setEnabled(false);
                    break;
                case SUCCESS:
                    assert canviarContrasenyaState.getData() != null;

                    Toast.makeText(getContext(), canviarContrasenyaState.getData(), Toast.LENGTH_SHORT).show();
                    navController.navigate(R.id.action_CanviarContrasenya_to_AjustesPerfil);

                    break;
                case ERROR:
                    assert canviarContrasenyaState.getError() != null;

                    Toast.makeText(getContext(), canviarContrasenyaState.getError().getMessage(), Toast.LENGTH_SHORT).show();
                    binding.btnCanviarContrasenya.setEnabled(true);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + canviarContrasenyaState.getStatus());
            }
        });
    }
}