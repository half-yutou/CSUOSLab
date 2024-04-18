package func;

import bean.pcb.command.Command;
import bean.pcb.task.Task;
import bean.processor.Processor;
import bean.processor.ProcessorParam;
import bean.system.SystemInfo;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

@Data
@NoArgsConstructor
public class ProcessorManager{
    public static SystemInfo SYSTEM = null;
    public static ReentrantLock LOCK =  new ReentrantLock();

    /**
     * CPU执行任务的方法
     *
     * @param processor 执行任务的处理机
     * @param task 任务
     * @return int 正常结束:0;未执行完:-1
     */
    public int execute(Processor processor, Task task) {
        //recovery
        int curPoint = task.allocatedAddress.startIndex + task.curCodeIndex;


        //execute
        //without IO instruction
        int resultTime = ProcessorParam.ROUND_TIME;
        System.out.printf("pid: %d is executing on %d!\n", task.pid, curPoint);

        while (curPoint < task.allocatedAddress.endIndex && resultTime >= 0) {
            Command cmd = task.job.commandList.get(task.curCodeIndex);
            curPoint++;
            task.curCodeIndex++;
            resultTime -= 1;
            try {
                System.out.printf("processor %s:p->id: %d is Executing command type %d at %d in memory %d\n",
                        Thread.currentThread().getName(),
                        task.pid, cmd.type, curPoint, task.allocatedAddress.startIndex);
                //使用线程休眠模拟进程执行任务的时间
                Thread.sleep(cmd.time * 1000L);
            } catch (InterruptedException e) {
                System.out.println("Command execution is interrupted");
                break;
            }
        }
        task.priority -= 1;


        //mission accomplished return 0
        if (curPoint == task.allocatedAddress.endIndex) {
            System.out.printf("pid: %d has done!\n", task.pid);
            return 0;
        }

        //else save and return -1
        System.out.printf("pid: %d has saved on %d!\n", task.pid, curPoint);
        if (processor.queFull()) {
            SYSTEM.suspendQueue.add(task);
        } else {
            processor.readyQueue.add(task);
        }
        return -1;
    }

    /**
     * 短程调度
     *
     * @param processor 进行调度的处理机
     * @return 是否调度成功? 0 : -1
     */
    public Task shortScheduler(Processor processor) throws InterruptedException {
        LOCK.lock();

        if (processor.readyQueue.isEmpty()) {
            System.out.println("The ready queue is empty, \nand intermediate scheduling is attempted\n");
            LOCK.unlock();
            int flag = this.midScheduler(processor);
            if (flag == -1) {
                return null;
            }
        }
        LOCK.lock();
        //找到优先级最高的task, 并将其从就绪列表中移除
        Task task = processor.readyQueue.get(0);
        for (Task itertask : processor.readyQueue) {
            if (task.priority < itertask.priority) {
                task = itertask;
            }
        }
        processor.readyQueue.remove(task);
        LOCK.unlock();
        return task;
    }

    /**
     * 中程调度 suspendQue-->readyQue
     * @param processor 进行调度的处理机
     * @return 是否调度成功? 0 : -1
     */
    public Integer midScheduler(Processor processor) throws InterruptedException {
        LOCK.lock();
        if (SYSTEM.suspendQueue.isEmpty()) {
            System.out.println("The suspending queue is empty, \nand advanced scheduling is attempted\n");
            LOCK.unlock();
            int flag = this.longScheduler();
            if (flag == -1) {
                return -1;
            }
        }

        LOCK.lock();
        while (!processor.queFull() && !SYSTEM.suspendQueue.isEmpty()) {
            processor.readyQueue.add(SYSTEM.suspendQueue.get(0));
            SYSTEM.suspendQueue.remove(0);
        }
        LOCK.unlock();
        return 0;
    }

    /**
     * 长程调度 poolQue-->suspendQue
     * @return 是否调度成功? 0 : -1
     */
    public Integer longScheduler() throws InterruptedException {
        LOCK.lock();
        ArrayList<Task> pool = SYSTEM.poolQueue;
        ArrayList<Task> suspend = SYSTEM.suspendQueue;

        while (pool.isEmpty()) {
            System.out.println("The task pool is empty, the advanced scheduling fails, \nand the waiting to join the task is blocked!\n");
            Thread.sleep(10000);
        }

        while (!pool.isEmpty() && !SYSTEM.queFull()) {
            suspend.add(pool.get(0));
            pool.remove(0);
        }
        LOCK.unlock();
        return 0;
    }
}
