package Messages;

import akka.actor.ActorPath;

import java.io.Serializable;

public class SearchingRequest implements Serializable {

    private SearchingType type;
    private ActorPath receiverPath;
    private String content;

    public SearchingRequest(String content){
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

    public void setType(SearchingType type){
        this.type = type;
    }

    public void setReceiverPath(ActorPath receiverPath){
        this.receiverPath = receiverPath;
    }
}