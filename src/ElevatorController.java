import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ElevatorController implements Callable<String> {
    private final AtomicInteger remainRequests;
    Elevator elevator1, elevator2;
    private final ConcurrentLinkedQueue<Request> requests;
    private final ExecutorService executorService;

    ElevatorController(AtomicInteger remainRequests, ConcurrentLinkedQueue<Request> requests){
        this.remainRequests = remainRequests;
        this.requests = requests;
        elevator1 = new Elevator(remainRequests, 1);
        elevator2 = new Elevator(remainRequests, 2);
        executorService = Executors.newSingleThreadExecutor();
    }

    private Elevator getFreeElevator(Request request){
        if (elevator1.getState() == DIRECTION.RESTS && elevator2.getState() == DIRECTION.RESTS){
            int dist1 = Math.abs(elevator1.getCurFloor() - request.getStartFloor());
            int dist2 = Math.abs(elevator2.getCurFloor() - request.getStartFloor());
            if (dist1 <= dist2) {
                System.out.printf("Elevator %d accepted a request on %d floor\n", 1, request.getStartFloor() + 1);
                return elevator1;
            }
            System.out.printf("Elevator %d accepted a request on %d floor\n", 2, request.getStartFloor() + 1);
            return elevator2;
        }
        else if (elevator1.getState() == DIRECTION.RESTS && elevator2.getState() != DIRECTION.RESTS){
            System.out.printf("Elevator %d accepted a request on %d floor\n", 1, request.getStartFloor() + 1);
            return elevator1;
        }
        else if(elevator1.getState() != DIRECTION.RESTS && elevator2.getState() == DIRECTION.RESTS){
            System.out.printf("Elevator %d accepted a request on %d floor\n", 2, request.getStartFloor() + 1);
            return elevator2;
        }
        return null;
    }

    @Override
    public String call() throws Exception {
        while (remainRequests.get() > 0){
            if (!requests.isEmpty()){
                if (elevator1.getState() == DIRECTION.RESTS || elevator2.getState() == DIRECTION.RESTS){
                    Request request = requests.poll();
                    Elevator freeElevator = getFreeElevator(request);
                    if (freeElevator == null){
                        continue;
                    }
                    freeElevator.setMainRequest(request);
                    Thread.sleep(500);
                    Future<String> resultMoving = executorService.submit(freeElevator);

                    System.out.println(resultMoving.get());
                }
            }
        }
        executorService.shutdownNow();
        return "All elevators finished their work!";
    }
}
