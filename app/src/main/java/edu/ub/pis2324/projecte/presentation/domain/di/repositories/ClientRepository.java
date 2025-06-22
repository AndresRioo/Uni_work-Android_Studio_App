package edu.ub.pis2324.projecte.presentation.domain.di.repositories;
import edu.ub.pis2324.projecte.presentation.utils.error_handling.XopingError;
import io.reactivex.Observable;

import java.util.ArrayList;
import java.util.Map;

public interface ClientRepository {
    Observable<String> canviarContrasenya(String correu, String novaContrasenya);
    Observable<String> getContrasenyaPerCorreu(String correu);
    Observable<String> getNomPerCorreu(String correu);
    Observable<String> eliminarClient(String correu);
    Observable<String>crearClient(String correu, Map<String, Object> infoClient);
    Observable<String> afegirPista(String correu, String idPista);
    Observable<String> afegirPartit(String correu, String idPartit);
    Observable<String> eliminarPista(String correu, String idPista);
    Observable<String> eliminarPartit(String correu, String idPartit);
    Observable<ArrayList<String>> obtenirPistes(String correu);
    Observable<ArrayList<String>> obtenirPartits(String correu);
    Observable<String> eliminarPistesPassades(String dataActual);
    Observable<String> eliminarPartitsPassats(String dataActual);

    enum Error implements XopingError {
        CLIENT_DESCONEGUT,
        CLIENT_EXISTENT,
        PARTIT_DESCONEGUT,
        PISTA_DESCONEGUDA,;
    }
}