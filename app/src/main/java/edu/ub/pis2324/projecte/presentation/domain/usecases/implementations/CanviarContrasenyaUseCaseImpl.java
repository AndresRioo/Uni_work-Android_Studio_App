package edu.ub.pis2324.projecte.presentation.domain.usecases.implementations;

import edu.ub.pis2324.projecte.presentation.domain.di.repositories.ClientRepository;
import edu.ub.pis2324.projecte.presentation.domain.usecases.CanviarContrasenyaUseCase;

import io.reactivex.Observable;
import edu.ub.pis2324.projecte.presentation.utils.error_handling.XopingThrowableMapper;
import edu.ub.pis2324.projecte.presentation.utils.error_handling.XopingThrowable;

public class CanviarContrasenyaUseCaseImpl implements CanviarContrasenyaUseCase {
    private final ClientRepository clientRepository;
    private final XopingThrowableMapper throwableMapper;

    public CanviarContrasenyaUseCaseImpl(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;

        throwableMapper = new XopingThrowableMapper();
        throwableMapper.add(ClientRepository.Error.CLIENT_DESCONEGUT, Error.CLIENT_DESCONEGUT);
    }

    public Observable<String> canviarContrasenya(String correu, String contrasenya, String novaContrasenya, String repeticioContrasenya) {
        return clientRepository.getContrasenyaPerCorreu(correu)
                .concatMap(contrasenyaActual -> {
                    if (!contrasenyaActual.equals(contrasenya)) {
                        return Observable.error(new XopingThrowable(Error.CONTRASENYA_INCORRECTA));
                    } else if (contrasenyaActual.equals(novaContrasenya)) {
                        return Observable.error(new XopingThrowable(Error.NOVA_CONTRASENYA_IGUAL_ANTIGUA));
                    } else if (!novaContrasenya.equals(repeticioContrasenya)) {
                        return Observable.error(new XopingThrowable(Error.REPETICIO_CONTRASENYA_INCORRECTA));
                    } else {
                        return clientRepository.canviarContrasenya(correu, novaContrasenya)
                                .onErrorResumeNext((Throwable throwable) -> Observable.error(throwableMapper.map(throwable)));
                    }
                })
                .onErrorResumeNext((Throwable throwable) -> Observable.error(throwableMapper.map(throwable)));
    }
}
