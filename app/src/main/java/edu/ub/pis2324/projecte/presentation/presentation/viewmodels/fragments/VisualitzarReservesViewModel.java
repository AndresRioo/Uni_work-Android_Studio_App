package edu.ub.pis2324.projecte.presentation.presentation.viewmodels.fragments;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;

import edu.ub.pis2324.projecte.presentation.domain.model.entities.Reserva;
import edu.ub.pis2324.projecte.presentation.domain.model.entities.Pista;
import edu.ub.pis2324.projecte.presentation.domain.model.entities.Partit;
import edu.ub.pis2324.projecte.presentation.domain.usecases.VisualitzarReservesUseCase;
import edu.ub.pis2324.projecte.presentation.domain.usecases.implementations.VisualitzarReservesUseCaseImpl;
import edu.ub.pis2324.projecte.presentation.utils.error_handling.XopingError;
import edu.ub.pis2324.projecte.presentation.utils.error_handling.XopingThrowable;
import edu.ub.pis2324.projecte.presentation.utils.livedata.StateLiveData;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class VisualitzarReservesViewModel extends AndroidViewModel {
    private final VisualitzarReservesUseCase useCase;
    private final StateLiveData<ArrayList<? extends Reserva>> estatReserves;
    private final StateLiveData<ArrayList<Pista>> estatPistes;
    private final StateLiveData<ArrayList<Partit>> estatPartits;
    private final CompositeDisposable compositeDisposable;

    public VisualitzarReservesViewModel(Application application, VisualitzarReservesUseCase useCase) {
        super(application);
        this.useCase = useCase;
        estatReserves = new StateLiveData<>();
        estatPistes = new StateLiveData<>();
        estatPartits = new StateLiveData<>();
        compositeDisposable = new CompositeDisposable();
    }

    public StateLiveData<ArrayList<? extends Reserva>> getInitLlistaReserves() {
        return estatReserves;
    }
    public StateLiveData<ArrayList<Pista>> getInitLlistaPistes() {
        return estatPistes;
    }
    public StateLiveData<ArrayList<Partit>> getInitLlistaPartits() {
        return estatPartits;
    }

    public void initLlistaReserves(String correu) {
        estatReserves.postLoading();

        /* Invoca cas d'ús */
        Disposable d = useCase.initLlistaReserves(correu)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        this::handleSuccessReserves, // Si no hi ha error, s'executa aquesta funció
                        throwable -> handleError(throwable, estatPistes) // Sinó hi ha error, s'executa aquesta
                );

        compositeDisposable.add(d);
    }

    public void initLlistaPistes(String correu) {
        estatPistes.postLoading();

        /* Invoca cas d'ús */
        Disposable d = useCase.initLlistaPistes(correu)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        this::handleSuccessPistes, // Si no hi ha error, s'executa aquesta funció
                        throwable -> handleError(throwable, estatPistes) // Sinó hi ha error, s'executa aquesta
                );

        compositeDisposable.add(d);
    }

    public void initLlistaPartits(String correu) {
        estatPistes.postLoading();

        /* Invoca cas d'ús */
        Disposable d = useCase.initLlistaPartits(correu)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        this::handleSuccessPartits, // Si no hi ha error, s'executa aquesta funció
                        throwable -> handleError(throwable, estatPartits) // Sinó hi ha error, s'executa aquesta
                );

        compositeDisposable.add(d);
    }

    private void handleSuccessReserves(ArrayList<? extends Reserva> llistaReserves) {
        estatReserves.postSuccess(llistaReserves);
    }

    private void handleSuccessPistes(ArrayList<Pista> llistaPistes) {
        estatPistes.postSuccess(llistaPistes);
    }

    private void handleSuccessPartits(ArrayList<Partit> llistaPartits) {
        estatPartits.postSuccess(llistaPartits);
    }

    private void handleError(Throwable throwable, StateLiveData<?> estat) {
        if (throwable instanceof XopingThrowable)
            handleXopingError((XopingThrowable) throwable, estat);
        else
            estat.postError(new Throwable("Error desconegut"));
    }

    private void handleXopingError(XopingThrowable xopingThrowable, StateLiveData<?> estat) {
        String message;
        XopingError xError = (xopingThrowable).getError();
        if (xError == VisualitzarReservesUseCaseImpl.Error.LLISTA_BUIDA)
            message = "No hi ha reserves disponibles";
        else if (xError == VisualitzarReservesUseCaseImpl.Error.PISTA_DESCONEGUDA)
            message = "No s'ha trobat una pista";
        else if (xError == VisualitzarReservesUseCaseImpl.Error.PARTIT_DESCONEGUT)
            message = "No s'ha trobat un partit";
        else
            message = "Error desconegut";

        estat.postError(new Throwable(message));
    }

    /**
     * Factory for the ViewModel to be able to pass parameters to the constructor
     */
    public static class Factory extends ViewModelProvider.NewInstanceFactory {
        private final Application application;
        private final VisualitzarReservesUseCase useCase;

        public Factory(Application application, VisualitzarReservesUseCase useCase) {
            this.application = application;
            this.useCase = useCase;
        }

        @NonNull
        @Override
        @SuppressWarnings("unchecked")
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new VisualitzarReservesViewModel(application, useCase);
        }
    }
}