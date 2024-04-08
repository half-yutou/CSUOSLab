package bean.pcb.task;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import bean.memory.SegmentMemory;
import bean.pcb.job.Job;

/**
 * 进程任务---低级调度的单位
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    public volatile int state;
    //pid = 0 为系统进程 else 用户进程
    public int pid;
    //status = 0 独立进程 else 同步进程
    public int status = 0;
    public int priority;
    //public Integer processor;
    //记录相对地址(目的: 方便保存与恢复现场)
    public int curCodeIndex = 0;

    //该任务对应的作业
    public Job job;

    //该任务所在内存分区,记录绝对地址
    public SegmentMemory allocatedAddress;
}