package edu.ub.pis2324.projecte.presentation.domain.usecases;

import java.util.ArrayList;

import edu.ub.pis2324.projecte.presentation.domain.model.entities.Partit;
import edu.ub.pis2324.projecte.presentation.utils.error_handling.XopingError;
import io.reactivex.Observable;

public interface VisualitzarHoresPartitUseCase {
    Observable<ArrayList<Partit>> initLlistaPartits(String esport, String data, String correu);
    enum Error implements XopingError {
        LLISTA_BUIDA;
    }
}
