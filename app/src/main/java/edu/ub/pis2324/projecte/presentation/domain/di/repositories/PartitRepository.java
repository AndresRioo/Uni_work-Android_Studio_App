package edu.ub.pis2324.projecte.presentation.domain.di.repositories;

import java.util.ArrayList;

import edu.ub.pis2324.projecte.presentation.domain.model.entities.Partit;
import edu.ub.pis2324.projecte.presentation.utils.error_handling.XopingError;
import io.reactivex.Observable;

public interface PartitRepository {
    Observable<ArrayList<Partit>> initLlistaPartits(String esport, String data);
    Observable<ArrayList<Partit>> obtenirReservesClient(ArrayList<String> idPartits);
    Observable<String> afegirClient(String correu, String idPartit);
    Observable<String> eliminarClient(String correu, String idPartit);
    Observable<String> eliminarPartits(String dataActual);

    enum Error implements XopingError {
        PARTIT_DESCONEGUT;
    }
}
