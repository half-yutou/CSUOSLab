package bean.pcb.task;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Deque;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDeque {
    public Deque<Task> taskDeque;
}
