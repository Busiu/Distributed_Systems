import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) throws Exception{

        Scanner scanner = new Scanner(System.in);
        System.out.println("Please, enter the location of your config file:");
        String configPath = scanner.next();
        System.out.println("Please, enter the name of your actor system");
        String actorSystemName = scanner.next();


        // config
        File configFile = new File(configPath);
        Config config = ConfigFactory.parseFile(configFile);

        // create actor system & actors
        final ActorSystem system = ActorSystem.create(actorSystemName, config);
        final ActorRef clientActor = system.actorOf(Props.create(ClientActor.class), "client");

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            String line = br.readLine();
            if (line.equals("q")) {
                break;
            }
            clientActor.tell(line, null);
        }

        system.terminate();
    }
}
