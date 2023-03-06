package com.jkeya.workshop.mapping;



import org.mapstruct.Mapper;
import com.jkeya.workshop.Incoming;

@Mapper(componentModel = "cdi")
public interface IncomingToMDBMapper {
  /**
   * Maps all fields from {@code incoming} to a {@link com.jkeya.workshop.MdbRequest}
   * @param incoming
   * @return
   */
  com.jkeya.workshop.MdbRequest toSchema(Incoming incoming);


  com.jkeya.workshop.Outgoing toOutgoing(Incoming incoming);


}
