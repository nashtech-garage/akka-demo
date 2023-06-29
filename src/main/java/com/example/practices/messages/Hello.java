package com.example.practices.messages;

import lombok.Builder;
import lombok.With;

import java.util.UUID;

@Builder
public record Hello(@With UUID id, @With String message) {

  public Hello(UUID id, String message) {
    this.id = id;
    this.message = message;
  }

  public Hello(String message) {
    this(UUID.randomUUID(), message);
  }

}
