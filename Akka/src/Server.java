import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class Server {

    public static void main(String[] args) throws Exception{

        // config
        File configFile = new File("config/server.conf");
        Config config = ConfigFactory.parseFile(configFile);

        // create actor system & actors
        final ActorSystem system = ActorSystem.create("server_system", config);
        final ActorRef serverActor = system.actorOf(Props.create(ServerActor.class), "server");

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            String line = br.readLine();
            if (line.equals("q")) {
                break;
            }
            serverActor.tell(line, null);
        }

        system.terminate();
    }
}
