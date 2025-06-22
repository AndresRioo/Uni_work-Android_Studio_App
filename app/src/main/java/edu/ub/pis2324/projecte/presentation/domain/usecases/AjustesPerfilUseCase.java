package edu.ub.pis2324.projecte.presentation.domain.usecases;

import edu.ub.pis2324.projecte.presentation.utils.error_handling.XopingError;
import io.reactivex.Observable;

public interface AjustesPerfilUseCase {
    Observable<String> getNomPerCorreu(String correu);
    Observable<String> eliminarClient(String correu);
    enum Error implements XopingError {
        CLIENT_DESCONEGUT;
    }
}
