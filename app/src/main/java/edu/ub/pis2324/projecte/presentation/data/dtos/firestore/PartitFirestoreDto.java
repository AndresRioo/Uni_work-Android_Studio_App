package edu.ub.pis2324.projecte.presentation.data.dtos.firestore;

import java.util.ArrayList;

public class PartitFirestoreDto extends ReservaFirestoreDto {
    private ArrayList<String> idPistes;
    private String nivellDificultat;
    private int maxParticipants, participantsActuals;
    public ArrayList<String> getIdPistes() { return idPistes; }
    public String getNivellDificultat() { return nivellDificultat; }
    public int getMaxParticipants() { return maxParticipants; }
    public int getParticipantsActuals() { return participantsActuals; }
}
