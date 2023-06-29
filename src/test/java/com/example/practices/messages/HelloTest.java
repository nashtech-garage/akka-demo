package com.example.practices.messages;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

public class HelloTest {

  @Before
  public void setUp() throws Exception {
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testConstructors() {
    UUID id = UUID.randomUUID();
    String message = UUID.randomUUID().toString().split("-")[0];
    String message2 = UUID.randomUUID().toString().split("-")[1];
    var record = new Hello(id, message);
    var record2 = new Hello(id, message);
    assertEquals( id, record.id());
    assertEquals( message, record.message());
    assertEquals( record, record2);

    var record3 = record.withMessage(message2);

    assertEquals( id, record3.id());
    assertEquals( message2, record3.message());

  }
}