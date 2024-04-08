package bean.pcb.job;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Deque;

/**
 * 作业调度队列类
 * 包含一个用于作业调度的双端队列
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobDeque {
    public Deque<Job> jobDeque;
}
