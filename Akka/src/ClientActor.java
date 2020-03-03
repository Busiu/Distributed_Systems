import Messages.OrderRequest;
import Messages.OrderResponse;
import Messages.SearchingRequest;
import Messages.SearchingResponse;
import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class ClientActor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    @Override
    public AbstractActor.Receive createReceive(){
        return receiveBuilder()
                .match(String.class, s -> {
                    String [] command = s.split(" : ");
                    if(s.startsWith("Search")){
                        SearchingRequest request = new SearchingRequest(command[1]);
                        getContext().actorSelection("akka.tcp://server_system@127.0.0.1:5101/user/server").tell(request, getSelf());
                    }
                    else if(s.startsWith("Order")){
                        OrderRequest request = new OrderRequest(command[1]);
                        getContext().actorSelection("akka.tcp://server_system@127.0.0.1:5101/user/server").tell(request, getSelf());
                    }
                    else{
                        System.out.println("Wrong command");
                    }
                })
                .match(SearchingResponse.class, response -> {
                    System.out.println(response.getContent());
                })
                .match(OrderResponse.class, response ->{
                    System.out.println(response.getContent());
                })
                .matchAny(o -> log.info("received unknown message"))
                .build();
    }
}