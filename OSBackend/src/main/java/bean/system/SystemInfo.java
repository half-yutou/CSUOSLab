package bean.system;

import bean.pcb.job.Job;
import bean.pcb.task.Task;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import bean.pcb.job.JobDeque;
import bean.processor.Processor;

import java.util.ArrayList;

/**
 * 操作系统相关信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemInfo {
    public int cpuCnt;
    public int procCnt;
    public Processor[] processors;

    //作业池,所有作业都会放入池中
    //本实验高级调度,中极调度均采用First Come First Service策略
    public ArrayList<Task> poolQueue;
    public ArrayList<Task> suspendQueue;


    public boolean queFull() {
        return suspendQueue.size() >= SystemParam.MAX_SUSPEND_QUEUE_SIZE;
    }
}
