package edu.ub.pis2324.projecte.presentation.domain.usecases.implementations;

import edu.ub.pis2324.projecte.presentation.domain.di.repositories.ClientRepository;
import edu.ub.pis2324.projecte.presentation.domain.di.repositories.PartitRepository;
import edu.ub.pis2324.projecte.presentation.domain.usecases.CancelarPartitUseCase;
import edu.ub.pis2324.projecte.presentation.utils.error_handling.XopingThrowableMapper;
import io.reactivex.Observable;

public class CancelarPartitUseCaseImpl implements CancelarPartitUseCase {
    private final ClientRepository clientRepository;
    private final PartitRepository partitRepository;
    private final XopingThrowableMapper throwableMapper;

    public CancelarPartitUseCaseImpl(ClientRepository clientRepository, PartitRepository partitRepository) {
        this.clientRepository = clientRepository;
        this.partitRepository = partitRepository;

        throwableMapper = new XopingThrowableMapper();
        throwableMapper.add(ClientRepository.Error.CLIENT_DESCONEGUT, CancelarPartitUseCase.Error.CLIENT_DESCONEGUT);
        throwableMapper.add(PartitRepository.Error.PARTIT_DESCONEGUT, CancelarPartitUseCase.Error.PARTIT_DESCONEGUT);
    }

    public Observable<String> cancelar(String correu, String idPartit) {
        return clientRepository.eliminarPartit(correu, idPartit)
                .concatMap(ignored -> partitRepository.eliminarClient(correu,idPartit)
                        .onErrorResumeNext((Throwable throwable) -> Observable.error(throwableMapper.map(throwable))))
                .onErrorResumeNext((Throwable throwable) -> Observable.error(throwableMapper.map(throwable)));
    }
}
