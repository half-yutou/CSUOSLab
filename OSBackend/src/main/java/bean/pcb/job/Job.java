package bean.pcb.job;

import bean.pcb.command.Command;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

/**
 * 作业---高级调度的单位
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Job {
    public int id;
    //优先级考虑使用枚举类
    //此处的优先级指中长程调度的优先级,但是本实验使用FCFS,不存在优先级
    //public Integer priority;

    //size = CommandSize(default = 1) * cmdCount * commandList.size()
    public int cmdCount;
    public int size;
    public ArrayList<Command> commandList;
}
