package Messages;

import akka.actor.ActorPath;

import java.io.Serializable;

public class OrderResponse implements Serializable {

    private ActorPath receiverPath;
    private String content;

    public OrderResponse(ActorPath receiverPath, String content){
        this.receiverPath = receiverPath;
        this.content = content;
    }

    public ActorPath getReceiverPath() {
        return receiverPath;
    }

    public String getContent() {
        return content;
    }
}