package edu.ub.pis2324.projecte.presentation.domain.model.entities;

import edu.ub.pis2324.projecte.presentation.domain.model.valueobjects.ReservaId;

public class Pista extends Reserva {
    private String idClient;
    private boolean reservada;

    public Pista() {} // Required for Firestore

    public Pista(ReservaId reservaId, String data, String dataOrdenada, String hora, String esport, String material,
                 String idClient, boolean reservada) {
        super(reservaId, data, dataOrdenada, hora, esport, material);
        this.idClient = idClient;
        this.reservada = reservada;
    }

    public String getIdClient() { return idClient; }
    public boolean getReservada() { return reservada; }

}
