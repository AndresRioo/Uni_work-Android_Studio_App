package edu.ub.pis2324.projecte.presentation.domain.usecases.implementations;

import edu.ub.pis2324.projecte.presentation.domain.di.repositories.ClientRepository;
import edu.ub.pis2324.projecte.presentation.domain.di.repositories.PartitRepository;
import edu.ub.pis2324.projecte.presentation.domain.di.repositories.PistaRepository;
import edu.ub.pis2324.projecte.presentation.utils.error_handling.XopingThrowableMapper;
import edu.ub.pis2324.projecte.presentation.domain.usecases.UpdateDataBaseUseCase;
import io.reactivex.Observable;

public class UpdateDataBaseUseCaseImpl implements UpdateDataBaseUseCase {
    private final ClientRepository clientRepository;
    private final PistaRepository pistaRepository;
    private final PartitRepository partitRepository;
    private final XopingThrowableMapper throwableMapper;

    public UpdateDataBaseUseCaseImpl(ClientRepository clientRepository, PistaRepository pistaRepository, PartitRepository partitRepository) {
        this.clientRepository = clientRepository;
        this.pistaRepository = pistaRepository;
        this.partitRepository = partitRepository;

        throwableMapper = new XopingThrowableMapper();
        throwableMapper.add(ClientRepository.Error.CLIENT_DESCONEGUT, UpdateDataBaseUseCase.Error.CLIENT_DESCONEGUT);
        throwableMapper.add(PistaRepository.Error.PISTA_DESCONEGUDA, UpdateDataBaseUseCase.Error.PISTA_DESCONEGUDA);
        throwableMapper.add(PartitRepository.Error.PARTIT_DESCONEGUT, UpdateDataBaseUseCase.Error.PARTIT_DESCONEGUT);
    }

    public Observable<String> eliminarPistes(String dataActual) {
        return pistaRepository.eliminarPistes(dataActual)
                .onErrorResumeNext((Throwable throwable) -> Observable.error(throwableMapper.map(throwable)));
    }

    public Observable<String> eliminarPartits(String dataActual) {
        return partitRepository.eliminarPartits(dataActual)
                .onErrorResumeNext((Throwable throwable) -> Observable.error(throwableMapper.map(throwable)));
    }

    public Observable<String> eliminarPistesClient( String dataActual) {
        return clientRepository.eliminarPistesPassades(dataActual)
                .onErrorResumeNext((Throwable throwable) -> Observable.error(throwableMapper.map(throwable)));
    }

    public Observable<String> eliminarPartitsClient(String dataActual) {
        return clientRepository.eliminarPartitsPassats(dataActual)
                .onErrorResumeNext((Throwable throwable) -> Observable.error(throwableMapper.map(throwable)));
    }

}
