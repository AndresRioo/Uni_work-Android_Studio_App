package edu.ub.pis2324.projecte.presentation.domain.model.valueobjects;

import java.io.Serializable;
import java.util.Objects;

public class ClientId implements Serializable {
  private String id;

  public ClientId(String id) {
    this.id = id;
  }

  @SuppressWarnings("unused")
  public ClientId() {}

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
    ClientId clientId = (ClientId) obj;
    return Objects.equals(id, clientId.id); // Use Objects.equals for null safety
  }

  @Override
  public int hashCode() {
    return Objects.hash(id); // Use Objects.hash to generate hash code based on id
  }

  @Override
  public String toString() {
    return id;
  }
}
