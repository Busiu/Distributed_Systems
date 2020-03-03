import java.util.Scanner;

public class UserInterface
{
    private DistributedMap client;
    private String channelName;
    private boolean quit;
    private Scanner scan;

    public UserInterface(String channelName)
    {
        this.channelName = channelName;
        this.quit = false;
        this.scan = new Scanner(System.in);
    }

    public void run()
    {
        try
        {
            client = new DistributedMap(1, channelName);
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }

        while(!quit)
        {
            System.out.println("Hello! Tell me what do you want to do with OUR Hash Table:");
            System.out.println("[get]");
            System.out.println("[put]");
            System.out.println("[remove]");
            System.out.println("[quit]");
            String command = scan.next();
            switch (command)
            {
                case "get":
                {
                    System.out.println("Gimme key:");
                    String key = scan.next();
                    Integer ans = client.get(key);
                    System.out.println("Got value: " + ans);
                    break;
                }
                case "put":
                {
                    System.out.println("Gimme key:");
                    String key = scan.next();
                    System.out.println("Gimme value:");
                    Integer value = scan.nextInt();
                    client.put(key, value);
                    break;
                }
                case "remove":
                {
                    System.out.println("Gimme key:");
                    String key = scan.next();
                    client.remove(key);
                    break;
                }
                case "quit":
                {
                    quit = true;
                    break;
                }
            }
        }
    }
}
