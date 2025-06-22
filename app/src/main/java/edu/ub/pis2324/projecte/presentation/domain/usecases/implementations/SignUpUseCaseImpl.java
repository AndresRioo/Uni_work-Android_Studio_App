package edu.ub.pis2324.projecte.presentation.domain.usecases.implementations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edu.ub.pis2324.projecte.presentation.domain.di.repositories.ClientRepository;
import edu.ub.pis2324.projecte.presentation.domain.usecases.SignUpUseCase;

import edu.ub.pis2324.projecte.presentation.utils.error_handling.XopingThrowable;
import io.reactivex.Observable;
import edu.ub.pis2324.projecte.presentation.utils.error_handling.XopingThrowableMapper;

public class SignUpUseCaseImpl implements SignUpUseCase {
    private final ClientRepository clientRepository;
    private final XopingThrowableMapper throwableMapper;


    public SignUpUseCaseImpl(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;

        throwableMapper = new XopingThrowableMapper();
        throwableMapper.add(ClientRepository.Error.CLIENT_EXISTENT, SignUpUseCase.Error.CLIENT_EXISTENT);
    }

    public Observable<String> crearClient(String nom, String correu, String contrasenya, String repeticioContrasenya) {
        if (nom.isEmpty() || correu.isEmpty() || contrasenya.isEmpty() || repeticioContrasenya.isEmpty()) {
            return Observable.error(new XopingThrowable(SignUpUseCase.Error.CAMPS_INCOMPLETS));
        }
        if (!contrasenya.equals(repeticioContrasenya)) {
            return Observable.error(new XopingThrowable(SignUpUseCase.Error.REPETICIO_CONTRASENYA_INCORRECTA));
        }

        // Si no hi ha errors de validació, s'intenta crear el client
        Map<String, Object> infoClient = new HashMap<>();
        infoClient.put("nom", nom);
        infoClient.put("correu", correu);
        infoClient.put("contrasenya", contrasenya);
        infoClient.put("idPistes", new ArrayList<String>());
        infoClient.put("idPartits", new ArrayList<String>());

        return clientRepository.crearClient(correu, infoClient)
            .onErrorResumeNext((Throwable throwable) -> Observable.error(throwableMapper.map(throwable)));
    }
}
