package edu.ub.pis2324.projecte.presentation.domain.usecases.implementations;

import java.util.ArrayList;

import edu.ub.pis2324.projecte.presentation.domain.di.repositories.PistaRepository;
import edu.ub.pis2324.projecte.presentation.domain.di.repositories.PartitRepository;
import edu.ub.pis2324.projecte.presentation.domain.di.repositories.ClientRepository;
import edu.ub.pis2324.projecte.presentation.domain.model.entities.Reserva;
import edu.ub.pis2324.projecte.presentation.domain.model.entities.Pista;
import edu.ub.pis2324.projecte.presentation.domain.model.entities.Partit;
import edu.ub.pis2324.projecte.presentation.domain.usecases.VisualitzarReservesUseCase;
import edu.ub.pis2324.projecte.presentation.utils.error_handling.XopingThrowable;
import edu.ub.pis2324.projecte.presentation.utils.error_handling.XopingThrowableMapper;
import io.reactivex.Observable;

public class VisualitzarReservesUseCaseImpl implements VisualitzarReservesUseCase {
    private final ClientRepository clientRepository;
    private final PistaRepository pistaRepository;
    private final PartitRepository partitRepository;
    private final XopingThrowableMapper throwableMapper;

    public VisualitzarReservesUseCaseImpl(ClientRepository clientRepository, PistaRepository pistaRepository, PartitRepository partitRepository) {
        this.clientRepository = clientRepository;
        this.pistaRepository = pistaRepository;
        this.partitRepository = partitRepository;

        throwableMapper = new XopingThrowableMapper();
        throwableMapper.add(PistaRepository.Error.PISTA_DESCONEGUDA, Error.PISTA_DESCONEGUDA);
        throwableMapper.add(PartitRepository.Error.PARTIT_DESCONEGUT, Error.PARTIT_DESCONEGUT);
    }

    public Observable<ArrayList<Pista>> initLlistaPistes(String correu) {
        return clientRepository.obtenirPistes(correu)
                .concatMap(idPistes -> {
                    if (idPistes.isEmpty()) {
                        return Observable.error(new XopingThrowable(Error.LLISTA_BUIDA));
                    } else {
                        return pistaRepository.obtenirReservesClient(idPistes)
                        .concatMap(llistaPistes -> {
                            if (llistaPistes.isEmpty()) {
                                return Observable.error(new XopingThrowable(Error.LLISTA_BUIDA));
                            } else {
                                llistaPistes.sort((pista1, pista2) -> pista1.getIdReserva().getId().compareTo(pista2.getIdReserva().getId()));
                                return Observable.just(llistaPistes);
                            }
                        })
                        .onErrorResumeNext((Throwable throwable) -> Observable.error(throwableMapper.map(throwable)));
                    }
                })
                .onErrorResumeNext((Throwable throwable) -> Observable.error(throwableMapper.map(throwable)));
    }

    public Observable<ArrayList<Partit>> initLlistaPartits(String correu) {
        return clientRepository.obtenirPartits(correu)
                .concatMap(idPartits -> {
                    if (idPartits.isEmpty()) {
                        return Observable.error(new XopingThrowable(Error.LLISTA_BUIDA));
                    } else {
                        return partitRepository.obtenirReservesClient(idPartits)
                                .concatMap(llistaPartits -> {
                                    if (llistaPartits.isEmpty()) {
                                        return Observable.error(new XopingThrowable(Error.LLISTA_BUIDA));
                                    } else {
                                        llistaPartits.sort((partit1, partit2) -> partit1.getIdReserva().getId().compareTo(partit2.getIdReserva().getId()));
                                        return Observable.just(llistaPartits);
                                    }
                                })
                                .onErrorResumeNext((Throwable throwable) -> Observable.error(throwableMapper.map(throwable)));
                    }
                })
                .onErrorResumeNext((Throwable throwable) -> Observable.error(throwableMapper.map(throwable)));
    }

    public Observable<ArrayList<? extends Reserva>> initLlistaReserves(String correu) {
         return clientRepository.obtenirPistes(correu)
                .concatMap(idPistes -> {
                    if (idPistes.isEmpty()) {
                        return initLlistaPartits(correu);
                    } else {
                        return pistaRepository.obtenirReservesClient(idPistes)
                                .concatMap(llistaPistes -> {
                                    if (llistaPistes.isEmpty()) {
                                        return initLlistaPartits(correu);
                                    } else {
                                        return clientRepository.obtenirPartits(correu)
                                                .concatMap(idPartits -> {
                                                    if (idPartits.isEmpty()) {
                                                        return Observable.just(llistaPistes);
                                                    } else {
                                                        return partitRepository.obtenirReservesClient(idPartits)
                                                                .concatMap(llistaPartits -> {
                                                                    if (llistaPartits.isEmpty()) {
                                                                        return Observable.just(llistaPistes);
                                                                    } else {
                                                                        ArrayList<Reserva> llistaReserves = new ArrayList<>();
                                                                        llistaReserves.addAll(llistaPistes);
                                                                        llistaReserves.addAll(llistaPartits);
                                                                        llistaReserves.sort((partit1, partit2) -> partit1.getIdReserva().getId().compareTo(partit2.getIdReserva().getId()));
                                                                        return Observable.just(llistaReserves);
                                                                    }
                                                                })
                                                                .onErrorResumeNext((Throwable throwable) -> Observable.error(throwableMapper.map(throwable)));
                                                    }
                                                })
                                                .onErrorResumeNext((Throwable throwable) -> Observable.error(throwableMapper.map(throwable)));
                                    }
                                })
                                .onErrorResumeNext((Throwable throwable) -> Observable.error(throwableMapper.map(throwable)));
                    }
                })
                .onErrorResumeNext((Throwable throwable) -> Observable.error(throwableMapper.map(throwable)));
    }
}
