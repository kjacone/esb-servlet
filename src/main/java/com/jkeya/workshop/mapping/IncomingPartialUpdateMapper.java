package com.jkeya.workshop.mapping;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

import com.jkeya.workshop.Incoming;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

/**
 * Mapper to map <code><strong>non-null</strong></code> fields on an input {@link Incoming} onto a target {@link Incoming}.
 */
@Mapper(componentModel = "cdi", nullValuePropertyMappingStrategy = IGNORE)
public interface IncomingPartialUpdateMapper {
  /**
   * Maps all <code><strong>non-null</strong></code> fields from {@code input} onto {@code target}.
   * @param input The input {@link Incoming}
   * @param target The target {@link Incoming}
   */
  void mapPartialUpdate(Incoming input, @MappingTarget Incoming target);
}
