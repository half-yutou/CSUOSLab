package backend;

import bean.pcb.task.Task;
import bean.processor.Processor;
import bean.system.SystemInfo;
import func.MemoryManager;
import func.ProcessorManager;

import java.util.ArrayList;

public class ProcessThread implements Runnable{
    public static MemoryManager MEMORY_MANAGER = new MemoryManager();
    public static ProcessorManager PROCESSOR_MANAGER = null;
    public static Processor PROCESSOR = null;

    static {
        ProcessThread.MEMORY_MANAGER.init();

        ProcessorManager.SYSTEM = new SystemInfo();
        ProcessorManager.SYSTEM.suspendQueue = new ArrayList<>();
        ProcessorManager.SYSTEM.poolQueue = new ArrayList<>();
        ProcessThread.PROCESSOR_MANAGER = new ProcessorManager();

        ProcessThread.PROCESSOR = new Processor();
        ProcessThread.PROCESSOR.readyQueue = new ArrayList<>();
    }


    @Override
    public void run() {
        while (true) {
            try {
                Task cur = PROCESSOR_MANAGER.shortScheduler(ProcessThread.PROCESSOR);
                if (cur != null) {
                    if (cur.curCodeIndex == 0) MEMORY_MANAGER.malloc(cur);
                    int res = PROCESSOR_MANAGER.execute(ProcessThread.PROCESSOR, cur);
                    Thread.sleep(3000);
                    if (res == 0) MEMORY_MANAGER.free(cur);
                } else {
                    break;
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
