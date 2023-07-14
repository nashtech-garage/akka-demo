package com.example.practices.messages;


import lombok.*;

import java.util.Objects;
import java.util.UUID;

@Builder
@ToString
public class Hello implements Message {
  @With
  @Getter
  private final UUID id;

  @With
  @Getter
  private final String message;

  public Hello(UUID id, String message) {
    this.id = Objects.isNull(id) ? UUID.randomUUID() : id;
    this.message = message;
  }
}
