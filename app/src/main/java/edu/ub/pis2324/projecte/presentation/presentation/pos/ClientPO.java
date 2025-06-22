package edu.ub.pis2324.projecte.presentation.presentation.pos;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class ClientPO implements Parcelable {
    private String correu;
    private String contrasenya;



    // Constructor con parámetros
    public ClientPO(String correu, String contrasenya ) {
        this.correu = correu;
        this.contrasenya = contrasenya;
    }

    protected ClientPO(Parcel in) {
        correu = in.readString();
        contrasenya = in.readString();
    }

    public static final Creator<ClientPO> CREATOR = new Creator<ClientPO>() {
        @Override
        public ClientPO createFromParcel(Parcel in) {
            return new ClientPO(in);
        }

        @Override
        public ClientPO[] newArray(int size) {
            return new ClientPO[size];
        }
    };

    // Getters y Setters
    public String getCorreu() {
        return correu;
    }
    public String getContrasenya() {
        return contrasenya;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(this.correu);
        dest.writeString(this.contrasenya);
    }

    // Métodos de la clase

}
