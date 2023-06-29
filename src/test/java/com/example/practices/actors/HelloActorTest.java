package com.example.practices.actors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.javadsl.TestKit;
import com.example.practices.messages.Hello;
import org.junit.*;
import akka.actor.Props;

import java.time.Duration;
import java.util.UUID;

import static org.junit.Assert.*;

public class HelloActorTest {

  static ActorSystem system;

  @BeforeClass
  public static void setUpTest() throws Exception {
    system = ActorSystem.create();
  }

  @AfterClass
  public static void tearDownTest() throws Exception {
    TestKit.shutdownActorSystem(system);
  }

  @Test
  public void testSayHello() {
    new TestKit(system) {
      {
        final Props props = Props.create(HelloActor.class);
        final ActorRef helloActorRef = getSystem().actorOf(props, "hello-world");

        helloActorRef.tell(new HelloActor.StartMessage(), getRef());

        var helloMessage = Hello.builder().id(UUID.randomUUID()).message("Hello world").build();
        var replyMessage = helloMessage.withMessage("Hi there");


        helloActorRef.tell( helloMessage, getRef());

        // await the correct response
        expectMsg(Duration.ofSeconds(1), replyMessage);

        awaitAssert(Duration.ofSeconds(1), () -> true);
      }
    };

  }

}