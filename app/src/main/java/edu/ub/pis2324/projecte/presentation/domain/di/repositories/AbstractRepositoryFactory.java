package edu.ub.pis2324.projecte.presentation.domain.di.repositories;

public interface AbstractRepositoryFactory {
    ClientRepository createClientRepository();
    PistaRepository createPistaRepository();
    PartitRepository createPartitRepository();
}
