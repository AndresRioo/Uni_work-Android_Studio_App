package edu.ub.pis2324.projecte.presentation.domain.usecases;

import io.reactivex.Observable;
import edu.ub.pis2324.projecte.presentation.utils.error_handling.XopingError;

public interface CanviarContrasenyaUseCase {
    Observable<String> canviarContrasenya(String correu, String contrasenya, String novaContrasenya, String repeticioContrasenya);
    enum Error implements XopingError {
        CLIENT_DESCONEGUT,
        CONTRASENYA_INCORRECTA,
        REPETICIO_CONTRASENYA_INCORRECTA,
        NOVA_CONTRASENYA_IGUAL_ANTIGUA;
    }
}
