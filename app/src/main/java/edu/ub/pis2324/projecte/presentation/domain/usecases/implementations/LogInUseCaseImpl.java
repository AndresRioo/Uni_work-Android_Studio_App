package edu.ub.pis2324.projecte.presentation.domain.usecases.implementations;

import edu.ub.pis2324.projecte.presentation.domain.di.repositories.ClientRepository;
import edu.ub.pis2324.projecte.presentation.domain.usecases.LogInUseCase;

import edu.ub.pis2324.projecte.presentation.utils.error_handling.XopingThrowable;
import io.reactivex.Observable;
import edu.ub.pis2324.projecte.presentation.utils.error_handling.XopingThrowableMapper;

public class LogInUseCaseImpl implements LogInUseCase {
    private final ClientRepository clientRepository;
    private final XopingThrowableMapper throwableMapper;

    public LogInUseCaseImpl(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;

        throwableMapper = new XopingThrowableMapper();
        throwableMapper.add(ClientRepository.Error.CLIENT_DESCONEGUT, LogInUseCase.Error.CLIENT_DESCONEGUT);
    }

    public Observable<String> iniciarClient(String correu, String contrasenya) {
        if (correu.isEmpty() || contrasenya.isEmpty()) {
            return Observable.error(new XopingThrowable(LogInUseCase.Error.CAMPS_INCOMPLETS));
        }
        // Comprovar que la contrasenya sigui vàlida també retornarà si el correu no existeix
        return clientRepository.getContrasenyaPerCorreu(correu)
                .concatMap(contrasenyaActual -> {
                    if (!contrasenyaActual.equals(contrasenya)) {
                        return Observable.error(new XopingThrowable(LogInUseCase.Error.CONTRASENYA_INCORRECTA));
                    } else {
                        return Observable.just(contrasenyaActual);
                    }
                })
                .onErrorResumeNext((Throwable throwable) -> Observable.error(throwableMapper.map(throwable)));
    }

}
