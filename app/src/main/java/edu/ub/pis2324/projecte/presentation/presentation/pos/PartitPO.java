package edu.ub.pis2324.projecte.presentation.presentation.pos;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

public class PartitPO implements Parcelable {
    private String nivellDificultat ;
    private int participantsActuals;

    private String esport;
    private boolean disponible;
    private String fechaString;
    private String horaString;

    private static final Map<String, Integer> maxParticipantsEsport = new HashMap<>();
    static {
        maxParticipantsEsport.put("FUTBOL 7", 14);
        maxParticipantsEsport.put("TENIS", 2);
        maxParticipantsEsport.put("PADEL", 4);
        maxParticipantsEsport.put("BASQUET", 10);
        maxParticipantsEsport.put("VOLEIBOL", 12);
        maxParticipantsEsport.put("FUTBOL 5", 10);
    }



    // Constructor con parámetros
    public PartitPO(String nivellDificultat, int participantsActuals,String esport, boolean disponible, String fechaString, String horaString) {
        this.nivellDificultat = nivellDificultat;
        this.participantsActuals = participantsActuals;
        this.esport = esport;
        this.disponible = disponible;
        this.fechaString = fechaString;
        this.horaString = horaString;
    }

    protected PartitPO(Parcel in) {
        nivellDificultat = in.readString();
        participantsActuals = in.readInt();
        esport = in.readString();
        disponible = in.readByte() != 0;
        fechaString = in.readString();
        horaString = in.readString();
    }



    public static final Creator<PartitPO> CREATOR = new Creator<PartitPO>() {
        @Override
        public PartitPO createFromParcel(Parcel in) {
            return new PartitPO(in);
        }

        @Override
        public PartitPO[] newArray(int size) {
            return new PartitPO[size];
        }
    };

    // Getters y Setters

    public String getNivellDificultat() {
        return nivellDificultat;
    }
    public int getParticipantsActuals() {
        return participantsActuals;
    }

    public String getEsport() {
        return esport;
    }
    public boolean getDisponible() {
        return disponible;
    }
    public String getFechaString() {
        return fechaString;
    }
    public String getHoraString() {
        return horaString;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(this.nivellDificultat);
        dest.writeInt(this.participantsActuals);
        dest.writeString(this.esport);
        dest.writeByte((byte) (this.disponible ? 1 : 0));
        dest.writeString(this.fechaString);
        dest.writeString(this.horaString);
    }

    // Métodos de la clase

}
