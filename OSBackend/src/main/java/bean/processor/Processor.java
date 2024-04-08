package bean.processor;

import bean.pcb.task.Task;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import bean.memory.SegmentMemory;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 处理机类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Processor {
    //存储处理机相关的内存空间
    public SegmentMemory systemMemory;

    //CPU核心标识
    public int coreId;
    //任务数量
    public int taskCount;
    public Task curTask;
    public ArrayList<Task> readyQueue;

    public boolean queFull() {
        return readyQueue.size() >= ProcessorParam.MAX_READY_QUEUE_SIZE;
    }
}
