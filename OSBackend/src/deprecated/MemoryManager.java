package func;

import bean.memory.MemoryParam;
import bean.memory.SegmentMemory;
import bean.memory.SegmentMemoryList;


import java.util.concurrent.locks.ReentrantLock;

/**
 * 用于给指定任务分配内存
 * 必须保证线程安全
 */
public class MemoryManager {
    public SegmentMemory userMemory;
    public SegmentMemory sysMemory;

    public SegmentMemoryList userMemoryHead;
    public SegmentMemoryList sysMemoryHead;

    public ReentrantLock lock;

    public MemoryManager() {
        this.userMemory = new SegmentMemory();
        this.sysMemory = new SegmentMemory();
        this.userMemoryHead = new SegmentMemoryList(-1, userMemory);
        this.sysMemoryHead = new SegmentMemoryList(-1, sysMemory);
        this.lock = new ReentrantLock();
    }

    public void init() {
        this.lock.lock();

        //初始化整块用户内存空间和系统内存空间(左闭右开)
        this.sysMemory.startIndex = 0;
        this.sysMemory.endIndex = this.sysMemory.startIndex + MemoryParam.SYS_MEMORY_SIZE;
        this.userMemory.startIndex = this.sysMemory.endIndex;
        this.userMemory.endIndex = this.userMemory.startIndex + MemoryParam.USER_MEMORY_SIZE;

        //初始化系统内存头指针
        this.sysMemoryHead.pid = -1;
        this.sysMemoryHead.prev = null;
        this.sysMemoryHead.next = null;
        this.sysMemoryHead.val.startIndex = 0;
        this.sysMemoryHead.val.endIndex = this.sysMemory.endIndex;

        //初始化用户内存头指针
        this.userMemoryHead.pid = -1;
        this.userMemoryHead.prev = null;
        this.userMemoryHead.next = null;
        this.userMemoryHead.val.startIndex = this.sysMemory.endIndex;
        this.userMemoryHead.val.endIndex = this.userMemory.endIndex;
    }

    public Integer malloc(int pid, int size) {
        if (size <= 0) return -1;
        this.lock.lock();

        SegmentMemoryList ptr;
        if (pid == 0) { //系统内存申请
            ptr = sysMemoryHead;
        } else {
            ptr = userMemoryHead;
        }

        while (ptr != null) {
            int length = ptr.val.endIndex - ptr.val.startIndex;
            //ptr.pid = -1表示未分配
            /*
            length < size || 已分配: 检查下一个块
            length > size : 切割,赋值,退出
            length = size : 不用切割,直接赋值,退出
             */
            if (length < size || ptr.pid != -1) {
                ptr = ptr.next;
                continue;
            }
            if (length > size) {
                Integer newStartIndex = ptr.val.startIndex + size;
                //切割多余空间,形成新的结点
                SegmentMemory newSegMem = new SegmentMemory(newStartIndex, ptr.val.endIndex);
                SegmentMemoryList node = new SegmentMemoryList();
                node.val = newSegMem;
                node.prev = ptr;
                ptr.val.endIndex = newStartIndex;
                if (ptr.next != null) {
                    node.next = ptr.next;
                }
                ptr.next = node;
            }
            ptr.pid = pid;
            this.lock.unlock();

            return ptr.val.startIndex;
        }
        this.lock.unlock();
        return -1;
    }

    public void free(int pid, int sys) {
        this.lock.lock();
        SegmentMemoryList ptr;
        if (sys == 1) {
            ptr = this.sysMemoryHead;
        } else {
            ptr = this.userMemoryHead;
        }

        while (ptr.pid != pid) ptr = ptr.next;
        if (ptr.val == null) {
            this.lock.unlock();
            return;
        }

        SegmentMemoryList prev = ptr.prev;
        SegmentMemoryList next = ptr.next;
        ptr.pid = -1;
        //尝试merge空闲分区
        if (prev != null && prev.pid == -1) {//前驱结点是空闲内存块
            prev.val.endIndex = ptr.val.endIndex;
            prev.next = ptr.next;
            if (next != null) next.prev = prev;
            ptr = prev;
        }
        if (next != null && next.pid == -1) {//后继结点是空闲内存块
            ptr.val.endIndex = next.val.endIndex;
            ptr.next = next.next;
            if (next.next != null) next.next.prev = ptr;
        }
        this.lock.unlock();
    }

    public void userFree(int pid) {
        free(pid, 0);
    }

    public void sysFree(int pid) {
        free(pid, 1);
    }
}
