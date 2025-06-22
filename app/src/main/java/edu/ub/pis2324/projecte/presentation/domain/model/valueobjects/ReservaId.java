package edu.ub.pis2324.projecte.presentation.domain.model.valueobjects;

import java.io.Serializable;
import java.util.Objects;

public class ReservaId implements Serializable {
    private String id;

    public ReservaId(String id) {
      this.id = id;
    }

    @SuppressWarnings("unused")
    public ReservaId() {}

    public String getId() {
      return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ReservaId reservaId = (ReservaId) obj;
        return Objects.equals(id, reservaId.id); // Use Objects.equals for null safety
    }

    @Override
    public int hashCode() {
        return Objects.hash(id); // Use Objects.hash to generate hash code based on id
    }

    @Override
    public String toString() {
        return id.toString();
    }
}
