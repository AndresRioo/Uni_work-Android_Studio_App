package edu.ub.pis2324.projecte.presentation.domain.usecases.implementations;

import java.util.ArrayList;

import edu.ub.pis2324.projecte.presentation.domain.di.repositories.PistaRepository;
import edu.ub.pis2324.projecte.presentation.domain.model.entities.Pista;
import edu.ub.pis2324.projecte.presentation.domain.usecases.VisualitzarHoresPistaUseCase;

import edu.ub.pis2324.projecte.presentation.utils.error_handling.XopingThrowable;
import io.reactivex.Observable;
import edu.ub.pis2324.projecte.presentation.utils.error_handling.XopingThrowableMapper;

public class VisualitzarHoresPistaUseCaseImpl implements VisualitzarHoresPistaUseCase {
    private final PistaRepository pistaRepository;
    private final XopingThrowableMapper throwableMapper;

    public VisualitzarHoresPistaUseCaseImpl(PistaRepository pistaRepository) {
        this.pistaRepository = pistaRepository;

        throwableMapper = new XopingThrowableMapper();
    }

    public Observable<ArrayList<Pista>> initLlistaPistes(String esport, String data) {
        return pistaRepository.obtenirDisponiblesPerDia(esport, data)
                .concatMap(llistaPistes -> {
                    if (llistaPistes.isEmpty()) {
                        return Observable.error(new XopingThrowable(VisualitzarHoresPistaUseCase.Error.LLISTA_BUIDA));
                    } else {
                        return Observable.just(llistaPistes);
                    }
                })
                .onErrorResumeNext((Throwable throwable) -> Observable.error(throwableMapper.map(throwable)));
    }
}
