package edu.ub.pis2324.projecte.presentation.domain.usecases;

import java.util.ArrayList;

import edu.ub.pis2324.projecte.presentation.domain.model.entities.Pista;
import edu.ub.pis2324.projecte.presentation.utils.error_handling.XopingError;
import io.reactivex.Observable;

public interface VisualitzarHoresPistaUseCase {
    Observable<ArrayList<Pista>> initLlistaPistes(String esport, String data);
    enum Error implements XopingError {
        LLISTA_BUIDA;
    }
}
