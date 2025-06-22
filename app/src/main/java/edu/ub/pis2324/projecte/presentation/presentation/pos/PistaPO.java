package edu.ub.pis2324.projecte.presentation.presentation.pos;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.sql.Time;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import edu.ub.pis2324.projecte.presentation.domain.model.valueobjects.ReservaId;

public class PistaPO implements Parcelable {

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



    public PistaPO(String esport, boolean disponible, String fechaString, String horaString) {
        this.esport = esport;
        this.disponible = disponible;
        this.fechaString = fechaString;
        this.horaString = horaString;
    }
    protected PistaPO(Parcel in) {
        esport = in.readString();
        disponible = in.readByte() != 0;
        fechaString = in.readString();
        horaString = in.readString();
    }


    public static final Creator<PistaPO> CREATOR = new Creator<PistaPO>() {
        @Override
        public PistaPO createFromParcel(Parcel in) {
            return new PistaPO(in);
        }

        @Override
        public PistaPO[] newArray(int size) {
            return new PistaPO[size];
        }
    };

    // Getters y Setters
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
        dest.writeString(this.esport);
        dest.writeByte((byte) (this.disponible ? 1 : 0));
        dest.writeString(this.fechaString);
        dest.writeString(this.horaString);
    }

    // Métodos de la clase

}
