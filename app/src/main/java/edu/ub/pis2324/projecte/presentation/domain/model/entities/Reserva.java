package edu.ub.pis2324.projecte.presentation.domain.model.entities;

import java.io.Serializable;

import edu.ub.pis2324.projecte.presentation.domain.model.valueobjects.ReservaId;

public class Reserva implements Serializable {
    private ReservaId idReserva;
    private String data, dataOrdenada, hora;
    private String esport, material;

    public Reserva() {} // Empty constructor required for Firestore.

    public Reserva(ReservaId reservaId, String data, String dataOrdenada, String hora, String esport, String material) {
        this.idReserva = reservaId;
        this.data = data;
        this.dataOrdenada = dataOrdenada;
        this.hora = hora;
        this.esport = esport;
        this.material = material;
    }

    public void setIdReserva(ReservaId idReserva) { this.idReserva = idReserva; }
    public ReservaId getIdReserva() { return idReserva; }
    public String getData() { return data; }
    public String getDataOrdenada() { return dataOrdenada; }
    public String getHora() { return hora; }
    public String getEsport() { return esport; }
    public String getMaterial() { return material; }

}