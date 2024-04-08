package bean.pcb.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
/**
 * 作业包含的命令类
 * 包括命令类型和执行次数
 * 并做如下假设: 一条命令占用内存空间为1个单位
 */
public class Command {
    public int type;
    public int time;
}
