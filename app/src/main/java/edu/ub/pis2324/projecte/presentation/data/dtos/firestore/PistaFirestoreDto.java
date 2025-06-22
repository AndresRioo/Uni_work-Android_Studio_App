package edu.ub.pis2324.projecte.presentation.data.dtos.firestore;

public class PistaFirestoreDto extends ReservaFirestoreDto {
    private String idClient;
    private boolean reservada;
    public String getIdClient() { return idClient; }
    public boolean getReservada() { return reservada; }
}
