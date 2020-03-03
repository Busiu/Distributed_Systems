import java.util.ArrayList;
import java.util.HashMap;

public class Test
{
    private String channelName;
    private ArrayList<DistributedMap> clients;
    private static final int noClients = 10;

    public Test(String channelName)
    {
        this.channelName = channelName;
        this.clients = new ArrayList<>();
    }

    public void run()
    {
        try
        {
            for(int i = 0; i < noClients; i++)
            {
                System.out.println(i);
                clients.add(new DistributedMap(i, channelName));
            }
            for(int i = 0; i < noClients; i++)
            {
                clients.get(i).put(Integer.toString(i), i);
            }
            clients.get(1).put(Integer.toString(2), 8);

            HashMap hashMap = clients.get(0).getMap();
            hashMap.forEach((k, v) -> {
                System.out.format("key: %s, value: %d | ", k, v);
            });
            System.out.println();

            for(int i = 0; i < noClients; i+=2)
            {
                clients.get(i).remove(Integer.toString(i));
            }
            hashMap.forEach((k, v) -> {
                System.out.format("key: %s, value: %d | ", k, v);
            });

            //  --------------------------------------------------------------------------------------------------------

            clients.add(new DistributedMap(noClients, channelName));
            hashMap = clients.get(noClients).getMap();
            hashMap.forEach((k, v) -> {
                System.out.format("key: %s, value: %d | ", k, v);
            });

        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }

}
