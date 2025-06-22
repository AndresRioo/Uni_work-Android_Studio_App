package edu.ub.pis2324.projecte.presentation.domain.model.entities;

import java.util.ArrayList;

import edu.ub.pis2324.projecte.presentation.domain.model.valueobjects.ClientId;

public class Client {
    private ClientId correu;

    private String contrasenya;
    private ArrayList<String> idPistes;
    private ArrayList<String> idPartits;

    public Client() {
        idPistes = new ArrayList<>();
        idPartits = new ArrayList<>();
    }
    public Client(ClientId correu, String contrasenya) {
        this.correu = correu;
        this.contrasenya = contrasenya;
        idPistes = new ArrayList<>();
        idPartits = new ArrayList<>();
    }

    /**
     * Afegeix una reserva (Pista) a la llista
     * @param idReserva
     */
    public void afegirPista(String idReserva) {
        idPistes.add(idReserva);
    }
    /**
     * Afegeix una reserva (Partit) a la llista
     * @param idReserva
     */
    public void afegirPartit(String idReserva) {
        idPartits.add(idReserva);
    }

    /**
     * Elimina una reserva (Pista) de la llista
     * @param idReserva
     */
    public void eliminarPista(String idReserva) {
        idPistes.remove(idReserva);
    }

    /**
     * Elimina una reserva (Partit) de la llista
     * @param idReserva
     */
    public void eliminarPartit(String idReserva ) {
        idPartits.remove(idReserva);
    }

    /**
     * Retorna la llista de pistes del client
     * @return
     */
    public ArrayList<String> getIdPistes() {
        return idPistes;
    }

    /**
     * Retorna la llista de partits del client
     * @return
     */

    public ArrayList<String> getIdPartits() {return idPartits;}

    public String getCorreu() {
        return correu.getId();
    }

    public String getContrasenya() {
        return contrasenya;
    }

    public void setCorreu(ClientId correu) {
        this.correu = correu;
    }

    public void setContrasenya(String contrasenya) {
        this.contrasenya = contrasenya;
    }
}
