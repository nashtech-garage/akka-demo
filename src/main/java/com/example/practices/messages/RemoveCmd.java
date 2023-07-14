package com.example.practices.messages;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.With;

/**
 * Remove Command to ask actor remove a child
 */
@Builder
@ToString
public class RemoveCmd implements Message {
  @With
  @Getter
  private final String name;
}
