package com.example.practices.messages;

import java.util.UUID;

/**
 * Result for a Command Execution
 */
public interface Result extends Message {

  UUID requestId();

  boolean isSuccess();

  Throwable error();
}
