package bean.memory;

/**
 * 内存块链表
 */
public class SegmentMemoryList {
    //pid表示占用当前结点的进程的pid,-1表示未分配
    public Integer pid = -1;
    public SegmentMemory val;

    public SegmentMemoryList prev;
    public SegmentMemoryList next;


    public SegmentMemoryList() {
    }

    public SegmentMemoryList(Integer pid, SegmentMemory val) {
        this.pid = pid;
        this.val = val;
    }

    public SegmentMemoryList(Integer pid, SegmentMemory val, SegmentMemoryList prev, SegmentMemoryList next) {
        this.pid = pid;
        this.val = val;
        this.prev = prev;
        this.next = next;
    }
}
