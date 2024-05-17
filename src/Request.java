public class Request{
    private final int startFloor;
    private final int destinationFloor;
    private final DIRECTION direction;

    public int getStartFloor(){
        return startFloor;
    }
    public int getDestinationFloor(){
        return destinationFloor;
    }
    public DIRECTION getDirection() { return direction; }

    Request(int startFloor, int destinationFloor){
        this.startFloor = startFloor;
        this.destinationFloor = destinationFloor;
        direction = this.destinationFloor > this.startFloor ? DIRECTION.UP : DIRECTION.DOWN;
    }
}