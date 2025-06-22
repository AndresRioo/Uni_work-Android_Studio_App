package edu.ub.pis2324.projecte.presentation.domain.usecases;

import java.util.ArrayList;

import edu.ub.pis2324.projecte.presentation.domain.model.entities.Reserva;
import edu.ub.pis2324.projecte.presentation.domain.model.entities.Pista;
import edu.ub.pis2324.projecte.presentation.domain.model.entities.Partit;
import edu.ub.pis2324.projecte.presentation.utils.error_handling.XopingError;
import io.reactivex.Observable;

public interface VisualitzarReservesUseCase {
    Observable<ArrayList<Pista>> initLlistaPistes(String correu);
    Observable<ArrayList<Partit>> initLlistaPartits(String correu);
    Observable<ArrayList<? extends Reserva>> initLlistaReserves(String correu);
    enum Error implements XopingError {
        PISTA_DESCONEGUDA,
        PARTIT_DESCONEGUT,
        LLISTA_BUIDA;
    }
}
