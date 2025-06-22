package edu.ub.pis2324.projecte.presentation.presentation.viewmodels.fragments;

import android.app.Application;

import edu.ub.pis2324.projecte.presentation.domain.usecases.LogInUseCase;
import edu.ub.pis2324.projecte.presentation.utils.error_handling.XopingError;
import edu.ub.pis2324.projecte.presentation.utils.error_handling.XopingThrowable;
import edu.ub.pis2324.projecte.presentation.utils.livedata.StateLiveData;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class LogInViewModel extends AndroidViewModel {
    private final LogInUseCase useCase;
    private final StateLiveData<String> estat;
    private final CompositeDisposable compositeDisposable;

    public LogInViewModel(Application application, LogInUseCase useCase) {
        super(application);
        this.useCase = useCase;
        estat = new StateLiveData<>();
        compositeDisposable = new CompositeDisposable();
    }

    public StateLiveData<String> getIniciarClientState() {
        return estat;
    }

    public void iniciarClient(String correu, String contrasenya) {
        estat.postLoading();

        /* Invoca cas d'ús */
        Disposable d = useCase.iniciarClient(correu, contrasenya)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        client -> handleLogInSuccess(),  // Si no hi ha error, s'executa aquesta funció
                        this::handleError // Sinó hi ha error, s'executa aquesta
                );

        compositeDisposable.add(d);
    }

    private void handleLogInSuccess() {
        estat.postSuccess("Benvingut/da");
    }

    private void handleError(Throwable throwable) {
        if (throwable instanceof XopingThrowable)
            handleXopingError((XopingThrowable) throwable);
        else
            estat.postError(new Throwable("Error desconegut"));
    }

    private void handleXopingError(XopingThrowable xopingThrowable) {
        String message;
        XopingError xError = (xopingThrowable).getError();
        if (xError == LogInUseCase.Error.CLIENT_DESCONEGUT)
            message = "No s'ha trobat el client";
        else if (xError == LogInUseCase.Error.CONTRASENYA_INCORRECTA)
            message = "La contrasenya no és correcta";
        else if (xError == LogInUseCase.Error.CAMPS_INCOMPLETS)
            message = "No has omplert tots els camps";
        else
            message = "Error desconegut";

        estat.postError(new Throwable(message));
    }

    /**
     * Factory for the ViewModel to be able to pass parameters to the constructor
     */
    public static class Factory extends ViewModelProvider.NewInstanceFactory {
        private final Application application;
        private final LogInUseCase useCase;

        public Factory(Application application, LogInUseCase useCase) {
            this.application = application;
            this.useCase = useCase;
        }

        @NonNull
        @Override
        @SuppressWarnings("unchecked")
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new LogInViewModel(application, useCase);
        }
    }
}
