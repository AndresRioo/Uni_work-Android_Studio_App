package edu.ub.pis2324.projecte.presentation.data.repositories.firestore;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.QuerySnapshot;

import edu.ub.pis2324.projecte.presentation.domain.di.repositories.ClientRepository;
import io.reactivex.Observable;
import edu.ub.pis2324.projecte.presentation.utils.error_handling.XopingThrowable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Map;

import java.util.Collections;
import java.util.Iterator;

public class ClientFirestoreRepository implements ClientRepository {
    private final FirebaseFirestore db;
    private final String nomColeccio = "CLIENTS";
    private ArrayList<String> llistaIdPistesReservades;
    private ArrayList<String> llistaIdPartitsReservats;

    public ClientFirestoreRepository() {
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Busca la contrasenya del client rebut per paràmetre a la base de dades
     * Es fa servir a UC1 (iniciar sessió) i UC5 (canviar dades - canviar contrasenya)
     * @param correu correu del client
     * @return contrasenya del client
     */
    public Observable<String> getContrasenyaPerCorreu(String correu) {
        return Observable.create(emitter -> {
            db.collection(nomColeccio)
                    .whereEqualTo("correu", correu)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("ClientFirestoreRepository", "getContrasenyaPerCorreu: " + correu);
                            for (DocumentSnapshot document : task.getResult()) {
                                // Si la contrasenya es troba correctament
                                String contrasenya = document.getString("contrasenya");
                                emitter.onNext(contrasenya);
                                emitter.onComplete();
                                return;
                            }
                            // Si no es troba un client amb aquest correu
                            emitter.onError(new XopingThrowable(Error.CLIENT_DESCONEGUT));
                            emitter.onComplete();
                        } else {
                            // Si hi ha un altre error
                            emitter.onError(task.getException());
                        }
                    });
        });
    }


    /**
     * Busca el nom del client rebut per paràmetre a la base de dades
     * Es fa servir a UC4 (visualitzar perfil)
     * @param correu correu del client
     * @return nom del client
     */
    public Observable<String> getNomPerCorreu(String correu) {
        return Observable.create(emitter -> {
            db.collection(nomColeccio)
                    .whereEqualTo("correu", correu)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("ClientFirestoreRepository", "getNomPerCorreu: " + correu);
                            for (DocumentSnapshot document : task.getResult()) {
                                // Si el nom es troba correctament
                                String nom = document.getString("nom");
                                emitter.onNext(nom);
                                emitter.onComplete();
                                return;
                            }
                            // Si no es troba un client amb aquest correu
                            emitter.onError(new Exception("client"));
                            emitter.onComplete();
                        } else {
                            // Si hi ha un altre error
                            emitter.onError(task.getException());
                        }
                    });
        });
    }


    /**
     * Canvia la conrtasenya del client rebut per paràmetre a la base de dades
     * Es fa servir a UC5 (canviar dades - canviar contrasenya)
     * @param correu correu del client
     * @param novaContrasenya nova contrasenya del client
     * @return èxit o error
     */
    public Observable<String> canviarContrasenya(String correu, String novaContrasenya) {
        return Observable.create(emitter -> {
            db.collection(nomColeccio)
                    .whereEqualTo("correu", correu)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("ClientFirestoreRepository", "canviarContrasenya: " + correu);
                            if (!task.getResult().isEmpty()) {
                                DocumentSnapshot document = task.getResult().getDocuments().get(0);
                                document.getReference().update("contrasenya", novaContrasenya)
                                        .addOnSuccessListener(aVoid -> {
                                            // Si la contrasenya es canvia correctament
                                            emitter.onNext("actualitzada");
                                            emitter.onComplete();
                                        })
                                        .addOnFailureListener(e -> {
                                            // Si hi ha un altre error
                                            emitter.onError(task.getException());
                                        });
                            } else {
                                // Si no es troba un client amb aquest correu
                                emitter.onError(new XopingThrowable(Error.CLIENT_DESCONEGUT));
                                emitter.onComplete();
                            }
                        } else {
                            // Si hi ha un altre error
                            emitter.onError(task.getException());
                        }
                    });
        });
    }


    /**
     * Elimina el client amb el correu rebut per paràmetre de la base de dades
     * Es fa servir a UC5 (canviar dades - eliminar compte)
     * @param correu correu del client
     * @return èxit o error
     */
    public Observable<String> eliminarClient(String correu) {
        return Observable.create(emitter -> {
            db.collection(nomColeccio)
                    .whereEqualTo("correu", correu)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("ClientFirestoreRepository", "eliminarClient: " + correu);
                            if (!task.getResult().isEmpty()) {
                                DocumentSnapshot document = task.getResult().getDocuments().get(0);
                                document.getReference().delete()
                                        .addOnSuccessListener(aVoid -> {
                                            emitter.onNext("eliminat");
                                            emitter.onComplete();
                                        })
                                        .addOnFailureListener(e -> {
                                            emitter.onError(e);
                                        });
                            } else {
                                // Si no es troba un client amb aquest correu
                                emitter.onError(new XopingThrowable(Error.CLIENT_DESCONEGUT));
                                emitter.onComplete();
                            }
                        } else {
                            // Si hi ha un error a la consulta
                            emitter.onError(task.getException());
                        }
                    });
        });
    }


    /**
     * Afegeix el client amb el correu rebut per paràmetre a la base de dades
     * Es fa servir a UC2 (crear un compte)
     * @param correu correu del client = id del document
     * @param infoClient informació del client (nom, contrasenya, etc.)
     * @return èxit o error
     */
    public Observable<String> crearClient(String correu, Map<String, Object> infoClient) {
        return Observable.create(emitter -> {
            db.collection(nomColeccio).document(correu).get()
                    .addOnSuccessListener(document -> {
                        if (document.exists()) {
                            emitter.onError(new XopingThrowable(Error.CLIENT_EXISTENT));
                        } else {
                            Log.d("ClientFirestoreRepository", "crearClient: " + correu);
                            db.collection(nomColeccio).document(correu).set(infoClient, SetOptions.merge())
                                    .addOnSuccessListener(aVoid -> {
                                        emitter.onNext("creat");
                                        emitter.onComplete();
                                    })
                                    .addOnFailureListener(e -> {
                                        emitter.onError(e);
                                    });
                        }
                    })
                    .addOnFailureListener(e -> {
                        emitter.onError(e);
                    });
        });
    }


    /**
     * Afegeix la pista amb l'id rebut per paràmetre a la llista de pistes del client
     * Es fa servir a UC10 (reservar pista)
     * @param correu correu del client
     * @param idPista id de la pista
     * @return èxit o error
     */
    public Observable<String> afegirPista(String correu, String idPista) {
        return Observable.create(emitter -> {
            db.collection(nomColeccio).document(correu).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot clientDocument = task.getResult();
                            if (clientDocument.exists()) {

                                Object idPistesObj = clientDocument.get("idPistes");
                                ArrayList<String> idPistes = new ArrayList<>();

                                if (idPistesObj instanceof String) {
                                    idPistes.add((String) idPistesObj);
                                    idPistes.add(idPista);
                                } else if (idPistesObj instanceof ArrayList) {
                                    idPistes = (ArrayList<String>) idPistesObj;
                                    idPistes.add(idPista);
                                }

                                // Actialitza el camp idPartits del client amb la reserva feta
                                db.collection(nomColeccio).document(correu)
                                        .update("idPistes", idPistes)
                                        .addOnSuccessListener(aVoid -> {
                                            emitter.onNext("Pista afegisa");
                                            emitter.onComplete();
                                        })
                                        .addOnFailureListener(e -> emitter.onError(e));

                            } else {
                                // Si no es troba un client amb aquest correu
                                emitter.onError(new XopingThrowable(Error.CLIENT_DESCONEGUT));
                                emitter.onComplete();
                            }
                        } else {
                            // Si hi ha un error a la consulta
                            emitter.onError(task.getException());
                        }
                    });
        });
    }


    /**
     * Afegeix el partit amb l'id rebut per paràmetre a la llista de partits del client
     * Es fa servir a UC13 (reservar partit)
     * @param correu correu del client
     * @param idPartit id del partit
     * @return èxit o error
     */
    public Observable<String> afegirPartit(String correu, String idPartit) {
        return Observable.create(emitter -> {
            db.collection(nomColeccio).document(correu).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot clientDocument = task.getResult();
                            if (clientDocument.exists()) {

                                Object idPartitsObj = clientDocument.get("idPartits");
                                ArrayList<String> idPartits = new ArrayList<>();

                                if (idPartitsObj instanceof String) {
                                    idPartits.add((String) idPartitsObj);
                                    idPartits.add(idPartit);
                                } else if (idPartitsObj instanceof ArrayList) {
                                    idPartits = (ArrayList<String>) idPartitsObj;
                                    idPartits.add(idPartit);
                                }

                                // Actialitza el camp idPartits del client amb la reserva feta
                                db.collection(nomColeccio).document(correu)
                                        .update("idPartits", idPartits)
                                        .addOnSuccessListener(aVoid -> {
                                            emitter.onNext("Partit afegit");
                                            emitter.onComplete();
                                        })
                                        .addOnFailureListener(e -> emitter.onError(e));

                            } else {
                                // Si no es troba un client amb aquest correu
                                emitter.onError(new XopingThrowable(Error.CLIENT_DESCONEGUT));
                                emitter.onComplete();
                            }
                        } else {
                            // Si hi ha un error a la consulta
                            emitter.onError(task.getException());
                        }
                    });
        });
    }


    /**
     * Elimina la pista amb l'id rebut per paràmetre de la llista de pistes del client
     * Es fa servir a UC7 (cancel·lar pista)
     * @param correu correu del client
     * @param idPista id de la pista
     * @return èxit o error
     */
    public Observable<String> eliminarPista(String correu, String idPista) {
        return Observable.create(emitter -> {
            db.collection(nomColeccio).document(correu).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot clientDocument = task.getResult();
                            if (clientDocument.exists()) {

                                Object idPistesObj = clientDocument.get("idPistes");
                                ArrayList<String> idPistes = new ArrayList<>();

                                if (idPistesObj instanceof String) {
                                    String idPistaUnica = (String) idPistesObj;
                                    if (!idPistaUnica.equals(idPista)) {
                                        emitter.onError(new XopingThrowable(Error.PISTA_DESCONEGUDA));
                                    }

                                } else if (idPistesObj instanceof ArrayList) {
                                    idPistes = (ArrayList<String>) idPistesObj;
                                    if (idPistes.contains(idPista)) {
                                        idPistes.remove(idPista);
                                    } else {
                                        emitter.onError(new XopingThrowable(Error.PISTA_DESCONEGUDA));
                                    }
                                }

                                // Actualiza el camp idPartits del client excloent el partit cancel·lat
                                db.collection(nomColeccio).document(correu)
                                        .update("idPistes", idPistes)
                                        .addOnSuccessListener(aVoid -> {
                                            emitter.onNext("Pista cancel·lada");
                                            emitter.onComplete();
                                        })
                                        .addOnFailureListener(e -> emitter.onError(e));

                            } else {
                                // Si no es troba un client amb aquest correu
                                emitter.onError(new XopingThrowable(Error.CLIENT_DESCONEGUT));
                                emitter.onComplete();
                            }
                        } else {
                            // Si hi ha un error a la consulta
                            emitter.onError(task.getException());
                        }
                    });
        });
    }


    /**
     * Elimina la pista amb l'id rebut per paràmetre de la llista de pistes del client
     * Es fa servir a UC8 (cancel·lar partit)
     * @param correu correu del client
     * @param idPartit id del partit
     * @return èxit o error
     */
    public Observable<String> eliminarPartit(String correu, String idPartit) {
        return Observable.create(emitter -> {
            db.collection(nomColeccio).document(correu).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot clientDocument = task.getResult();
                            if (clientDocument.exists()) {

                                Object idPartitsObj = clientDocument.get("idPartits");
                                ArrayList<String> idPartits = new ArrayList<>();

                                if (idPartitsObj instanceof String) {
                                    String idPartitUnic = (String) idPartitsObj;
                                    if (!idPartitUnic.equals(idPartit)) {
                                        emitter.onError(new XopingThrowable(Error.PARTIT_DESCONEGUT));
                                    }

                                } else if (idPartitsObj instanceof ArrayList) {
                                    idPartits = (ArrayList<String>) idPartitsObj;
                                    if (idPartits.contains(idPartit)) {
                                        idPartits.remove(idPartit);
                                    } else {
                                        emitter.onError(new XopingThrowable(Error.PARTIT_DESCONEGUT));
                                    }
                                }

                                // Actualiza el camp idPartits del client excloent el partit cancel·lat
                                db.collection(nomColeccio).document(correu)
                                        .update("idPartits", idPartits)
                                        .addOnSuccessListener(aVoid -> {
                                            emitter.onNext("Partit cancel·lat");
                                            emitter.onComplete();
                                        })
                                        .addOnFailureListener(e -> emitter.onError(e));

                            } else {
                                // Si no es troba un client amb aquest correu
                                emitter.onError(new XopingThrowable(Error.CLIENT_DESCONEGUT));
                                emitter.onComplete();
                            }
                        } else {
                            // Si hi ha un error a la consulta
                            emitter.onError(task.getException());
                        }
                    });
        });
    }


    /**
     * Retorna una llista amb els id de les pistes reservades pel client
     * Es fa servir a UC6 (visualitzar reserves)
     * @param correu correu del client
     * @return llista amb els id de les pistes reservades
     */
    public Observable<ArrayList<String>> obtenirPistes(String correu) {
        llistaIdPistesReservades = new ArrayList<>();
        return Observable.create(emitter -> {
            db.collection(nomColeccio).document(correu).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {

                                Object idPistesObj = document.get("idPistes");
                                if (idPistesObj instanceof String) {
                                    // Si hi ha un únic id sera String
                                    String idPistaUnica = (String) idPistesObj;
                                    llistaIdPistesReservades.add(idPistaUnica);

                                } else if (idPistesObj instanceof ArrayList) {
                                    // Si hi ha més d'un id serà ArrayList
                                    llistaIdPistesReservades = (ArrayList<String>) idPistesObj;
                                }

                                emitter.onNext(llistaIdPistesReservades);
                                emitter.onComplete();
                            } else {
                                emitter.onError(new XopingThrowable(Error.CLIENT_DESCONEGUT));
                            }
                        } else {
                            emitter.onError(task.getException());
                        }
                    });
        });
    }


    /**
     * Retorna una llista amb els id dels partits reservats pel client
     * Es fa servir a UC6 (visualitzar reserves)
     * @param correu correu del client
     * @return llista amb els id dels partits reservats
     */
    public Observable<ArrayList<String>> obtenirPartits(String correu) {
        llistaIdPartitsReservats = new ArrayList<>();
        return Observable.create(emitter -> {
            db.collection(nomColeccio).document(correu).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {

                            Object idPartitsObj = document.get("idPartits");
                            if (idPartitsObj instanceof String) {
                                // Si hi ha un únic id sera String
                                String idPartitUnic = (String) idPartitsObj;
                                llistaIdPartitsReservats.add(idPartitUnic);

                            } else if (idPartitsObj instanceof ArrayList) {
                                // Si hi ha més d'un id serà ArrayList
                                llistaIdPartitsReservats = (ArrayList<String>) idPartitsObj;
                            }

                            emitter.onNext(llistaIdPartitsReservats);
                            emitter.onComplete();
                        } else {
                            emitter.onError(new XopingThrowable(Error.CLIENT_DESCONEGUT));
                        }
                    } else {
                        emitter.onError(task.getException());
                    }
            });
        });
    }


    public Observable<String> eliminarPistesPassades(String dataActual) {
        return Observable.create(emitter -> {
            db.collection(nomColeccio).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null && !querySnapshot.isEmpty()) {

                                // Per cada client que hi ha a la base de dades
                                for (DocumentSnapshot clientDocument : querySnapshot.getDocuments()) {
                                    if (clientDocument.exists()) {
                                        ArrayList<String> idPistes = (ArrayList<String>) clientDocument.get("idPistes");
                                        if (idPistes != null) {
                                            // Ordena la llista
                                            Collections.sort(idPistes);

                                            // Elimina les pistes velles
                                            Iterator<String> iterator = idPistes.iterator();
                                            while (iterator.hasNext()) {
                                                String idPista = iterator.next();
                                                String dataPista = idPista.substring(0, 6); // Extrau la part de la data
                                                if (dataPista.compareTo(dataActual) <= 0) {
                                                    iterator.remove();
                                                } else {
                                                    break;
                                                }
                                            }

                                            // Actualiza el document amb la llista modificada
                                            db.collection(nomColeccio).document(clientDocument.getId())
                                                    .update("idPistes", idPistes)
                                                    .addOnFailureListener(e -> emitter.onError(e) );
                                        }
                                    }
                                }
                                emitter.onNext("Totes les pistes eliminades");
                                emitter.onComplete();
                            } else {
                                emitter.onError(new XopingThrowable(Error.CLIENT_DESCONEGUT));
                                emitter.onComplete();
                            }
                        } else {
                            // Si hi ha un error a la consulta
                            emitter.onError(task.getException());
                        }
                    });
        });
    }


    public Observable<String> eliminarPartitsPassats(String dataActual) {
        return Observable.create(emitter -> {
            db.collection(nomColeccio).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null && !querySnapshot.isEmpty()) {

                                // Per cada client que hi ha a la base de dades
                                for (DocumentSnapshot clientDocument : querySnapshot.getDocuments()) {
                                    if (clientDocument.exists()) {
                                        ArrayList<String> idPartits = (ArrayList<String>) clientDocument.get("idPartits");
                                        if (idPartits != null) {
                                            // Ordena la llista
                                            Collections.sort(idPartits);

                                            // Elimina les pistes velles
                                            Iterator<String> iterator = idPartits.iterator();
                                            while (iterator.hasNext()) {
                                                String idPartit = iterator.next();
                                                String dataPartit = idPartit.substring(0, 6); // Extrau la part de la data
                                                if (dataPartit.compareTo(dataActual) <= 0) {
                                                    iterator.remove();
                                                } else {
                                                    break;
                                                }
                                            }

                                            // Actualiza el document amb la llista modificada
                                            db.collection(nomColeccio).document(clientDocument.getId())
                                                    .update("idPartits", idPartits)
                                                    .addOnFailureListener(e -> emitter.onError(e) );
                                        }
                                    }
                                }
                                emitter.onNext("Totes els partits eliminats");
                                emitter.onComplete();
                            } else {
                                emitter.onError(new XopingThrowable(Error.CLIENT_DESCONEGUT));
                                emitter.onComplete();
                            }
                        } else {
                            // Si hi ha un error a la consulta
                            emitter.onError(task.getException());
                        }
                    });
        });
    }

}
