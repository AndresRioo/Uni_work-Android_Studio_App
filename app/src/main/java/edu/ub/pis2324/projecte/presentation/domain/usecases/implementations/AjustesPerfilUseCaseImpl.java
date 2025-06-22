package edu.ub.pis2324.projecte.presentation.domain.usecases.implementations;

import edu.ub.pis2324.projecte.presentation.data.repositories.firestore.ClientFirestoreRepository;
import edu.ub.pis2324.projecte.presentation.domain.di.repositories.ClientRepository;
import edu.ub.pis2324.projecte.presentation.domain.usecases.AjustesPerfilUseCase;

import io.reactivex.Observable;
import edu.ub.pis2324.projecte.presentation.utils.error_handling.XopingThrowableMapper;

public class AjustesPerfilUseCaseImpl implements AjustesPerfilUseCase {

    private final ClientRepository clientRepository;
    private final XopingThrowableMapper throwableMapper;

    public AjustesPerfilUseCaseImpl(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;

        throwableMapper = new XopingThrowableMapper();
        throwableMapper.add(ClientFirestoreRepository.Error.CLIENT_DESCONEGUT, AjustesPerfilUseCase.Error.CLIENT_DESCONEGUT);
    }

    public Observable<String> getNomPerCorreu(String correu) {
        return clientRepository.getNomPerCorreu(correu)
                .onErrorResumeNext((Throwable throwable) -> Observable.error(throwableMapper.map(throwable)));
    }

    public Observable<String> eliminarClient(String correu) {
        return clientRepository.eliminarClient(correu)
                .onErrorResumeNext((Throwable throwable) -> Observable.error(throwableMapper.map(throwable)));
    }

}