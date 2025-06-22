package edu.ub.pis2324.projecte.presentation.domain.usecases.implementations;

import edu.ub.pis2324.projecte.presentation.domain.di.repositories.ClientRepository;
import edu.ub.pis2324.projecte.presentation.domain.di.repositories.PartitRepository;
import edu.ub.pis2324.projecte.presentation.domain.usecases.ReservarPartitUseCase;
import edu.ub.pis2324.projecte.presentation.utils.error_handling.XopingThrowableMapper;
import io.reactivex.Observable;

public class ReservarPartitUseCaseImpl implements ReservarPartitUseCase {
    private final ClientRepository clientRepository;
    private final PartitRepository partitRepository;
    private final XopingThrowableMapper throwableMapper;

    public ReservarPartitUseCaseImpl(ClientRepository clientRepository, PartitRepository partitRepository) {
        this.clientRepository = clientRepository;
        this.partitRepository = partitRepository;

        throwableMapper = new XopingThrowableMapper();
        throwableMapper.add(ClientRepository.Error.CLIENT_DESCONEGUT, ReservarPartitUseCase.Error.CLIENT_DESCONEGUT);
        throwableMapper.add(PartitRepository.Error.PARTIT_DESCONEGUT, ReservarPartitUseCase.Error.PARTIT_DESCONEGUT);
    }

    public Observable<String> reservar(String correu, String idPartit) {
        return clientRepository.afegirPartit(correu, idPartit)
                .concatMap(ignored -> partitRepository.afegirClient(correu,idPartit)
                        .onErrorResumeNext((Throwable throwable) -> Observable.error(throwableMapper.map(throwable))))
                .onErrorResumeNext((Throwable throwable) -> Observable.error(throwableMapper.map(throwable)));
    }
}
