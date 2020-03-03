import Messages.SearchingRequest;
import Messages.SearchingResponse;
import Messages.SearchingType;
import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import static Messages.SearchingType.SEARCH;

public class DatabaseScanner extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    private String databasePath;

    public DatabaseScanner(String databasePath){
        this.databasePath = databasePath;
    }

    @Override
    public AbstractActor.Receive createReceive(){
        return receiveBuilder().
                match(SearchingRequest.class, request ->{
                    SearchingResponse response = scan(request);
                    getSender().tell(response, getSelf());
                })
                .matchAny(o -> log.info("received unknown message"))
                .build();
    }

    private SearchingResponse scan(SearchingRequest request) throws FileNotFoundException {
        Scanner reader = new Scanner(new File(this.databasePath));
        while (reader.hasNext()){
            String line = reader.nextLine();
            String [] bookDetails = line.split(" : ");
            if(bookDetails[0].equals(request.getContent())) {
                if(request.getType() == SEARCH){
                    return new SearchingResponse(request.getType(), request.getReceiverPath(), line);
                }
                else{
                    return new SearchingResponse(request.getType(), request.getReceiverPath(), bookDetails[0]);
                }
            }
        }

        return new SearchingResponse(request.getType(), request.getReceiverPath(), null);
    }
}
