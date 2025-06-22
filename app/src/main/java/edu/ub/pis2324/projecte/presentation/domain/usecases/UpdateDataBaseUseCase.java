package edu.ub.pis2324.projecte.presentation.domain.usecases;

import edu.ub.pis2324.projecte.presentation.utils.error_handling.XopingError;
import io.reactivex.Observable;

public interface UpdateDataBaseUseCase {
    Observable<String> eliminarPistes(String dataActual);
    Observable<String> eliminarPartits(String dataActual);
    Observable<String> eliminarPistesClient(String dataActual);
    Observable<String> eliminarPartitsClient(String dataActual);
    enum Error implements XopingError {
        CLIENT_DESCONEGUT,
        PISTA_DESCONEGUDA,
        PARTIT_DESCONEGUT;
    }
}
