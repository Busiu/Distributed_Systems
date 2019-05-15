import java.util.Scanner;

public class Main {

    private static String channelName = "Posylanko";
    private static Scanner scan = new Scanner(System.in);

    public static void main(String[] args)
    {
        System.setProperty("java.net.preferIPv4Stack","true");

        System.out.println("Choose mode:");
        System.out.println("\t1. Testing");
        System.out.println("\t2. Application");

        int chosenMode = scan.nextInt();
        switch (chosenMode)
        {
            case 1:
            {
                Test test = new Test(channelName);
                test.run();
                break;
            }
            case 2:
            {
                UserInterface userInterface = new UserInterface(channelName);
                userInterface.run();
                break;
            }
            default:
            {
                System.out.println("You are not funny at all");
                break;
            }
        }
    }
}
