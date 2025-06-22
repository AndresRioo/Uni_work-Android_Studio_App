package edu.ub.pis2324.projecte.presentation.presentation.viewmodels.fragments;

import android.app.Application;

import edu.ub.pis2324.projecte.presentation.domain.usecases.UpdateDataBaseUseCase;
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

import android.util.Log;

public class MainViewModel extends AndroidViewModel {
    private final UpdateDataBaseUseCase useCase;
    private final StateLiveData<String> eliminarPistaState;
    private final StateLiveData<String> eliminarPartitState;
    private final StateLiveData<String> eliminarPistesClientState;
    private final StateLiveData<String> eliminarPartitsClientState;
    private final CompositeDisposable compositeDisposable;

    public MainViewModel(Application application, UpdateDataBaseUseCase useCase) {
        super(application);
        this.useCase = useCase;
        eliminarPistaState = new StateLiveData<>();
        eliminarPartitState = new StateLiveData<>();
        eliminarPistesClientState = new StateLiveData<>();
        eliminarPartitsClientState = new StateLiveData<>();
        compositeDisposable = new CompositeDisposable();
    }

    public void eliminarPistesPassades(String dataActual) {
        eliminarPistaState.postLoading();

        /* Invoca cas d'ús */
        Disposable d = useCase.eliminarPistes(dataActual)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        client -> handSuccess(eliminarPistaState),  // Si no hi ha error, s'executa aquesta funció
                        throwable -> handleError(throwable, eliminarPistaState) // Sinó hi ha error, s'executa aquesta
                );

        compositeDisposable.add(d);
    }

    public void eliminarPartitsPassats(String dataActual) {
        eliminarPartitState.postLoading();

        /* Invoca cas d'ús */
        Disposable d = useCase.eliminarPartits(dataActual)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        client -> handSuccess(eliminarPartitState),  // Si no hi ha error, s'executa aquesta funció
                        throwable -> handleError(throwable, eliminarPartitState) // Sinó hi ha error, s'executa aquesta
                );

        compositeDisposable.add(d);
    }

    public void eliminarPistesClient(String dataActual) {
        eliminarPistesClientState.postLoading();

        /* Invoca cas d'ús */
        Disposable d = useCase.eliminarPistesClient(dataActual)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        client -> handSuccess(eliminarPistesClientState),  // Si no hi ha error, s'executa aquesta funció
                        throwable -> handleError(throwable, eliminarPistesClientState) // Sinó hi ha error, s'executa aquesta
                );

        compositeDisposable.add(d);
    }

    public void eliminarPartitsClient(String dataActual) {
        eliminarPartitsClientState.postLoading();

        /* Invoca cas d'ús */
        Disposable d = useCase.eliminarPartitsClient(dataActual)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        client -> handSuccess(eliminarPartitsClientState),  // Si no hi ha error, s'executa aquesta funció
                        throwable -> handleError(throwable, eliminarPartitsClientState) // Sinó hi ha error, s'executa aquesta
                );

        compositeDisposable.add(d);
    }


    private void handSuccess(StateLiveData<String> estat) {
        estat.postSuccess("exit");
    }

    private void handleError(Throwable throwable, StateLiveData<String> estat) {
        if (throwable instanceof XopingThrowable)
            handleXopingError((XopingThrowable) throwable, estat);
        else
            estat.postError(new Throwable("Error desconegut"));
    }

    private void handleXopingError(XopingThrowable xopingThrowable, StateLiveData<String> estat) {
        String message;
        XopingError xError = (xopingThrowable).getError();
        if (xError == UpdateDataBaseUseCase.Error.CLIENT_DESCONEGUT)
            message = "No s'ha trobat el client";
        else if (xError == UpdateDataBaseUseCase.Error.PISTA_DESCONEGUDA)
            message = "No s'ha trobat la pista";
        else if (xError == UpdateDataBaseUseCase.Error.PARTIT_DESCONEGUT)
            message = "No s'ha trobat el partit";
        else
            message = "Error desconegut";

        estat.postError(new Throwable(message));
    }

    /**
     * Factory for the ViewModel to be able to pass parameters to the constructor
     */
    public static class Factory extends ViewModelProvider.NewInstanceFactory {
        private final Application application;
        private final UpdateDataBaseUseCase useCase;

        public Factory(Application application, UpdateDataBaseUseCase useCase) {
            this.application = application;
            this.useCase = useCase;
        }

        @NonNull
        @Override
        @SuppressWarnings("unchecked")
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new MainViewModel(application, useCase);
        }
    }
}
