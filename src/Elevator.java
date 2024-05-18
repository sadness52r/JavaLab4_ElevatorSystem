import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Elevator implements Callable<String> {
    private final int elevatorId;
    private final int speed;
    private volatile int curFloor;
    private int destinationFloor;
    private volatile DIRECTION state;
    private final ConcurrentLinkedQueue<Request> requests;
    private final ArrayList<Request> passengers;
    private volatile Request mainRequest;
    private final AtomicInteger remainRequests;

    public int getCurFloor(){
        return curFloor;
    }
    public void setCurFloor(int curFloor){
        this.curFloor = curFloor;
    }
    public int getDestinationFloor(){
        return destinationFloor;
    }
    public void setDestinationFloor(int destinationFloor){
        this.destinationFloor = destinationFloor;
    }
    public DIRECTION getState(){
        return state;
    }
    public void setState(DIRECTION state){
        this.state = state;
    }
    public Request getMainRequest(){
        return mainRequest;
    }
    public void setMainRequest(Request newMainRequest){
        mainRequest = newMainRequest;
    }

    Elevator(AtomicInteger remainRequests, int elevatorId){
        this.elevatorId = elevatorId;
        speed = 1;
        state = DIRECTION.RESTS;
        curFloor = 0;
        this.destinationFloor = -1;
        requests = new ConcurrentLinkedQueue<>();
        mainRequest = null;
        passengers = new ArrayList<>();
        this.remainRequests = remainRequests;
    }
    Elevator(int destinationFloor, AtomicInteger remainRequests, int elevatorId){
        this.elevatorId = elevatorId;
        speed = 1;
        state = DIRECTION.RESTS;
        curFloor = 0;
        this.destinationFloor = destinationFloor;
        requests = new ConcurrentLinkedQueue<>();
        mainRequest = null;
        passengers = new ArrayList<>();
        this.remainRequests = remainRequests;
    }

    private void getRequestsFromFloor() throws InterruptedException {
        synchronized (requests) {
            var iterator = requests.iterator();
            while (iterator.hasNext()) {
                System.out.println(remainRequests + " from getRequests");
                Request request = iterator.next();
                if (state == request.getDirection() && mainRequest.getDirection() == request.getDirection() && curFloor == request.getStartFloor()) {
                    Thread.sleep((int)(speed * 500));
                    iterator.remove();
                    passengers.add(request);

                    System.out.printf("A request has taken by elevator %d from %d floor\n", elevatorId, request.getStartFloor() + 1);
                }
            }
        }
    }
    private void freeRequestsToFloor() throws InterruptedException {
        Thread.sleep((int)(speed * 500));
        var iterator = passengers.iterator();
        while (iterator.hasNext()) {
            Request passenger = iterator.next();
            if (passenger.getDestinationFloor() == curFloor) {
                iterator.remove();
                remainRequests.set(remainRequests.get() - 1);
                //Thread.sleep((int)(velocityElev * 500));
                System.out.printf("A passenger has been free by elevator %d on %d floor\n", elevatorId, passenger.getDestinationFloor() + 1);
            }
        }

    }
    private void move(int pointFloor) throws InterruptedException {
        while (pointFloor > curFloor){
            try {
                getRequestsFromFloor();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            freeRequestsToFloor();
            curFloor++;
            System.out.printf("%d elevator floor: %d\n", elevatorId, curFloor + 1);
        }
        while (pointFloor < curFloor){
            getRequestsFromFloor();
            freeRequestsToFloor();
            curFloor--;
            System.out.printf("%d elevator floor: %d\n", elevatorId, curFloor + 1);
        }
        Thread.sleep(1000);
    }

    @Override
    public String call() throws Exception {
        Thread.sleep(500);
        System.out.printf("Elevator %d is moving to waiter on %d floor...\n", elevatorId, mainRequest.getStartFloor() + 1);
        move(mainRequest.getStartFloor());

        passengers.add(mainRequest);
        getRequestsFromFloor();
        freeRequestsToFloor();
        state = mainRequest.getDirection();

        Thread.sleep(500);
        System.out.printf("Elevator %d is delivering passengers from %d floor...\n", elevatorId, mainRequest.getStartFloor() + 1);
        move(mainRequest.getDestinationFloor());
        Thread.sleep(500);

        getRequestsFromFloor();
        freeRequestsToFloor();
        state = DIRECTION.RESTS;

        System.out.println("Elevator " + elevatorId + " stopped!");
        return "Elevator " + elevatorId + " stopped!";
    }
}