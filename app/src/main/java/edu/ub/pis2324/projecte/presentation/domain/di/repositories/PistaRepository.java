package edu.ub.pis2324.projecte.presentation.domain.di.repositories;

import java.util.ArrayList;

import edu.ub.pis2324.projecte.presentation.domain.model.entities.Pista;
import edu.ub.pis2324.projecte.presentation.utils.error_handling.XopingError;
import io.reactivex.Observable;

public interface PistaRepository {
    Observable<ArrayList<Pista>> obtenirDisponiblesPerDia(String esport, String data);
    Observable<ArrayList<Pista>> obtenirReservesClient(ArrayList<String> idPistes);
    Observable<String> afegirClient(String correu, String idPista);
    Observable<String> eliminarClient(String correu, String idPista);
    Observable<String> eliminarPistes(String dataActual);

    enum Error implements XopingError {
        PISTA_DESCONEGUDA;
    }
}
