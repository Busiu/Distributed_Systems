import org.jgroups.*;
import org.jgroups.protocols.*;
import org.jgroups.protocols.pbcast.*;
import org.jgroups.stack.ProtocolStack;
import org.jgroups.util.Util;
import pl.edu.agh.dsrg.sr.protos.HashTableOperationProtos;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;

public class DistributedMap extends ReceiverAdapter implements SimpleStringMap
{
    private int ID;
    private JChannel channel;
    private HashMap<String, Integer> map;

    public DistributedMap(int ID, String channelName) throws Exception
    {
        this.ID = ID;
        channel = new JChannel(false);
        map = new HashMap<>();

        initProtocolStack();
        channel.connect(channelName);
        setReceiver();
        channel.getState(null, 10000);
    }

    public HashMap<String, Integer> getMap()
    {
        return map;
    }

    @Override
    public boolean containsKey(String key)
    {
        return map.containsKey(key);
    }

    @Override
    public Integer get(String key)
    {
        return map.get(key);
    }

    @Override
    public void put(String key, Integer value)
    {
        System.out.println(ID + " Putting");

        if(containsKey(key))
        {
            System.out.println("There is already such a key in Hash Map");
            return;
        }
        else
        {
            send(HashTableOperationProtos.HashTableOperation.OperationType.PUT, key, value);
            map.put(key, value);
        }
    }

    @Override
    public Integer remove(String key)
    {
        System.out.println(ID + " Removing");

        send(HashTableOperationProtos.HashTableOperation.OperationType.REMOVE, key, 0);
        Integer ans = map.get(key);
        map.remove(key);

        return ans;
    }

    public void send(HashTableOperationProtos.HashTableOperation.OperationType operationType, String key, double value)
    {
        HashTableOperationProtos.HashTableOperation operation = HashTableOperationProtos.HashTableOperation.newBuilder()
                .setType(operationType)
                .setKey(key)
                .setValue(value)
                .build();
        byte[] toSend = operation.toByteArray();
        Message msg = new Message(null, null, toSend);
        try
        {
            channel.send(msg);
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }
    }

    private void initProtocolStack()
    {
        try
        {
            ProtocolStack stack = new ProtocolStack();
            channel.setProtocolStack(stack);
            stack.addProtocol(new UDP().setValue("mcast_group_addr", InetAddress.getByName("230.100.200.34")))
                    .addProtocol(new PING())
                    .addProtocol(new MERGE3())
                    .addProtocol(new FD_SOCK())
                    .addProtocol(new FD_ALL().setValue("timeout", 12000).setValue("interval", 3000))
                    .addProtocol(new VERIFY_SUSPECT())
                    .addProtocol(new BARRIER())
                    .addProtocol(new NAKACK2())
                    .addProtocol(new UNICAST3())
                    .addProtocol(new STABLE())
                    .addProtocol(new GMS())
                    .addProtocol(new UFC())
                    .addProtocol(new MFC())
                    .addProtocol(new FRAG2())
                    .addProtocol(new SEQUENCER())
                    .addProtocol(new FLUSH())
                    .addProtocol(new STATE_TRANSFER());
            stack.init();
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }
    }

    private void setReceiver()
    {
        channel.setReceiver(new ReceiverAdapter()
        {
            @Override
            public void getState(OutputStream outputStream) throws Exception
            {
                System.out.println("Getting State");

                Util.objectToStream(map, new DataOutputStream((outputStream)));
            }

            @Override
            public void setState(InputStream inputStream) throws Exception
            {
                System.out.println("Setting State");

                HashMap<String, Integer> hashMap;
                hashMap = (HashMap<String, Integer>) Util.objectFromStream(new DataInputStream(inputStream));

                map.clear();
                map.putAll(hashMap);
            }

            @Override
            public void viewAccepted(View view)
            {
                if(view instanceof MergeView)
                {
                    MergeView tmpMergeView = (MergeView) view;
                    List<View> subgroups = tmpMergeView.getSubgroups();
                    View tmpView = subgroups.get(0);

                    Address address = channel.getAddress();
                    if(!tmpView.getMembers().contains(address))
                    {
                        System.out.println("Not member of the new primary partition ("
                                + tmpView + "), will re-acquire the state");
                        try
                        {
                            channel.getState(null, 30000);
                        }
                        catch (Exception e)
                        {
                            System.out.println(e.getMessage());
                        }
                    }
                    else
                    {
                        System.out.println("Member of the new primary partition ("
                                + tmpView + "), will do nothing");
                    }
                }
            }

            @Override
            public void receive(Message msg)
            {
                if(msg.getSrc().equals(channel.getAddress()))
                {
                    return;
                }

                HashTableOperationProtos.HashTableOperation operation;
                try
                {
                    operation = HashTableOperationProtos.HashTableOperation.parseFrom(msg.getBuffer());
                }
                catch (Exception e)
                {
                    System.out.println(e.getMessage());
                    return;
                }
                String key = operation.getKey();
                Double doubleValue = operation.getValue();
                Integer value = doubleValue.intValue();

                switch(operation.getType())
                {
                    case PUT:
                    {
                        map.put(key, value);
                        break;
                    }
                    case REMOVE:
                    {
                        map.remove(key);
                        break;
                    }
                }
            }
        });
    }
}
