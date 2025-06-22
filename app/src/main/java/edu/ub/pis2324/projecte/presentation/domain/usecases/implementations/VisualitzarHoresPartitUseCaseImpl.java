package edu.ub.pis2324.projecte.presentation.domain.usecases.implementations;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import edu.ub.pis2324.projecte.presentation.domain.di.repositories.PartitRepository;
import edu.ub.pis2324.projecte.presentation.domain.di.repositories.ClientRepository;
import edu.ub.pis2324.projecte.presentation.domain.model.entities.Partit;
import edu.ub.pis2324.projecte.presentation.domain.usecases.VisualitzarHoresPartitUseCase;
import edu.ub.pis2324.projecte.presentation.utils.error_handling.XopingThrowable;
import edu.ub.pis2324.projecte.presentation.utils.error_handling.XopingThrowableMapper;
import io.reactivex.Observable;

public class VisualitzarHoresPartitUseCaseImpl implements VisualitzarHoresPartitUseCase {

    private final ClientRepository clientRepository;
    private final PartitRepository partitRepository;
    private final XopingThrowableMapper throwableMapper;

    public VisualitzarHoresPartitUseCaseImpl(ClientRepository clienntReporsitory, PartitRepository partitRepository) {
        this.clientRepository = clienntReporsitory;
        this.partitRepository = partitRepository;
        throwableMapper = new XopingThrowableMapper();
    }

    public Observable<ArrayList<Partit>> initLlistaPartits(String esport, String data) {
        return partitRepository.initLlistaPartits(esport, data)
                .concatMap(llistaPartits -> {
                    if (llistaPartits.isEmpty()) {
                        return Observable.error(new XopingThrowable(VisualitzarHoresPartitUseCase.Error.LLISTA_BUIDA));
                    } else {
                        return Observable.just(llistaPartits);
                    }
                })
                .onErrorResumeNext((Throwable throwable) -> Observable.error(throwableMapper.map(throwable)));
    }

    public Observable<ArrayList<Partit>> initLlistaPartits(String esport, String data, String correu) {
        return partitRepository.initLlistaPartits(esport, data)
                .zipWith(clientRepository.obtenirPartits(correu),
                        (llistaPartits, llistaIds) -> {


                            if (llistaPartits.isEmpty()) {
                                throw new RuntimeException(new XopingThrowable(VisualitzarHoresPartitUseCase.Error.LLISTA_BUIDA));
                            } else {

                                /**
                                // Filtrar la lista de partidos según la lista de IDs del cliente
                                 */

                                List<Partit> filteredPartits = llistaPartits.stream()
                                        .filter(partit -> !llistaIds.contains(partit.getIdReserva().getId())) // Cambiado a !llistaIds.contains
                                        .collect(Collectors.toList());
                                return new ArrayList<>(filteredPartits);
                            }
                        }
                        )
                .onErrorResumeNext((Throwable throwable) -> Observable.error(throwableMapper.map(throwable)));
    }

}
