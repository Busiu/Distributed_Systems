import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class Z3_SinkActor extends AbstractActor{
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    @Override
    public AbstractActor.Receive createReceive() {
        return receiveBuilder()
                .match(String.class, s -> {
                    String result = "You sent to me: " + s;
                    getContext().actorSelection(getSender().path()).tell(result, getSelf());
                })
                .matchAny(o -> log.info("received unknown message"))
                .build();
    }
}



