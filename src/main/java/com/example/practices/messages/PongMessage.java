package com.example.practices.messages;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.With;

import java.util.UUID;

@Builder
@AllArgsConstructor
public class PongMessage implements Result {

  @With @Getter
  private final UUID id;

  @With @Getter
  private final UUID requestId;

  @With @Getter
  private final String message;

  @Override
  public UUID requestId() {
    return requestId;
  }

  @Override
  public boolean isSuccess() {
    return true;
  }

  @Override
  public Throwable error() {
    return null;
  }
}
