package com.example.practices.messages;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.With;

/**
 * Spawn Command to ask actor spawn a child
 */
@Builder
@ToString
public class SpawnCmd implements Message {
  @With
  @Getter
  private final String name;
}
