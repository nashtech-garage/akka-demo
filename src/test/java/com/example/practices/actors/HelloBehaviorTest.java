package com.example.practices.actors;

import akka.actor.testkit.typed.javadsl.ActorTestKit;
import akka.actor.typed.ActorRef;
import akka.actor.typed.SupervisorStrategy;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.GroupRouter;
import akka.actor.typed.javadsl.PoolRouter;
import akka.actor.typed.javadsl.Routers;
import akka.actor.typed.receptionist.Receptionist;
import akka.actor.typed.receptionist.ServiceKey;
import com.example.practices.messages.Hello;
import com.example.practices.messages.Message;
import com.example.practices.messages.RemoveCmd;
import com.example.practices.messages.SpawnCmd;
import org.junit.*;

import java.util.List;
import java.util.Objects;
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


  @Test
  public void testRoute_PoolRouter_Routing() throws InterruptedException {
    int poolSize = 4;
    List<String> messages = List.of("Hello", "Xin Chao", "bonjour", "Ciaos", "Hola", "Ni hao", "annyeonghaseyo", "Kon'nichiwa", "privet", "Ahoj", "Geia sou");
    PoolRouter<Message> pool = Routers.pool( poolSize, Behaviors.supervise(HelloBehavior.create()).onFailure(SupervisorStrategy.restart()));
    ActorRef<Message> router = testKit.spawn(pool, "hello-behavior");

    messages.stream().map(m -> Hello.builder().message(m).build()).forEach(router::tell);
    Thread.sleep(500);
  }

  @Test
  public void testRoute_PoolRouter_BroadCasting() throws InterruptedException {
    int poolSize = 4;

    PoolRouter<Message> pool = Routers.pool( poolSize, Behaviors.supervise(HelloBehavior.create()).onFailure(SupervisorStrategy.restart()));
    PoolRouter<Message> broadcastingPool = pool.withBroadcastPredicate( msg -> msg instanceof Hello && Objects.equals(((Hello)msg).getMessage(), "hello"));
    ActorRef<Message> router = testKit.spawn(broadcastingPool, "hello-behavior");

    // broadcast the Hello Message
    router.tell(Hello.builder().message("hello").build());

    // fire a message
    router.tell(Hello.builder().message("Xin Chao").build());
    Thread.sleep(500);
  }

  @Test
  public void testRoute_GroupRouter_BroadCasting() throws InterruptedException {
    List<String> messages = List.of("Hello", "Xin Chao", "bonjour", "Ciaos", "Hola", "Ni hao", "annyeonghaseyo", "Kon'nichiwa", "privet", "Ahoj", "Geia sou");
    List<String> workerNames = List.of("worker-1", "worker-2","worker-3");
    // Create Service Key
    ServiceKey<Message> serviceKey = ServiceKey.create(Message.class, "hello-worker");

    workerNames.forEach( name -> {
      // Create Worker
      ActorRef<Message> worker =  testKit.spawn(HelloBehavior.create(), name);

      // Add to receptionist
      testKit.system().receptionist().tell(Receptionist.register(serviceKey, worker));
    });

    //
    GroupRouter<Message> group = Routers.group(serviceKey);
    ActorRef<Message> router = testKit.spawn(group, "worker-group");

    messages.stream().map(m -> Hello.builder().message(m).build()).forEach(router::tell);
    Thread.sleep(500);
  }
}