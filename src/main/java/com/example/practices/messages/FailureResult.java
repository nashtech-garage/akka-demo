package com.example.practices.messages;

import lombok.Builder;
import lombok.Getter;
import lombok.With;

import java.util.UUID;

@Builder
public class FailureResult implements Result {

  @With @Getter
  private final UUID id;

  @With @Getter
  private final UUID requestId;

  @With @Getter
  private final Throwable error;

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
    return error;
  }
}
