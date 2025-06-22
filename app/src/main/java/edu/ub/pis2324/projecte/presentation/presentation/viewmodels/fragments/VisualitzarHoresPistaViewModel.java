package edu.ub.pis2324.projecte.presentation.presentation.viewmodels.fragments;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import edu.ub.pis2324.projecte.presentation.domain.usecases.VisualitzarHoresPartitUseCase;
import edu.ub.pis2324.projecte.presentation.domain.usecases.VisualitzarHoresPistaUseCase;
import edu.ub.pis2324.projecte.presentation.domain.usecases.implementations.VisualitzarHoresPistaUseCaseImpl;
import edu.ub.pis2324.projecte.presentation.utils.error_handling.XopingError;
import edu.ub.pis2324.projecte.presentation.utils.error_handling.XopingThrowable;
import edu.ub.pis2324.projecte.presentation.utils.livedata.StateLiveData;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import java.util.ArrayList;
import edu.ub.pis2324.projecte.presentation.domain.model.entities.Pista;

public class VisualitzarHoresPistaViewModel extends AndroidViewModel {
    private final VisualitzarHoresPistaUseCase useCase;
    private final StateLiveData<ArrayList<Pista>> estat;
    private final CompositeDisposable compositeDisposable;

    public VisualitzarHoresPistaViewModel(Application application, VisualitzarHoresPistaUseCase useCase) {
        super(application);
        this.useCase = useCase;
        estat = new StateLiveData<>();
        compositeDisposable = new CompositeDisposable();
    }

    public StateLiveData<ArrayList<Pista>> getInitLlistaPistes() {
        return estat;
    }

    public void initLlistaPistes(String esport, String data) {
        estat.postLoading();

        /* Invoca cas d'ús */
        Disposable d = useCase.initLlistaPistes(esport, data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        this::handleSuccess, // Si no hi ha error, s'executa aquesta funció
                        this::handleError // Sinó hi ha error, s'executa aquesta
                );

        compositeDisposable.add(d);
    }

    private void handleSuccess(ArrayList<Pista> llistaPistes) {
        estat.postSuccess(llistaPistes);
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
        if (xError == VisualitzarHoresPistaUseCaseImpl.Error.LLISTA_BUIDA)
            message = "No hi ha pistes disponibles";
        else
            message = "Error desconegut";

        estat.postError(new Throwable(message));
    }

    /**
     * Factory for the ViewModel to be able to pass parameters to the constructor
     */
    public static class Factory extends ViewModelProvider.NewInstanceFactory {
        private final Application application;
        private final VisualitzarHoresPistaUseCase useCase;

        public Factory(Application application, VisualitzarHoresPistaUseCase useCase) {
            this.application = application;
            this.useCase = useCase;
        }

        @NonNull
        @Override
        @SuppressWarnings("unchecked")
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new VisualitzarHoresPistaViewModel(application, useCase);
        }
    }
}