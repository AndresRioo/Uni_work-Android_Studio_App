package edu.ub.pis2324.projecte.presentation.domain.usecases;

import edu.ub.pis2324.projecte.presentation.utils.error_handling.XopingError;
import io.reactivex.Observable;

public interface ReservarPartitUseCase {
    Observable<String> reservar(String correu, String idPartit);
    enum Error implements XopingError {
        CLIENT_DESCONEGUT,
        PARTIT_DESCONEGUT;
    }
}
