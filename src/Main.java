import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in);
        System.out.print("Please, enter number of floors: ");
        int nFloors = scanner.nextInt();
        System.out.print("\nPlease, enter number of requests: ");
        AtomicInteger nRequests = new AtomicInteger(scanner.nextInt());
        System.out.print("\nPlease, enter max delay of requests: ");
        int maxDelay = scanner.nextInt();

        Building building = new Building(nFloors, nRequests, maxDelay);
        try {
            building.startSimulation();
        } catch (ExecutionException | InterruptedException e) {
            System.out.println(e.getMessage());
        }
        finally {
            scanner.close();
        }
    }
}