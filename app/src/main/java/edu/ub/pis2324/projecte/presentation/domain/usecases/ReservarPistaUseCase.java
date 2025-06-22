package edu.ub.pis2324.projecte.presentation.domain.usecases;

import edu.ub.pis2324.projecte.presentation.utils.error_handling.XopingError;
import io.reactivex.Observable;

public interface ReservarPistaUseCase {
    Observable<String> reservar(String correu, String idPista);
    enum Error implements XopingError {
        CLIENT_DESCONEGUT,
        PISTA_DESCONEGUDA;
    }
}
