package Messages;

import akka.actor.ActorPath;

import java.io.Serializable;

public class OrderRequest implements Serializable {

    private ActorPath receiverPath;
    private String content;

    public OrderRequest(String content){
        this.content = content;
    }
    public OrderRequest(ActorPath receiverPath, String content){
        this.receiverPath = receiverPath;
        this.content = content;
    }

    public ActorPath getReceiverPath() {
        return receiverPath;
    }

    public String getContent() {
        return content;
    }

    public void setReceiverPath(ActorPath receiverPath){
        this.receiverPath = receiverPath;
    }
}
