package edu.ub.pis2324.projecte.presentation.data.dtos.firestore.mappers;

import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;
import org.modelmapper.convention.MatchingStrategies;

import edu.ub.pis2324.projecte.presentation.domain.model.valueobjects.ReservaId;

public class DTOToDomainMapper extends ModelMapper {

    public DTOToDomainMapper() {
        super();

        super.getConfiguration()
                .setFieldMatchingEnabled(true) // Evita haver de fer servir setters
                .setFieldAccessLevel(Configuration.AccessLevel.PRIVATE) // Nivell d'accés privat
                .setMatchingStrategy(MatchingStrategies.LOOSE); // Coincidència no estricta

        super.addConverter(new AbstractConverter<String, ReservaId>() {
            @Override
            protected ReservaId convert(String source) {
                return new ReservaId(source);
            }
        });
    }

    @Override
    public <T> T map(Object source, Class<T> destinationType) {
        return super.map(source, destinationType);
    }
}