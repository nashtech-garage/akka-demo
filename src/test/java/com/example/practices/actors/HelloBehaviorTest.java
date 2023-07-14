package com.example.practices.actors;

import akka.actor.testkit.typed.javadsl.ActorTestKit;
import akka.actor.typed.ActorRef;
import akka.actor.typed.SupervisorStrategy;
import com.example.practices.messages.Hello;
import com.example.practices.messages.Message;
import com.example.practices.messages.RemoveCmd;
import com.example.practices.messages.SpawnCmd;
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

  @Test
  public void testSpawnAndSayHello() throws InterruptedException {

    ActorRef<Message> hello = testKit.spawn(HelloBehavior.create(), "hello-behavior");
    var helloMessage = Hello.builder().id(UUID.randomUUID()).message("all").build();

    // Ask HelloBehavior Actor to spawn new Childrens
    hello.tell(SpawnCmd.builder().name("child-01").build());
    hello.tell(SpawnCmd.builder().name("child-02").build());

    Thread.sleep(100);
    hello.tell(helloMessage);
    Thread.sleep(200);
    testKit.stop(hello);
  }

  @Test
  public void testRemoveChild_handleTerminalSignal() throws InterruptedException {

    ActorRef<Message> hello = testKit.spawn(HelloBehavior.create(), "hello-behavior");
    var helloMessage = Hello.builder().id(UUID.randomUUID()).message("all").build();

    // Ask HelloBehavior Actor to spawn new Childrens
    hello.tell(SpawnCmd.builder().name("child-01").build());
    hello.tell(SpawnCmd.builder().name("child-02").build());

    Thread.sleep(100);
    hello.tell(helloMessage);
    Thread.sleep(200);

    // Remove a child
    hello.tell(RemoveCmd.builder().name("child-02").build());
    Thread.sleep(200);
  }

  void executeExceptionCase(SupervisorStrategy strategy) throws InterruptedException {

    ActorRef<Message> hello = testKit.spawn(HelloBehavior.create(strategy), "hello-behavior");
    var errorMessage = Hello.builder().id(UUID.randomUUID()).message(null).build();
    var helloMessage = Hello.builder().id(UUID.randomUUID()).message("Hello World").build();
    hello.tell(errorMessage);
    Thread.sleep(200);
    hello.tell(helloMessage);
    Thread.sleep(200);
  };

  @Test
  public void testEmptyHelloMessage_restartWhenFailure() throws InterruptedException {
    executeExceptionCase(SupervisorStrategy.restart());
  }

  @Test
  public void testEmptyHelloMessage_resumeWhenFailure() throws InterruptedException {
    executeExceptionCase(SupervisorStrategy.resume());
  }
}