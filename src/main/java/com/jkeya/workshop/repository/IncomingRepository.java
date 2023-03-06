package com.jkeya.workshop.repository;

import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.smallrye.mutiny.Uni;
import java.util.List;
import java.util.Random;
import javax.enterprise.context.ApplicationScoped;

import com.jkeya.workshop.Incoming;

@ApplicationScoped
public class IncomingRepository implements PanacheRepository<Incoming> {

  public Uni<Incoming> findRandom() {
    return count()
      .map(count -> (count > 0) ? count : null)
      .onItem()
      .ifNotNull()
      .transform(count -> new Random().nextInt(count.intValue()))
      .onItem()
      .ifNotNull()
      .transformToUni(randomIncoming ->
        findAll().page(randomIncoming, 1).firstResult()
      );
  }

  public Uni<List<Incoming>> listAllWhereNameLike(String name) {
    return (name != null)
      ? list("LOWER(name) LIKE CONCAT('%', ?1, '%')", name.toLowerCase())
      : Uni.createFrom().item(List::of);
  }
}
