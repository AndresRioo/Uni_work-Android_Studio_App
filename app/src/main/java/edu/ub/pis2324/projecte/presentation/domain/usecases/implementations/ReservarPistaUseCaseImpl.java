package edu.ub.pis2324.projecte.presentation.domain.usecases.implementations;

import edu.ub.pis2324.projecte.presentation.domain.di.repositories.ClientRepository;
import edu.ub.pis2324.projecte.presentation.domain.di.repositories.PistaRepository;
import edu.ub.pis2324.projecte.presentation.domain.usecases.ReservarPistaUseCase;
import edu.ub.pis2324.projecte.presentation.utils.error_handling.XopingThrowableMapper;
import io.reactivex.Observable;

public class ReservarPistaUseCaseImpl implements ReservarPistaUseCase {
    private final ClientRepository clientRepository;
    private final PistaRepository pistaRepository;
    private final XopingThrowableMapper throwableMapper;

    public ReservarPistaUseCaseImpl(ClientRepository clientRepository, PistaRepository pistaRepository) {
        this.clientRepository = clientRepository;
        this.pistaRepository = pistaRepository;

        throwableMapper = new XopingThrowableMapper();
        throwableMapper.add(ClientRepository.Error.CLIENT_DESCONEGUT, ReservarPistaUseCase.Error.CLIENT_DESCONEGUT);
        throwableMapper.add(PistaRepository.Error.PISTA_DESCONEGUDA, ReservarPistaUseCase.Error.PISTA_DESCONEGUDA);
    }

    public Observable<String> reservar(String correu, String idPista) {
        return clientRepository.afegirPista(correu, idPista)
                .concatMap(ignored -> pistaRepository.afegirClient(correu,idPista)
                    .onErrorResumeNext((Throwable throwable) -> Observable.error(throwableMapper.map(throwable))))
                .onErrorResumeNext((Throwable throwable) -> Observable.error(throwableMapper.map(throwable)));
    }
}
