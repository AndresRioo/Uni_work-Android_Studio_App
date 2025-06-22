package edu.ub.pis2324.projecte.presentation.presentation.viewmodels.fragments;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import edu.ub.pis2324.projecte.presentation.domain.usecases.ReservarPistaUseCase;
import edu.ub.pis2324.projecte.presentation.domain.usecases.implementations.ReservarPistaUseCaseImpl;
import edu.ub.pis2324.projecte.presentation.utils.error_handling.XopingError;
import edu.ub.pis2324.projecte.presentation.utils.error_handling.XopingThrowable;
import edu.ub.pis2324.projecte.presentation.utils.livedata.StateLiveData;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ReservarPistaViewModel extends AndroidViewModel {
    private final ReservarPistaUseCase useCase;
    private final StateLiveData<String> estat;
    private final CompositeDisposable compositeDisposable;

    public ReservarPistaViewModel(Application application, ReservarPistaUseCase useCase) {
        super(application);
        this.useCase = useCase;
        estat = new StateLiveData<>();
        compositeDisposable = new CompositeDisposable();
    }

    public StateLiveData<String> getReservarPista() {
        return estat;
    }

    public void reservar(String correu, String idPista) {
        estat.postLoading();

        /* Invoca cas d'ús */
        Disposable d = useCase.reservar(correu, idPista)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        reserva -> handleSuccess(), // Si no hi ha error, s'executa aquesta funció
                        this::handleError // Sinó hi ha error, s'executa aquesta
                );

        compositeDisposable.add(d);
    }

    private void handleSuccess() {
        estat.postSuccess("Pista reservada");
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
        if (xError == ReservarPistaUseCaseImpl.Error.CLIENT_DESCONEGUT)
            message = "No s'ha trobat el client";
        else if (xError == ReservarPistaUseCaseImpl.Error.PISTA_DESCONEGUDA)
            message = "No s'ha trobat la pista";
        else
            message = "Error desconegut";

        estat.postError(new Throwable(message));
    }

    /**
     * Factory for the ViewModel to be able to pass parameters to the constructor
     */
    public static class Factory extends ViewModelProvider.NewInstanceFactory {
        private final Application application;
        private final ReservarPistaUseCase useCase;

        public Factory(Application application, ReservarPistaUseCase useCase) {
            this.application = application;
            this.useCase = useCase;
        }

        @NonNull
        @Override
        @SuppressWarnings("unchecked")
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new ReservarPistaViewModel(application, useCase);
        }
    }
}