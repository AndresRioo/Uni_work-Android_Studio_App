package edu.ub.pis2324.projecte.presentation.presentation.pos.mappers;

import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;
import org.modelmapper.convention.MatchingStrategies;

import java.util.List;
import java.util.stream.Collectors;

import edu.ub.pis2324.projecte.presentation.domain.model.valueobjects.ClientId;
import edu.ub.pis2324.projecte.presentation.domain.model.valueobjects.ReservaId;

public class DomainToPOMapper extends ModelMapper {

    public DomainToPOMapper() {
         super();

         super.getConfiguration()
              .setFieldMatchingEnabled(true) // No need to define setters
              .setFieldAccessLevel(Configuration.AccessLevel.PRIVATE)
              .setMatchingStrategy(MatchingStrategies.LOOSE);

         super.addConverter(new AbstractConverter<ReservaId, String>() {
             @Override
             protected String convert(ReservaId source) {
                 return null;
             }
         });

         super.addConverter(new AbstractConverter<ClientId, String>() {
            @Override
            protected String convert(ClientId source) {
              return source.toString();
            }
         });
    }

    public <S, T> List<T> mapList(List<S> source, Class<T> targetClass) {
        return source
            .stream()
            .map(element -> super.map(element, targetClass))
            .collect(Collectors.toList());
        }
}
