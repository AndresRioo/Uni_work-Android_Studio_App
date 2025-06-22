package edu.ub.pis2324.projecte.presentation.domain.usecases;

import edu.ub.pis2324.projecte.presentation.utils.error_handling.XopingError;
import io.reactivex.Observable;

public interface LogInUseCase {

    Observable<String> iniciarClient(String correu, String contrasenya);

    enum Error implements XopingError {
        CLIENT_DESCONEGUT,
        CAMPS_INCOMPLETS,
        CONTRASENYA_INCORRECTA;
    }
}
