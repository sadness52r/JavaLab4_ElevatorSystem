import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class RequestController implements Callable<String> {
    private final int nFloors;
    private final AtomicInteger nRequests;
    private final int maxDelay;
    private final ConcurrentLinkedQueue<Request> requests;
    private final Random random;

    RequestController(int nFloors, AtomicInteger nRequests, int maxDelay, ConcurrentLinkedQueue<Request> requests){
        this.nFloors = nFloors;
        this.requests = requests;
        this.nRequests = nRequests;
        this.maxDelay = maxDelay;
        random = new Random();
    }

    private void generateRequest(){
        int floor = random.nextInt(nFloors);
        int destinationFloor = random.nextInt(nFloors);
        while (destinationFloor == floor) {
            destinationFloor = random.nextInt(nFloors);
        }
        requests.add(new Request(floor, destinationFloor));
        System.out.println("A new request was generated on " + (floor + 1) + " floor to " + (destinationFloor + 1) + " floor");
    }

    @Override
    public String call() throws Exception {
        for (int i = 0; i < nRequests.get(); i++) {
            generateRequest();
            try {
                Thread.sleep(random.nextInt(maxDelay + 1) * 1000L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return "All requests were generated!";
    }
}