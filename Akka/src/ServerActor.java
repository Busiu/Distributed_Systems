import Messages.*;
import akka.actor.AbstractActor;
import akka.actor.ActorPath;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import java.util.HashMap;

import static Messages.SearchingType.*;

public class ServerActor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    private final int numberOfScanners = 2;

    private HashMap<ActorPath, Integer> searchCounters = new HashMap<>();
    private HashMap<ActorPath, SearchingResponse> searchResponses = new HashMap<>();

    @Override
    public AbstractActor.Receive createReceive() {
        return receiveBuilder()
                .match(SearchingRequest.class, request -> {
                    request.setReceiverPath(getSender().path());
                    request.setType(SEARCH);
                    searchCounters.put(getSender().path(), 0);
                    searchResponses.put(getSender().path(), null);

                    for(int i = 1; i <= numberOfScanners; i++){
                        String name = "DatabaseScanner" + i;
                        context().child(name).get().tell(request, getSelf());
                    }
                })
                .match(SearchingResponse.class, response -> {
                    SearchingResponse currentResponse = searchResponses.get(response.getReceiverPath());
                    if(currentResponse == null){
                        searchResponses.put(response.getReceiverPath(), response);
                    }
                    else if(response.getContent() != null){
                        searchResponses.put(response.getReceiverPath(), response);
                    }

                    Integer counter = searchCounters.get(response.getReceiverPath());
                    counter++;
                    searchCounters.put(response.getReceiverPath(), counter);
                    if(counter == numberOfScanners){
                        currentResponse = searchResponses.get(response.getReceiverPath());
                        if(currentResponse.getType() == SEARCH){
                            getContext().actorSelection(response.getReceiverPath()).tell(currentResponse, getSelf());
                        }
                        else{
                            OrderRequest request = new OrderRequest(
                                    currentResponse.getReceiverPath(),
                                    currentResponse.getContent());
                            context().child("OrderBoy").get().tell(request, getSelf());
                        }
                    }
                })
                .match(OrderRequest.class, orderRequest -> {
                    SearchingRequest request = new SearchingRequest(orderRequest.getContent());
                    request.setReceiverPath(getSender().path());
                    request.setType(ORDER);
                    searchCounters.put(getSender().path(), 0);
                    searchResponses.put(getSender().path(), null);

                    for(int i = 1; i <= numberOfScanners; i++) {
                        String name = "DatabaseScanner" + i;
                        context().child(name).get().tell(request, getSelf());
                    }
                })
                .match(OrderResponse.class, response -> {
                    getContext().actorSelection(response.getReceiverPath()).tell(response, getSelf());
                })
                .matchAny(o -> log.info("received unknown message"))
                .build();
    }

    @Override
    public void preStart(){
        for(int i = 1; i <= numberOfScanners; i++){
            String path = "databases/database" + i;
            String name = "DatabaseScanner" + i;
            context().actorOf(Props.create(DatabaseScanner.class, path), name);
        }
        String orderBoyName = "OrderBoy";
        context().actorOf(Props.create(OrderBoy.class), orderBoyName);
    }
}