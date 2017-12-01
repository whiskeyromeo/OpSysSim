package Sys;

/**
 * @project OS_Simulator
 *
 * Provides a simple means of communication between parents/children/grandchildren
 */
public class CommObject {

    private String sharedPiece;

    public CommObject(int id) {
        this.sharedPiece = String.format("%d mod, ", id);
    }

    public void setSharedPiece(int id) {
        this.sharedPiece += (id + " mod, ");
    }

    public String getSharedPiece() { return this.sharedPiece; }
}
