package edu.ub.pis2324.projecte.presentation.domain.usecases;

import java.util.Map;

import edu.ub.pis2324.projecte.presentation.utils.error_handling.XopingError;
import io.reactivex.Observable;

public interface SignUpUseCase {
    Observable<String> crearClient(String nom, String correu, String contrasenya, String repeticioContrasenya);

    enum Error implements XopingError {
        CLIENT_EXISTENT,
        CAMPS_INCOMPLETS,
        REPETICIO_CONTRASENYA_INCORRECTA;
    }
}
