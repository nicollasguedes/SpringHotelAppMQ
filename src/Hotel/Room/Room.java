package Hotel.Room;

import java.io.Serializable;

public class Room implements Serializable {

    private String roomType;
    private int roomId;
    private double roomPrice;

    public Room(int roomId, String roomType, double roomPrice) {
        this.roomId = roomId;
        this.roomType = roomType;
        this.roomPrice = roomPrice;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public double getRoomPrice() {
        return roomPrice;
    }

    public void setRoomPrice(double roomPrice) {
        this.roomPrice = roomPrice;
    }

    @Override
    public String toString() {
        return
                "roomType='" + roomType  +
                        "\n, roomPrice=" + roomPrice + "\n\n";
    }
}
