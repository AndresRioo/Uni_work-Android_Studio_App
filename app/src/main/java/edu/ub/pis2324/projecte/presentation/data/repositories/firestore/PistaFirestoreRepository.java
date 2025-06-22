package edu.ub.pis2324.projecte.presentation.data.repositories.firestore;


import android.util.Log;

import edu.ub.pis2324.projecte.presentation.data.dtos.firestore.PistaFirestoreDto;
import edu.ub.pis2324.projecte.presentation.domain.di.repositories.ClientRepository;
import edu.ub.pis2324.projecte.presentation.utils.error_handling.XopingThrowable;
import io.reactivex.Observable;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import edu.ub.pis2324.projecte.presentation.domain.di.repositories.PistaRepository;
import edu.ub.pis2324.projecte.presentation.domain.model.entities.Pista;
import edu.ub.pis2324.projecte.presentation.data.dtos.firestore.mappers.DTOToDomainMapper;
import edu.ub.pis2324.projecte.presentation.domain.model.valueobjects.ReservaId;

public class PistaFirestoreRepository implements PistaRepository {
    private final FirebaseFirestore db;
    private final String nomColeccio = "PISTES";
    private final DTOToDomainMapper DTOToDomainMapper;
    private ArrayList<Pista> llistaPistes;

    public PistaFirestoreRepository() {
        db = FirebaseFirestore.getInstance();
        DTOToDomainMapper = new DTOToDomainMapper();
    }

    /**
     * Retorna una llista de pistes disponibles per a un esport i una data concreta
     * Es fa servir a UC12 (visualitzar hores pista)
     * @param esport Esport de la pista
     * @param data Data de la pista
     * @return Observable amb la llista de pistes disponibles
     */
    public Observable<ArrayList<Pista>> obtenirDisponiblesPerDia(String esport, String data) {
        llistaPistes = new ArrayList<>();
        return io.reactivex.Observable.create(emitter -> {
            db.collection(nomColeccio)
                    .whereEqualTo("dataOrdenada", data)
                    .whereEqualTo("esport", esport)
                    .orderBy("hora")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (DocumentSnapshot document : task.getResult()) {
                                    // Només si no està reservada s'afegirà a la llista de disponibles
                                    Boolean reservada = document.getBoolean("reservada");
                                    if (reservada != null && !reservada) {
                                        PistaFirestoreDto dto = document.toObject(PistaFirestoreDto.class);
                                        Pista pista = DTOToDomainMapper.map(dto, Pista.class);
                                        pista.setIdReserva(new ReservaId(document.getId()));
                                        llistaPistes.add(pista);
                                    }
                                }
                                emitter.onNext(llistaPistes);
                                emitter.onComplete();
                            } else {
                                emitter.onError(task.getException());
                            }
                        }
                    });
        });
    }


    /**
     * Retorna una llista de pistes reservades per un client
     * Es fa servir a UC6 (visualitzar reserves)
     * @param idPistes Llista amb els ID de les pistes a recuperar
     * @return llista de pistes reservades
     */
    public Observable<ArrayList<Pista>> obtenirReservesClient(ArrayList<String> idPistes) {
        llistaPistes = new ArrayList<>();
        AtomicInteger consultesCompletades = new AtomicInteger(0);
        return io.reactivex.Observable.create(emitter -> {
            for (String idReserva : idPistes) {
                db.collection(nomColeccio).document(idReserva).get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    PistaFirestoreDto dto = document.toObject(PistaFirestoreDto.class);
                                    Pista pista = DTOToDomainMapper.map(dto, Pista.class);
                                    pista.setIdReserva(new ReservaId(document.getId()));
                                    llistaPistes.add(pista);
                                } else {
                                    emitter.onError(new XopingThrowable(Error.PISTA_DESCONEGUDA));
                                    return;
                                }
                            } else {
                                emitter.onError(task.getException());
                                return;
                            }

                            // Incrementar el comptador de consultes completades
                            int count = consultesCompletades.incrementAndGet();

                            // Si totes les consultes s'han completat, retornar la llista de pistes
                            if (count == idPistes.size()) {
                                emitter.onNext(llistaPistes);
                                emitter.onComplete();
                            }
                        });
            }
        });
    }


    /**
     * Afegeix el client amb l'id rebut per paràmetre a la pista rebuda per paràmetre
     * Es fa servir a UC10 (reservar pista)
     * @param correu correu del client
     * @param idPista id de la pista
     * @return èxit o error
     */
    public Observable<String> afegirClient(String correu, String idPista) {
        return io.reactivex.Observable.create(emitter -> {
            db.collection(nomColeccio).document(idPista).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        // Actualiza el camp idClient amb el correu del client que l'ha reservat
                        db.collection(nomColeccio).document(idPista)
                                .update("idClient", correu)
                                .addOnSuccessListener(aVoid1 -> {

                                    // Actualiza el camp de reservada a true
                                    db.collection(nomColeccio).document(idPista)
                                            .update("reservada", true)
                                            .addOnSuccessListener(aVoid -> {
                                                emitter.onNext("afegit");
                                                emitter.onComplete();
                                            })
                                            .addOnFailureListener(e -> {
                                                emitter.onError(e);
                                            });
                                })
                                .addOnFailureListener(e -> {
                                    emitter.onError(e);
                                });

                    } else {
                        emitter.onError(new XopingThrowable(Error.PISTA_DESCONEGUDA));
                    }
                } else {
                    emitter.onError(task.getException());
                }
            });
        });
    }

    /**
     * Elimina el client amb l'id rebut per paràmetre de la pista rebuda per paràmetre
     * Es fa servir a UC7 (cancel·lar pista)
     * @param correu correu del client
     * @param idPista id de la pista
     * @return èxit o error
     */
    public Observable<String> eliminarClient(String correu, String idPista) {
        return io.reactivex.Observable.create(emitter -> {
            db.collection(nomColeccio).document(idPista).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        // Actualiza el camp de reservada a true
                        db.collection(nomColeccio).document(idPista)
                                .update("reservada", false)
                                .addOnSuccessListener(aVoid -> {

                                    // Actualiza el camp idClient eliminant el correu del client
                                    db.collection(nomColeccio).document(idPista)
                                            .update("idClient", null)
                                            .addOnSuccessListener(aVoid1 -> {
                                                emitter.onNext("eliminat");
                                                emitter.onComplete();
                                            })
                                            .addOnFailureListener(e -> {
                                                emitter.onError(e);
                                            });
                                })
                                .addOnFailureListener(e -> {
                                    emitter.onError(e);
                                });

                    } else {
                        emitter.onError(new XopingThrowable(Error.PISTA_DESCONEGUDA));
                    }
                } else {
                    emitter.onError(task.getException());
                }
            });
        });
    }


    public Observable<String> eliminarPistes(String dataActual) {
        return Observable.create(emitter -> {
            db.collection(nomColeccio)
                    .whereLessThanOrEqualTo("dataOrdenada", dataActual).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Per cada element amb data més petita o igual que l'actual, eliminar-lo
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                db.collection(nomColeccio).document(document.getId()).delete();
                            }
                            emitter.onNext("exit");
                            emitter.onComplete();
                        } else {
                            emitter.onError(task.getException());
                        }
                    });
        });
    }

}

