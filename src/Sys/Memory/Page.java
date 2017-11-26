package Sys.Memory;

/**
 * @project OS_Simulator
 *
 */
public class Page {

    public final int associatedpID;
    public final int pageNumber;
    public boolean validBit;
    public final char pageLetter;


    public Page(int processId, int pageNumber) {
        this.associatedpID = processId;
        this.pageNumber = pageNumber;
        this.validBit = false;
        this.pageLetter = (char)(processId + 65);
    }

    public String getAssociatedPageNumber() {
        return String.format("%c %d %d", this.pageLetter, this.associatedpID,getValidBitRep());
    }

    public int getValidBitRep() {
        return (validBit ? 1 : 0);
    }



}
