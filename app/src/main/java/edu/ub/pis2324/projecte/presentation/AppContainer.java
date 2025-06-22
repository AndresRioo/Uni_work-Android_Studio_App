package edu.ub.pis2324.projecte.presentation;

import edu.ub.pis2324.projecte.presentation.data.repositories.firestore.*;
import edu.ub.pis2324.projecte.presentation.domain.di.repositories.*;
import edu.ub.pis2324.projecte.presentation.domain.usecases.*;
import edu.ub.pis2324.projecte.presentation.domain.usecases.implementations.*;

public class AppContainer {
    public final AbstractRepositoryFactory abbstractFactory = new FirestoreRepositoryFactory();

    public final ClientRepository clientRepository = abbstractFactory.createClientRepository();
    public final PistaRepository pistaRepository = abbstractFactory.createPistaRepository();
    public final PartitRepository partitRepository = abbstractFactory.createPartitRepository();

    // Casos d'ús ----------------------------------------------------------------------------------
    public final UpdateDataBaseUseCase updateDataBaseUseCase = new UpdateDataBaseUseCaseImpl(clientRepository, pistaRepository, partitRepository);
    public final LogInUseCase logInUseCase = new LogInUseCaseImpl(clientRepository);
    public final SignUpUseCase signUpUseCase = new SignUpUseCaseImpl(clientRepository);
    public final AjustesPerfilUseCase ajustesPerfilUseCase = new AjustesPerfilUseCaseImpl(clientRepository);
    public final CanviarContrasenyaUseCase canviarContrasenyaUseCase = new CanviarContrasenyaUseCaseImpl(clientRepository);
    public final CancelarPartitUseCase cancelarPartitUseCase = new CancelarPartitUseCaseImpl(clientRepository, partitRepository);
    public final CancelarPistaUseCase cancelarPistaUseCase = new CancelarPistaUseCaseImpl(clientRepository, pistaRepository);
    public final ReservarPartitUseCase reservarPartitUseCase = new ReservarPartitUseCaseImpl(clientRepository, partitRepository);
    public final ReservarPistaUseCase reservarPistaUseCase = new ReservarPistaUseCaseImpl(clientRepository, pistaRepository);
    public final VisualitzarHoresPartitUseCase visualitzarHoresPartitUseCase = new VisualitzarHoresPartitUseCaseImpl(clientRepository, partitRepository);
    public final VisualitzarHoresPistaUseCase visualitzarHoresPistaUseCase = new VisualitzarHoresPistaUseCaseImpl(pistaRepository);
    public final VisualitzarReservesUseCase visualitzarReservesUseCase = new VisualitzarReservesUseCaseImpl(clientRepository, pistaRepository ,partitRepository);

}
