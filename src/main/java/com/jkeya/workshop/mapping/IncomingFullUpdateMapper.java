package com.jkeya.workshop.mapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.jkeya.workshop.Incoming;



/**
 * Mapper to map all fields on an input {@link Incoming} onto a target {@link Incoming}.
 */
@Mapper(componentModel = "cdi")
public interface IncomingFullUpdateMapper {
	/**
	 * Maps all fields except <code>id</code> from {@code input} onto {@code target}.
	 * @param input The input {@link Incoming}
	 * @param target The target {@link Incoming}
	 */
	@Mapping(target = "id", ignore = true)
	void mapFullUpdate(Incoming input, @MappingTarget Incoming target);
}
