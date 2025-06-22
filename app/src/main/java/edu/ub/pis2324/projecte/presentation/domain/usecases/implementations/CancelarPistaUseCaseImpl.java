package edu.ub.pis2324.projecte.presentation.domain.usecases.implementations;

import edu.ub.pis2324.projecte.presentation.domain.di.repositories.ClientRepository;
import edu.ub.pis2324.projecte.presentation.domain.di.repositories.PistaRepository;
import edu.ub.pis2324.projecte.presentation.domain.usecases.CancelarPistaUseCase;
import edu.ub.pis2324.projecte.presentation.utils.error_handling.XopingThrowableMapper;
import io.reactivex.Observable;

public class CancelarPistaUseCaseImpl implements CancelarPistaUseCase {
    private final ClientRepository clientRepository;
    private final PistaRepository pistaRepository;
    private final XopingThrowableMapper throwableMapper;

    public CancelarPistaUseCaseImpl(ClientRepository clientRepository, PistaRepository pistaRepository) {
        this.clientRepository = clientRepository;
        this.pistaRepository = pistaRepository;

        throwableMapper = new XopingThrowableMapper();
        throwableMapper.add(ClientRepository.Error.CLIENT_DESCONEGUT, CancelarPistaUseCase.Error.CLIENT_DESCONEGUT);
        throwableMapper.add(PistaRepository.Error.PISTA_DESCONEGUDA, CancelarPistaUseCase.Error.PISTA_DESCONEGUDA);
    }

    public Observable<String> cancelar(String correu, String idPista) {
        return clientRepository.eliminarPista(correu, idPista)
                .concatMap(ignored -> pistaRepository.eliminarClient(correu,idPista)
                        .onErrorResumeNext((Throwable throwable) -> Observable.error(throwableMapper.map(throwable))))
                .onErrorResumeNext((Throwable throwable) -> Observable.error(throwableMapper.map(throwable)));
    }
}
