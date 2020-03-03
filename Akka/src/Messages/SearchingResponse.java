package Messages;

import akka.actor.ActorPath;

import java.io.Serializable;

public class SearchingResponse implements Serializable {

    private SearchingType type;
    private ActorPath receiverPath;
    private String content;

    public SearchingResponse(SearchingType type, ActorPath receiverPath, String content){
        this.type = type;
        this.receiverPath = receiverPath;
        this.content = content;
    }

    public SearchingType getType(){
        return type;
    }

    public ActorPath getReceiverPath() {
        return receiverPath;
    }

    public String getContent() {
        return content;
    }
}
