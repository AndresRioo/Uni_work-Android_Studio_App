package edu.ub.pis2324.projecte.presentation.domain.model.entities;

import java.util.ArrayList;
import edu.ub.pis2324.projecte.presentation.domain.model.valueobjects.ReservaId;

public class Partit extends Reserva {
    private ArrayList<String> idPistes;
    private String nivellDificultat;
    private int maxParticipants, participantsActuals;

    public Partit() {} // Required for Firestore

    public Partit(ReservaId reservaId, String data, String dataOrdenada, String hora, String esport, String material,
                  ArrayList<String> idPistes, String nivellDificultat, int maxParticipants, int participantsActuals) {
        super(reservaId, data, dataOrdenada, hora, esport, material);
        this.idPistes = idPistes;
        this.nivellDificultat = nivellDificultat;
        this.maxParticipants = maxParticipants;
        this.participantsActuals = participantsActuals;
    }

    public ArrayList<String> getIdPistes() { return idPistes; }
    public String getNivellDificultat() { return nivellDificultat; }
    public int getMaxParticipants() { return maxParticipants; }
    public int getParticipantsActuals() { return participantsActuals; }
}

