package edu.ub.pis2324.projecte.presentation.presentation.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import edu.ub.pis2324.projecte.R;
import edu.ub.pis2324.projecte.databinding.ActivitySignUpBinding;

import edu.ub.pis2324.projecte.presentation.AppContainer;
import edu.ub.pis2324.projecte.presentation.MyApplication;
import edu.ub.pis2324.projecte.presentation.presentation.viewmodels.fragments.SignUpViewModel;

public class SignUpActivity extends AppCompatActivity {
    private ActivitySignUpBinding binding;
    private SignUpViewModel viewModel;
    private AppContainer appContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbarSignUp);
        binding.toolbarSignUp.setTitle("Sign Up");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        appContainer = ((MyApplication) getApplication()).getAppContainer();

        // color del texto de la tool bar
        binding.toolbarSignUp.setTitleTextAppearance(this, R.style.ToolbarTextAppearance);

        binding.toolbarSignUp.setNavigationOnClickListener(v -> finish());

        initViewModel();
        initWidgetListeners();
    }
    public void initWidgetListeners(){
        binding.btnSignUp.setOnClickListener(item -> {
            String nom = binding.txtNom.getText().toString();
            String correu = binding.txtCorreu.getText().toString();
            String contrasenya = binding.txtContrasenya.getText().toString();
            String repeticioContrasenya = binding.txtRepeticioContrasenya.getText().toString();

            viewModel.crearClient(nom, correu, contrasenya, repeticioContrasenya);
        });

        binding.btnVisualitzacioContrasenya.setOnTouchListener((v,event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // Mostrar la contraseña mientras el botón está siendo presionado
                    binding.txtContrasenya.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    break;
                case MotionEvent.ACTION_UP:
                    // Volver a ocultar la contraseña cuando se suelta el botón
                    binding.txtContrasenya.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    v.performClick();
                    break;
            }
            binding.txtContrasenya.setSelection(binding.txtContrasenya.getText().length());
            return true;

        });
        binding.btnVisualitzacioRepeticioContrasenya.setOnTouchListener((v,event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // Mostrar la contraseña mientras el botón está siendo presionado
                    binding.txtRepeticioContrasenya.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    break;
                case MotionEvent.ACTION_UP:
                    // Volver a ocultar la contraseña cuando se suelta el botón
                    binding.txtRepeticioContrasenya.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    v.performClick();
                    break;
            }
            binding.txtRepeticioContrasenya.setSelection(binding.txtRepeticioContrasenya.getText().length());
            return true;
        });
    }

    private void initViewModel() {
        SignUpViewModel.Factory factory = new SignUpViewModel.Factory(
                getApplication(),
                appContainer.signUpUseCase
        );
        viewModel = new ViewModelProvider(this, factory).get(SignUpViewModel.class);

        initObservers();
    }

    private void initObservers() {
        viewModel.getCrearClientState().observe(this, crearState -> {

            switch (crearState.getStatus()) {
                case LOADING:
                    binding.btnSignUp.setEnabled(false);
                    break;

                case SUCCESS:
                    assert crearState.getData() != null;

                    Toast.makeText(SignUpActivity.this, crearState.getData(), Toast.LENGTH_SHORT).show();

                    // Afegir el correu a la memòria local
                    String correu = binding.txtCorreu.getText().toString();
                    SharedPreferences.Editor editor = getSharedPreferences("login", MODE_PRIVATE).edit();
                    editor.putString("correu", correu);
                    editor.apply();

                    Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                    startActivity(intent);

                    binding.btnSignUp.setEnabled(true);
                    break;

                case ERROR:
                    assert crearState.getError() != null;

                    Toast.makeText(SignUpActivity.this, crearState.getError().getMessage(), Toast.LENGTH_SHORT).show();

                    binding.btnSignUp.setEnabled(true);
                    break;

                default:
                    throw new IllegalStateException("Unexpected value: " + crearState.getStatus());
            }
        });
    }

}


