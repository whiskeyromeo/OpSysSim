package Sys.Memory;

import Sys.PCB;

import java.util.LinkedList;

/**
 * Created by abdullahhashmi on 12/1/17.
 */
public class PageFrames
{
    private PCB[] procs;
    private LinkedList<Page> pageList = new LinkedList<Page>();
    private static int buffer = 512;
    private static int pageSize;
    private static int faults = 0;

    public PageFrames(int buffer, int pageSize, PCB[] proc)
    {
        this.buffer = buffer/pageSize;
        this.pageSize = pageSize;
        this.procs = proc;
    }

    private boolean insertPage(Page pg)
    {
        if(pageList.size() < buffer)
        {
            pageList.addFirst(pg);
            return true;
        }
        return false;
    }

    private boolean checkIfContains(Page pg)
    {
        return pageList.contains(pg);
    }

    public boolean FIFOD(int pid, int pageNumber)
    {
        Page pg = new Page((procs[pid].getPage(pageNumber)), pageNumber);

        if(!checkIfContains(pg))
        {
            if(!insertPage(pg))
            {
                pageList.removeLast();
                insertPage(pg);
            }
            return true;
        }
        return false;
    }

}
