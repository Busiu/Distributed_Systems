import Messages.OrderRequest;
import Messages.OrderResponse;
import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import java.io.*;

public class OrderBoy extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    private String orderFilePath;

    public OrderBoy(){
        this.orderFilePath = "orders/orders.txt";
    }

    @Override
    public AbstractActor.Receive createReceive(){
        return receiveBuilder().
                match(OrderRequest.class, request ->{
                    OrderResponse response = order(request);
                    getSender().tell(response, getSelf());
                })
                .matchAny(o -> log.info("received unknown message"))
                .build();
    }

    private synchronized OrderResponse order(OrderRequest request) throws IOException {
        FileWriter fw = new FileWriter(this.orderFilePath, true);
        BufferedWriter bw = new BufferedWriter(fw);
        PrintWriter out = new PrintWriter(bw);

        if(request.getContent() != null)
            out.println(request.getContent());

        out.close();
        bw.close();
        fw.close();

        return new OrderResponse(request.getReceiverPath(), request.getContent() + " - successfully ordered!");
    }
}
