package edu.ub.pis2324.projecte.presentation.data.repositories.firestore;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import edu.ub.pis2324.projecte.presentation.data.dtos.firestore.PartitFirestoreDto;
import edu.ub.pis2324.projecte.presentation.data.dtos.firestore.mappers.DTOToDomainMapper;
import edu.ub.pis2324.projecte.presentation.domain.di.repositories.ClientRepository;
import edu.ub.pis2324.projecte.presentation.domain.di.repositories.PartitRepository;
import edu.ub.pis2324.projecte.presentation.domain.di.repositories.PistaRepository;
import edu.ub.pis2324.projecte.presentation.domain.model.entities.Partit;
import edu.ub.pis2324.projecte.presentation.domain.model.valueobjects.ReservaId;
import edu.ub.pis2324.projecte.presentation.utils.error_handling.XopingThrowable;
import io.reactivex.Observable;

public class PartitFirestoreRepository implements PartitRepository {
    private final FirebaseFirestore db;
    private final String nomColeccio = "PARTITS";
    private final DTOToDomainMapper DTOToDomainMapper;
    private ArrayList<Partit> llistaPartits;

    public PartitFirestoreRepository() {
        db = FirebaseFirestore.getInstance();
        DTOToDomainMapper = new DTOToDomainMapper();
    }

    /**
     * Retorna una llista de partits disponibles per a un esport i una data concreta
     * @param esport Esport del partit
     * @param data Data del partit
     * @return Observable amb la llista de partits disponibles
     */
    public Observable<ArrayList<Partit>> initLlistaPartits(String esport, String data) {
        llistaPartits = new ArrayList<>();
        return io.reactivex.Observable.create(emitter -> {
            db.collection(nomColeccio)
                    .whereEqualTo("dataOrdenada", data)
                    .whereEqualTo("esport", esport)
                    .orderBy("hora")
                    .get()
                    .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                for (DocumentSnapshot document : task.getResult()) {
                                    // Només si hi ha espais disponibles s'afegirà a la llista de disponibles
                                    int actuals = document.getLong("participantsActuals").intValue();
                                    int max = document.getLong("maxParticipants").intValue();
                                    if (actuals < max) {
                                        PartitFirestoreDto dto = document.toObject(PartitFirestoreDto.class);
                                        Partit partit = DTOToDomainMapper.map(dto, Partit.class);
                                        partit.setIdReserva(new ReservaId(document.getId()));
                                        llistaPartits.add(partit);
                                    }
                                }
                                emitter.onNext(llistaPartits);
                                emitter.onComplete();
                            } else {
                                emitter.onError(task.getException());
                            }
                    });
        });
    }


    /**
     * Retorna una llista de partits reservats per un client
     * Es fa servir a UC6 (visualitzar reserves)
     * @param idPartits Llista amb els ID dels partits a recuperar
     * @return llista de partits reservats
     */
    public Observable<ArrayList<Partit>> obtenirReservesClient(ArrayList<String> idPartits) {
        llistaPartits = new ArrayList<>();
        AtomicInteger consultesCompletades = new AtomicInteger(0);
        return io.reactivex.Observable.create(emitter -> {
            for (String idReserva : idPartits) {
                db.collection(nomColeccio).document(idReserva).get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    PartitFirestoreDto dto = document.toObject(PartitFirestoreDto.class);
                                    Partit partit = DTOToDomainMapper.map(dto, Partit.class);
                                    partit.setIdReserva(new ReservaId(document.getId()));
                                    llistaPartits.add(partit);
                                } else {
                                    emitter.onError(new XopingThrowable(Error.PARTIT_DESCONEGUT));
                                    return;
                                }
                            } else {
                                emitter.onError(task.getException());
                                return;
                            }

                            // Incrementar el comptador de consultes completades
                            int count = consultesCompletades.incrementAndGet();

                            // Si totes les consultes s'han completat, retornar la llista de pistes
                            if (count == idPartits.size()) {
                                emitter.onNext(llistaPartits);
                                emitter.onComplete();
                            }
                        });
            }
        });
    }


    /**
     * Afegeix el client amb l'id rebut per paràmetre al partit rebut per paràmetre
     * @param correu correu del client
     * @param idPartit id del partit
     * @return èxit o error
     */
    public Observable<String> afegirClient(String correu, String idPartit) {
        return io.reactivex.Observable.create(emitter -> {
            db.collection(nomColeccio).document(idPartit).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        // Actualiza el camp de participants afegint el correu del client que s'uneix
                        db.collection(nomColeccio).document(idPartit)
                                .update("idParticipants", FieldValue.arrayUnion(correu))
                                .addOnSuccessListener(aVoid -> {

                                    // Incrementa en 1 el número de participants actuals
                                    db.collection(nomColeccio).document(idPartit)
                                            .update("participantsActuals", FieldValue.increment(1))
                                            .addOnSuccessListener(aVoid1 -> {
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
                        emitter.onError(new XopingThrowable(Error.PARTIT_DESCONEGUT));
                    }
                } else {
                    emitter.onError(task.getException());
                }
            });
        });
    }


    /**
     * Elimina el client amb l'id rebut per paràmetre del partit per paràmetre
     * Es fa servir a UC8 (cancel·lar partit)
     * @param correu correu del client
     * @param idPartit id del partit
     * @return èxit o error
     */
    public Observable<String> eliminarClient(String correu, String idPartit) {
        return io.reactivex.Observable.create(emitter -> {
            db.collection(nomColeccio).document(idPartit).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        // Actualiza el camp de participants eliminant el correu del client que marxa
                        db.collection(nomColeccio).document(idPartit)
                                .update("idParticipants", FieldValue.arrayRemove(correu))
                                .addOnSuccessListener(aVoid -> {

                                    // Redueix en 1 el número de participants actuals
                                    db.collection(nomColeccio).document(idPartit)
                                            .update("participantsActuals", FieldValue.increment(-1))
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
                        emitter.onError(new XopingThrowable(Error.PARTIT_DESCONEGUT));
                    }
                } else {
                    emitter.onError(task.getException());
                }
            });
        });
    }



    public Observable<String> eliminarPartits(String dataActual) {
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
