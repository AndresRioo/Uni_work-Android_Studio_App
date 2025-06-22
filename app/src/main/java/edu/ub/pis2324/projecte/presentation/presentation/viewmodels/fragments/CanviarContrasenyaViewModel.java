package edu.ub.pis2324.projecte.presentation.presentation.viewmodels.fragments;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import edu.ub.pis2324.projecte.presentation.domain.usecases.CanviarContrasenyaUseCase;
import edu.ub.pis2324.projecte.presentation.domain.usecases.implementations.CanviarContrasenyaUseCaseImpl;

import edu.ub.pis2324.projecte.presentation.utils.livedata.StateLiveData;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.disposables.CompositeDisposable;

import edu.ub.pis2324.projecte.presentation.utils.error_handling.XopingError;
import edu.ub.pis2324.projecte.presentation.utils.error_handling.XopingThrowable;

public class CanviarContrasenyaViewModel extends AndroidViewModel {
    private final CanviarContrasenyaUseCase useCase;
    private final StateLiveData<String> estat;
    private final CompositeDisposable compositeDisposable;

    public CanviarContrasenyaViewModel(Application application, CanviarContrasenyaUseCase useCase) {
        super(application);
        this.useCase = useCase;
        estat = new StateLiveData<>();
        compositeDisposable = new CompositeDisposable();
    }

    public StateLiveData<String> getCanviarContrasenyaState() {
        return estat;
    }

    public void canviarContrasenya(String correu, String contrasenya, String novaContrasenya, String repeticioContrasenya) {
        estat.postLoading();

        /* Invoca cas d'ús */
        Disposable d = useCase.canviarContrasenya(correu, contrasenya, novaContrasenya, repeticioContrasenya)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        client -> handleCanviarContrasenyaSuccess(),  // Si no hi ha error, s'executa aquesta funció
                        this::handleCanviarContrasenyaError // Sinó hi ha error, s'executa aquesta
                );

        compositeDisposable.add(d);
    }

    private void handleCanviarContrasenyaSuccess() {
        estat.postSuccess("Contrasenya actualitzada");
    }

    private void handleCanviarContrasenyaError(Throwable throwable) {
        if (throwable instanceof XopingThrowable)
            handleXopingError((XopingThrowable) throwable);
        else
            estat.postError(new Throwable("Error desconegut"));
    }

    private void handleXopingError(XopingThrowable xopingThrowable) {
        String message;
        XopingError xError = (xopingThrowable).getError();
        if (xError == CanviarContrasenyaUseCaseImpl.Error.CLIENT_DESCONEGUT)
            message = "No s'ha trobat el client";
        else if (xError == CanviarContrasenyaUseCaseImpl.Error.CONTRASENYA_INCORRECTA)
            message = "La contrasenya actual no és correcta";
        else if (xError == CanviarContrasenyaUseCaseImpl.Error.NOVA_CONTRASENYA_IGUAL_ANTIGUA)
            message = "La nova no pot ser igual a la vella";
        else if (xError == CanviarContrasenyaUseCaseImpl.Error.REPETICIO_CONTRASENYA_INCORRECTA)
            message = "La repetició no coincideix";
        else
            message = "Error desconegut";

        estat.postError(new Throwable(message));
    }

    /**
     * Factory for the ViewModel to be able to pass parameters to the constructor
     */
    public static class Factory extends ViewModelProvider.NewInstanceFactory {
        private final Application application;
        private final CanviarContrasenyaUseCase useCase;

        public Factory(Application application, CanviarContrasenyaUseCase useCase) {
            this.application = application;
            this.useCase = useCase;
        }

        @NonNull
        @Override
        @SuppressWarnings("unchecked")
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new CanviarContrasenyaViewModel(application, useCase);
        }
    }

}

