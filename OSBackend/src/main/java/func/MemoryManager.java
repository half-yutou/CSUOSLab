package func;

import bean.memory.MemoryParam;
import bean.memory.SegmentMemory;
import bean.pcb.task.Task;

import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 用于给指定任务分配内存
 * 必须保证线程安全
 */
public class MemoryManager {
    public LinkedList<SegmentMemory> sysMemoryList;
    public LinkedList<SegmentMemory> userMemoryList;
    public static ReentrantLock LOCK =  new ReentrantLock();

    public MemoryManager() {
        this.sysMemoryList = new LinkedList<>();
        this.userMemoryList = new LinkedList<>();
    }

    /**
     * 初始化系统内存和用户内存
     */
    public void init() {
        LOCK.lock();

        SegmentMemory sysMemoryHead = new SegmentMemory(-1,
                0,
                MemoryParam.SYS_MEMORY_SIZE);
        SegmentMemory userMemoryHead = new SegmentMemory(-1,
                MemoryParam.SYS_MEMORY_SIZE,
                MemoryParam.SYS_MEMORY_SIZE + MemoryParam.USER_MEMORY_SIZE);

        sysMemoryList.add(sysMemoryHead);
        userMemoryList.add(userMemoryHead);

        LOCK.unlock();
    }

    /**
     * 为Task分配对应内存
     * @param task 任务对象
     * @return 是否成功
     */
    public boolean malloc(Task task) {
        LOCK.lock();

        LinkedList<SegmentMemory> list;
        if (task.pid == 0) {
             list = sysMemoryList;
        } else {
            list = userMemoryList;
        }

        int needSize = task.job.size;
        for (SegmentMemory curSeg : list) {
            int st = curSeg.startIndex;
            int ed = curSeg.endIndex;
            int freeSpace = ed - st;
            /*
              1. 当前内存块大小不足,或已分配-->跳过当前内存块
              2. 当前内存块大小超出,进行切割并分配,且退出方法
              3. 当前内存块大小恰好,直接分配,且退出方法
             */
            if (needSize > freeSpace || curSeg.pid != -1) continue;
            if (needSize < freeSpace) {
                SegmentMemory remainNode = new SegmentMemory(-1, st + needSize, ed);
                curSeg.endIndex = remainNode.startIndex;
                list.add(list.indexOf(curSeg) + 1, remainNode);
            }
            task.allocatedAddress.startIndex = curSeg.startIndex;
            task.allocatedAddress.endIndex = curSeg.endIndex;
            curSeg.pid = task.pid;
            LOCK.unlock();
            return true;
        }

        LOCK.unlock();
        return false;
    }

    /**
     * 释放Task所占内存
     * @param task 任务对象
     */
    public void free(Task task) {
        LOCK.lock();

        LinkedList<SegmentMemory> list;
        if (task.pid == 0) {
            list = this.sysMemoryList;
        } else {
            list = this.userMemoryList;
        }

        boolean flag = false;
        int position = 0;
        for (SegmentMemory curSeg : list) {
            if (curSeg.pid == task.pid) {
                position = list.indexOf(curSeg);
                flag = true;
                break;
            }
        }
        if (!flag) {
            LOCK.unlock();
            return;
        }

        //free and try to merge
        list.get(position).pid = -1;
        if (position != 0 && list.get(position - 1).pid == -1) {
            list.get(position - 1).endIndex = list.get(position).endIndex;
            list.remove(position);
            position--;
        }

        if (position < list.size() - 1 && list.get(position + 1).pid == -1) {
            list.get(position).endIndex = list.get(position + 1).endIndex;
            list.remove(position + 1);
        }

        LOCK.unlock();
    }

}
