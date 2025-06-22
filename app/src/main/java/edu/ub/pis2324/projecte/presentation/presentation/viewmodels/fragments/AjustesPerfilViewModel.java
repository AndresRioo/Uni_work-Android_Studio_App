package edu.ub.pis2324.projecte.presentation.presentation.viewmodels.fragments;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import edu.ub.pis2324.projecte.presentation.domain.usecases.AjustesPerfilUseCase;
import edu.ub.pis2324.projecte.presentation.utils.error_handling.XopingError;
import edu.ub.pis2324.projecte.presentation.utils.error_handling.XopingThrowable;
import edu.ub.pis2324.projecte.presentation.utils.livedata.StateLiveData;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class AjustesPerfilViewModel extends AndroidViewModel {
    private final AjustesPerfilUseCase useCase;
    private final StateLiveData<String> estatNom;
    private final StateLiveData<String> estatEliminar;
    private final CompositeDisposable compositeDisposable;

    public AjustesPerfilViewModel(Application application, AjustesPerfilUseCase useCase) {
        super(application);
        this.useCase = useCase;
        estatNom = new StateLiveData<>();
        estatEliminar = new StateLiveData<>();
        compositeDisposable = new CompositeDisposable();
    }

    public StateLiveData<String> getNomPerCorreuState() {
        return estatNom;
    }
    public StateLiveData<String> getEliminarClientState() {
        return estatEliminar;
    }

    public void getNomPerCorreu(String correu) {
        estatNom.postLoading();

        Disposable d = useCase.getNomPerCorreu(correu)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        this::handleNomPerCorreuSuccess,  // Si no hi ha error, s'executa aquesta funció
                        throwable -> handleError(throwable, estatNom) // Sinó hi ha error, s'executa aquesta
                );

        compositeDisposable.add(d);
    }

    public void eliminarClient(String correu) {
        estatEliminar.postLoading();

        Disposable d = useCase.eliminarClient(correu)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        client -> handleEliminarClientSuccess(),  // Si no hi ha error, s'executa aquesta funció
                        throwable -> handleError(throwable, estatEliminar) // Sinó hi ha error, s'executa aquesta
                );

        compositeDisposable.add(d);
    }


    private void handleNomPerCorreuSuccess(String client) {
        estatNom.postSuccess(client);
    }

    private void handleEliminarClientSuccess() {
        estatEliminar.postSuccess("Eliminat correctament");
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
        if (xError == AjustesPerfilUseCase.Error.CLIENT_DESCONEGUT)
            message = "No s'ha trobat el client";
        else
            message = "Error desconegut";

        estat.postError(new Throwable(message));
    }

    /**
     * Factory for the ViewModel to be able to pass parameters to the constructor
     */
    public static class Factory extends ViewModelProvider.NewInstanceFactory {
        private final Application application;
        private final AjustesPerfilUseCase useCase;

        public Factory(Application application, AjustesPerfilUseCase useCase) {
            this.application = application;
            this.useCase = useCase;
        }

        @NonNull
        @Override
        @SuppressWarnings("unchecked")
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new AjustesPerfilViewModel(application, useCase);
        }
    }

}