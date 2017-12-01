package Sys;

/**
 * @project OS_Simulator
 */
public class SharedObject {

    private String sharedPiece;

    public SharedObject(int id) {
        this.sharedPiece = String.format("%d mod, ", id);
    }

    public void setSharedPiece(int id) {
        this.sharedPiece += (id + " mod, ");
    }

    public String getSharedPiece() { return this.sharedPiece; }
}
