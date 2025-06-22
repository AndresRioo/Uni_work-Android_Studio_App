package edu.ub.pis2324.projecte.presentation.presentation.viewmodels.fragments;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;

import edu.ub.pis2324.projecte.presentation.domain.model.entities.Partit;
import edu.ub.pis2324.projecte.presentation.domain.usecases.VisualitzarHoresPartitUseCase;
import edu.ub.pis2324.projecte.presentation.domain.usecases.implementations.VisualitzarHoresPartitUseCaseImpl;
import edu.ub.pis2324.projecte.presentation.utils.error_handling.XopingError;
import edu.ub.pis2324.projecte.presentation.utils.error_handling.XopingThrowable;
import edu.ub.pis2324.projecte.presentation.utils.livedata.StateLiveData;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class VisualitzarHoresPartitViewModel extends AndroidViewModel {
    private final VisualitzarHoresPartitUseCase useCase;
    private final StateLiveData<ArrayList<Partit>> estat;
    private final CompositeDisposable compositeDisposable;

    public VisualitzarHoresPartitViewModel(Application application, VisualitzarHoresPartitUseCase useCase) {
        super(application);
        this.useCase = useCase;
        estat = new StateLiveData<>();
        compositeDisposable = new CompositeDisposable();
    }

    public StateLiveData<ArrayList<Partit>> getInitLlistaPartits() {
        return estat;
    }

    public void initLlistaPartits(String esport, String data, String correu) {
        estat.postLoading();

        /* Invoca cas d'ús */
        Disposable d = useCase.initLlistaPartits(esport, data, correu)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        this::handleSuccess, // Si no hi ha error, s'executa aquesta funció
                        this::handleError // Sinó hi ha error, s'executa aquesta
                );

        compositeDisposable.add(d);
    }

    private void handleSuccess(ArrayList<Partit> llistaPartits) {
        estat.postSuccess(llistaPartits);
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
        if (xError == VisualitzarHoresPartitUseCaseImpl.Error.LLISTA_BUIDA)
            message = "No hi ha partits disponibles";
        else
            message = "Error desconegut";

        estat.postError(new Throwable(message));
    }

    /**
     * Factory for the ViewModel to be able to pass parameters to the constructor
     */
    public static class Factory extends ViewModelProvider.NewInstanceFactory {
        private final Application application;
        private final VisualitzarHoresPartitUseCase useCase;

        public Factory(Application application, VisualitzarHoresPartitUseCase useCase) {
            this.application = application;
            this.useCase = useCase;
        }

        @NonNull
        @Override
        @SuppressWarnings("unchecked")
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new VisualitzarHoresPartitViewModel(application, useCase);
        }
    }
}