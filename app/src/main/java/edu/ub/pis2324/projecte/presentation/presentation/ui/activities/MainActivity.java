package edu.ub.pis2324.projecte.presentation.presentation.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import edu.ub.pis2324.projecte.presentation.presentation.viewmodels.fragments.MainViewModel;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import edu.ub.pis2324.projecte.R;
import edu.ub.pis2324.projecte.databinding.ActivityMainBinding;
import edu.ub.pis2324.projecte.presentation.AppContainer;
import edu.ub.pis2324.projecte.presentation.MyApplication;
import edu.ub.pis2324.projecte.presentation.presentation.viewmodels.fragments.LogInViewModel;
import edu.ub.pis2324.projecte.presentation.utils.BottomNavigationUtil;

public class MainActivity extends AppCompatActivity {
    private MainViewModel viewModel;
    private NavController navController;
    AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private AppContainer appContainer;

    /**
     * Called when the activity is being created.
     * @param savedInstanceState The saved instance state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        appContainer = ((MyApplication) getApplication()).getAppContainer();

        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        /* Initializations */
        initNavigation();
        initViewModel();
        initWidgetListeners();
        binding.NavigationViewBottom.setSelectedItemId(R.id.Home);
        BottomNavigationUtil bottomNavigationUtil = new BottomNavigationUtil(binding.NavigationViewBottom);
        updateDataBase();
    }

    /**
     * Initialize the navigation.
     */
    private void initNavigation() {
        /* Set up the navigation controller */
        navController = ( (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_main) )
                .getNavController();

    /*
      Set up the bottom navigation, indicating the fragments
      that are part of the bottom navigation.
    */
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.Perfil,
                R.id.Home,
                R.id.MisReservas
        ).build();

        /* Set up navigation with both the action bar and the bottom navigation view */
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.NavigationViewBottom, navController);
    }


    /**
     * Handle the up navigation.
     * @return
     */
    @Override
    public boolean onSupportNavigateUp() {
        /* Enable the up navigation: the button shown in the left of the action bar */
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void initWidgetListeners() {
        // Navegación mediante el NavigationViewBottom
        binding.NavigationViewBottom.setOnNavigationItemSelectedListener(item -> {

            int boton = item.getItemId(); // saber cual de los 3 botones ha sido seleccionado
            if (boton == R.id.Home) {

                // Navegar a Fragmento de Inicio
                navController.navigate(R.id.VisualitzaEsports);

                return true;
            } else if (boton == R.id.Perfil) {

                // Navegar a Fragmento de Perfil
                navController.navigate(R.id.AjustesPerfil);

                return true;
            } else if (boton == R.id.MisReservas) {

                // Navegar a Fragmento de Mis Reservas
                navController.navigate(R.id.MisReservas);

                return true;
            }
            return false;
        });
    }

    public com.google.android.material.bottomnavigation.BottomNavigationView getBottomNavigationView() {
        return binding.NavigationViewBottom;
    }

    private void initViewModel() {
        MainViewModel.Factory factory = new MainViewModel.Factory(
                getApplication(),
                appContainer.updateDataBaseUseCase
        );
        viewModel = new ViewModelProvider(this, factory).get(MainViewModel.class);
    }

    private void updateDataBase() {
        // Obtenim la data actual
        Date currentDate = new Date();
        SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/yy"); // Data com es llegirà
        SimpleDateFormat format2 = new SimpleDateFormat("yyMMdd");   // Data per ordenar
        String currentDateString = format2.format(currentDate);

        viewModel.eliminarPartitsPassats(currentDateString);
        viewModel.eliminarPistesPassades(currentDateString);
        viewModel.eliminarPistesClient(currentDateString);
        viewModel.eliminarPartitsClient(currentDateString);



    }


}