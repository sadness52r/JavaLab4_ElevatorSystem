import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Building {
    private final int nFloors;
    private final int nThreads;
    private final RequestController requestController;
    private final ElevatorController elevatorController;
    private final ConcurrentLinkedQueue<Request> requests;
    private final ExecutorService executorService;

    public int getNFloors(){
        return nFloors;
    }

    Building(int nFloors, AtomicInteger nRequests, int maxDelayRequests){
        this.nFloors = nFloors;
        nThreads = 2;
        requests = new ConcurrentLinkedQueue<Request>();
        requestController = new RequestController(this.nFloors, nRequests, maxDelayRequests, requests);
        elevatorController = new ElevatorController(nRequests, requests);
        executorService = Executors.newFixedThreadPool(nThreads);
    }

    public void startSimulation() throws ExecutionException, InterruptedException {
        Future<String> resultRequests = executorService.submit(requestController);

        Future<String> resultElevators = executorService.submit(elevatorController);
        Thread.sleep(5000);

        System.out.println(resultRequests.get());
        System.out.println(resultElevators.get());

        executorService.shutdownNow();
    }
}
