package edu.ub.pis2324.projecte.presentation.data.dtos.firestore;

public class ReservaFirestoreDto {
    private String data, dataOrdenada, hora;
    private String esport, material;

    public ReservaFirestoreDto() {} // Empty constructor required for Firestore.

    public String getData() { return data; }
    public String getDataOrdenada() { return dataOrdenada; }
    public String getHora() { return hora; }
    public String getEsport() { return esport; }
    public String getMaterial() { return material; }
}
