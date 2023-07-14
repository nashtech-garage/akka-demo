package com.example.practices.actors;

import akka.actor.testkit.typed.javadsl.ActorTestKit;
import akka.actor.typed.ActorRef;
import com.example.practices.messages.Hello;
import com.example.practices.messages.Message;
import org.junit.*;

import java.util.UUID;

import static org.junit.Assert.*;

public class HelloBehaviorTest {

  static final ActorTestKit testKit = ActorTestKit.create();

  @BeforeClass
  public static void setUpTest() throws Exception {
  }

  @AfterClass
  public static void tearDownTest() throws Exception {
    testKit.shutdownTestKit();
  }

  @Test
  public void testSayHello() {

    ActorRef<Message> hello = testKit.spawn(HelloBehavior.create(), "hello-behavior");
    Hello helloMessage = Hello.builder().id(UUID.randomUUID()).message("Hello world").build();
    hello.tell(helloMessage);
    testKit.stop(hello);
  }

}