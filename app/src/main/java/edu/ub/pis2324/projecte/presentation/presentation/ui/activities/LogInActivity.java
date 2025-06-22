package edu.ub.pis2324.projecte.presentation.presentation.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import edu.ub.pis2324.projecte.R;
import edu.ub.pis2324.projecte.databinding.ActivityLogInBinding;
import edu.ub.pis2324.projecte.presentation.AppContainer;
import edu.ub.pis2324.projecte.presentation.MyApplication;
import edu.ub.pis2324.projecte.presentation.presentation.viewmodels.fragments.LogInViewModel;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.FieldValue;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;


public class LogInActivity extends AppCompatActivity {
    private ActivityLogInBinding binding;
    private LogInViewModel viewModel;
    private FirebaseFirestore db;
    private AppContainer appContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        binding = ActivityLogInBinding.inflate(getLayoutInflater());
        db = FirebaseFirestore.getInstance();
        setContentView(binding.getRoot());
        appContainer = ((MyApplication) getApplication()).getAppContainer();

        binding.toolbarLogIn.setTitle("Iniciar sessió");
        // color del texto de la tool bar
        binding.toolbarLogIn.setTitleTextAppearance(this, R.style.ToolbarTextAppearance);

        initViewModel();
        initWidgetListeners();
    }
    public void initWidgetListeners(){
        binding.btnSignUp.setOnClickListener((v) -> {
            // Navego a l'activitat SignUp
            Intent intent = new Intent(LogInActivity.this, SignUpActivity.class);
            startActivity(intent);
        });

        binding.btnLogIn.setOnClickListener(item -> {
            String correu = binding.txtCorreu.getText().toString();
            String contrasenya = binding.txtContrasenya.getText().toString();

            viewModel.iniciarClient(correu, contrasenya);
        });

        binding.btnVisualitzarContrasenya.setOnTouchListener((v,event) -> {
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
    }

    private void initViewModel() {
        LogInViewModel.Factory factory = new LogInViewModel.Factory(
                getApplication(),
                appContainer.logInUseCase
        );
        viewModel = new ViewModelProvider(this, factory).get(LogInViewModel.class);
        initObservers();
    }

    private void initObservers() {
        viewModel.getIniciarClientState().observe(this, iniciarState -> {

            switch (iniciarState.getStatus()) {
                case LOADING:
                    binding.btnLogIn.setEnabled(false);
                    binding.btnSignUp.setEnabled(false);
                    break;

                case SUCCESS:
                    assert iniciarState.getData() != null;

                    Toast.makeText(LogInActivity.this, iniciarState.getData(), Toast.LENGTH_SHORT).show();

                    // Afegir el correu a la memòria local
                    String correu = binding.txtCorreu.getText().toString();
                    SharedPreferences.Editor editor = getSharedPreferences("login", MODE_PRIVATE).edit();
                    editor.putString("correu", correu);
                    editor.apply();



                    Intent intent = new Intent(LogInActivity.this, MainActivity.class);
                    startActivity(intent);

                    binding.btnLogIn.setEnabled(true);
                    binding.btnSignUp.setEnabled(true);
                    break;

                case ERROR:
                    assert iniciarState.getError() != null;

                    Toast.makeText(LogInActivity.this, iniciarState.getError().getMessage(), Toast.LENGTH_SHORT).show();

                    binding.btnLogIn.setEnabled(true);
                    binding.btnSignUp.setEnabled(true);
                    break;

                default:
                    throw new IllegalStateException("Unexpected value: " + iniciarState.getStatus());
            }
        });
    }

    /**
     * Cerrar la app si retrocedemos desde login
     */
    @Override
    public void onBackPressed() {
        // Realizar alguna acción, como salir de la aplicación
        super.onBackPressed();
        finish();
    }


    private void updateDataBase() {

        // Obtenim la data actual
        Date currentDate = new Date();
        SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/yy"); // Data com es llegirà
        SimpleDateFormat format2 = new SimpleDateFormat("yyMMdd");   // Data per ordenar
        String currentDateString = format2.format(currentDate);

        String[] esports = {"FUTBOL 7", "TENNIS", "PADEL", "BASQUET", "VOLEIBOL", "FUTBOL SALA"};
        Map<String, String> esportsMaterials = new HashMap<>();
        esportsMaterials.put("FUTBOL 7", "Pilota Fútbol 7");
        esportsMaterials.put("FUTBOL SALA", "Pilota Fútbol Sala");
        esportsMaterials.put("BASQUET", "Pilota Bàsquet");
        esportsMaterials.put("VOLEIBOL", "Pilota Voleibol");
        esportsMaterials.put("TENNIS", "Raqueta de Tennis");
        esportsMaterials.put("PADEL", "Raqueta de Pàdel");

        Map<String, Integer> esportsParticipants = new HashMap<>();
        esportsParticipants.put("FUTBOL 7", 14);
        esportsParticipants.put("FUTBOL SALA", 10);
        esportsParticipants.put("BASQUET", 10);
        esportsParticipants.put("VOLEIBOL", 12);
        esportsParticipants.put("TENNIS", 2);
        esportsParticipants.put("PADEL", 4);

        String[] nivells = {"Principiant", "Avançat", "Mundial"};


        // AFEGIR ELS DIES POSTERIORS A LA DATA D'AVUI PER PISTES
        db.collection("PISTES")
                .orderBy("dataOrdenada", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String lastDateString = currentDateString;
                        if (!task.getResult().isEmpty()) {
                            // Si hi ha elements a la base de dades, agafem la data més llunyana
                            lastDateString = task.getResult().getDocuments().get(0).getString("dataOrdenada");
                        }

                        // CALCULAR ELS DIES QUE FALTA AFEGIR
                        int diesAfegir = 5;
                        Date lastDate;
                        try {
                            lastDate = format2.parse(lastDateString);
                        } catch (ParseException e) {
                            e.printStackTrace();
                            return;
                        }
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(lastDate);

                        // Calcula els dies que ja existeixen fins a la data actual
                        Date currentDateFormatted = null;
                        try {
                            currentDateFormatted = format2.parse(currentDateString);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        int diesFinsData = (int) ((lastDate.getTime() - currentDateFormatted.getTime()) / (1000 * 60 * 60 * 24));

                        // Si la diferència de dies és superior a 0 els restem
                        if (diesFinsData > 0) { diesAfegir -= diesFinsData; }
                        final int finalDiesAfegir = diesAfegir;

                        db.collection("CLIENTS").get().addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                List<DocumentSnapshot> clients = task1.getResult().getDocuments();
                                Random random = new Random();

                                // Afegeix una pista per cada esport a cada hora de cada dia
                                for (int dia = 1; dia <= finalDiesAfegir; dia++) {
                                    calendar.add(Calendar.DATE, 1);
                                    for (String esport : esports) {
                                        for (int hora = 9; hora < 21; hora++) {
                                            Date date = calendar.getTime();

                                            String dataString1 = format1.format(date);
                                            String dataString2 = format2.format(date);
                                            String horaString = String.format("%02d", hora);

                                            Map<String, Object> pista = new HashMap<>();
                                            pista.put("dataOrdenada", dataString2); // Data per ordenar
                                            pista.put("data", dataString1); // Data de la reserva
                                            pista.put("hora", horaString);  // Hora de la reserva
                                            pista.put("esport", esport);    // Esport de la reserva
                                            pista.put("material", esportsMaterials.get(esport)); // Material de l'esport
                                            boolean reservada = random.nextBoolean();
                                            pista.put("reservada", reservada);  // Estat de la reserva
                                            String idClient = null;

                                            // ID de la pista data_hora_esport per localitzar la base de dades a vista de desenvolupador
                                            String documentId = dataString2 + "_" + horaString + "_" + esport;

                                            // Si ha estat reservada li assignem un client aleatori
                                            if (reservada) {
                                                DocumentSnapshot client = clients.get(random.nextInt(clients.size()));
                                                idClient = client.getId();
                                                pista.put("idClient", idClient);

                                                // Actualitzar el document del client amb la nova pista
                                                db.collection("CLIENTS").document(idClient)
                                                        .update("idPistes", FieldValue.arrayUnion(documentId))
                                                        .addOnFailureListener(e -> Log.e("updateDataBase", "Error actualizant idPistes", e));
                                            }

                                            // Crear o actualizar el documento de la pista
                                            db.collection("PISTES").document(documentId).set(pista, SetOptions.merge())
                                                    .addOnFailureListener(e -> Log.e("updateDataBase", "Error creant la pista: " + documentId, e));
                                        }
                                    }
                                }
                            } else {
                                Log.d("updateDataBase", "Error recuperant els documents dels clients: ", task1.getException());
                            }
                        });
                    } else {
                        Log.d("updateDataBase", "Error getting documents: ", task.getException());
                    }
                });


        // AFEGIR ELS DIES POSTERIORS A LA DATA D'AVUI PER PARTITS
        db.collection("PARTITS")
                .orderBy("dataOrdenada", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String lastDateString = currentDateString;
                        if (!task.getResult().isEmpty()) {
                            // Si hi ha elements a la base de dades, agafem la data més llunyana
                            lastDateString = task.getResult().getDocuments().get(0).getString("dataOrdenada");
                        }

                        // CALCULAR ELS DIES QUE FALTA AFEGIR
                        int diesAfegir = 5;
                        Date lastDate;
                        try {
                            lastDate = format2.parse(lastDateString);
                        } catch (ParseException e) {
                            e.printStackTrace();
                            return;
                        }
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(lastDate);

                        // Calcula els dies que ja existeixen fins a la data actual
                        Date currentDateFormatted = null;
                        try {
                            currentDateFormatted = format2.parse(currentDateString);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        int diesFinsData = (int) ((lastDate.getTime() - currentDateFormatted.getTime()) / (1000 * 60 * 60 * 24));

                        // Si la diferència de dies és superior a 0 els restem
                        if (diesFinsData > 0) { diesAfegir -= diesFinsData; }

                        // Afegeix una pista per cada esport a cada hora de cada dia
                        for (int dia = 1; dia <= diesAfegir; dia++) {
                            calendar.add(Calendar.DATE, 1);
                            for (String esport : esports) {
                                for (int hora = 9; hora < 21; hora++) {
                                    Date date = calendar.getTime();
                                    Random random = new Random();

                                    String dataString1 = format1.format(date);
                                    String dataString2 = format2.format(date);
                                    String horaString = String.format("%02d", hora);

                                    Map<String, Object> partit = new HashMap<>();
                                    partit.put("dataOrdenada", dataString2); // Data per ordenar
                                    partit.put("data", dataString1); // Data de la reserva
                                    partit.put("hora", horaString);  // Hora de la reserva
                                    partit.put("esport", esport);    // Esport de la reserva
                                    partit.put("material", esportsMaterials.get(esport)); // Material de l'esport
                                    String nivell = nivells[random.nextInt(nivells.length)];
                                    partit.put("nivellDificultat", nivell); // Nivell de dificultat

                                    partit.put("idParticipants", new ArrayList<>());    // ID del client que ha fet la reserva
                                    partit.put("idReserva", dataString2 + "_" + horaString + "_" + esport);   // ID de la reserva (data_hora_esport)

                                    int maxParticipants = esportsParticipants.get(esport);
                                    partit.put("maxParticipants", maxParticipants); // Màxim de participants
                                    partit.put("participantsActuals", 0); // Participants actuals

                                    // ID de la pista data_hora_esport per localitzar la base de dades a vista de desenvolupador
                                    String documentId = dataString2 + "_" + horaString + "_" + esport;
                                    db.collection("PARTITS").document(documentId).set(partit, SetOptions.merge())
                                            .addOnSuccessListener(aVoid -> {
                                                // Afegir participants aleatòriament després de crear el document
                                                afegirParticipantsAleatoris(documentId, maxParticipants);
                                            })
                                            .addOnFailureListener(e -> Log.e("updateDataBase", "Error creant el partit", e));
                                }
                            }
                        }
                    } else {
                        Log.d("updateDataBase", "Error getting documents: ", task.getException());
                    }
                });

    }

    // Mètode per afegir participants aleatòriament
    private void afegirParticipantsAleatoris(String documentId, int maxParticipants) {
        db.collection("CLIENTS").get().addOnCompleteListener(task1 -> {
            if (task1.isSuccessful()) {
                List<DocumentSnapshot> clients = task1.getResult().getDocuments();
                Random random = new Random();
                List<String> afegits = new ArrayList<>();

                // Calcula el número de participants a afegir basat en el màxim de participants
                int quantitatAfegir = random.nextInt((int) (maxParticipants * 1) + 1); // 0% a 100% dels participants màxims

                // Mentre quedin participants per afegir i hi hagi espai disponible
                while (quantitatAfegir > 0 && afegits.size() < maxParticipants) {
                    DocumentSnapshot client = clients.get(random.nextInt(clients.size()));
                    String idClient = client.getId();
                    if (!afegits.contains(idClient)) {
                        afegits.add(idClient);

                        // Actualitzar la llista de participants afegint idClient
                        db.collection("PARTITS").document(documentId)
                                .update("idParticipants", FieldValue.arrayUnion(idClient))
                                .addOnSuccessListener(aVoid -> {

                                    // Incrementar el número de participants actuals
                                    db.collection("PARTITS").document(documentId)
                                            .update("participantsActuals", FieldValue.increment(1))
                                            .addOnFailureListener(e -> Log.e("updateDataBase", "Error actualitzant participantsActuals", e));

                                    // Afegir el partit a la llista de partits del client
                                    db.collection("CLIENTS").document(idClient)
                                            .update("idPartits", FieldValue.arrayUnion(documentId))
                                            .addOnFailureListener(e -> Log.e("updateDataBase", "Error actualizant idPartits", e));
                                })
                                .addOnFailureListener(e -> Log.e("updateDataBase", "Error actualitzant idParticipants", e));
                        quantitatAfegir--;
                    }
                }
            } else {
                Log.d("updateDataBase", "Error recuperant els documents dels clients: ", task1.getException());
            }
        });
    }
}
