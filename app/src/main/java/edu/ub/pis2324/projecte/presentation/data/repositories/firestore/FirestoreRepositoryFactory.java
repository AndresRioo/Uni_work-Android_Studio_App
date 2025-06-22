package edu.ub.pis2324.projecte.presentation.data.repositories.firestore;

import edu.ub.pis2324.projecte.presentation.domain.di.repositories.AbstractRepositoryFactory;
import edu.ub.pis2324.projecte.presentation.domain.di.repositories.ClientRepository;
import edu.ub.pis2324.projecte.presentation.domain.di.repositories.PistaRepository;
import edu.ub.pis2324.projecte.presentation.domain.di.repositories.PartitRepository;

public class FirestoreRepositoryFactory implements AbstractRepositoryFactory {
    @Override
    public ClientRepository createClientRepository() {
        return new ClientFirestoreRepository();
    }

    @Override
    public PistaRepository createPistaRepository() {
        return new PistaFirestoreRepository();
    }

    @Override
    public PartitRepository createPartitRepository() {
        return new PartitFirestoreRepository();
    }
}
