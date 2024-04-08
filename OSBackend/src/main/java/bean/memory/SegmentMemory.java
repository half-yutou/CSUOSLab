package bean.memory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 动态分区的分区类
 * 存储task所在内存分区的起始地址和结束地址
 * @pid 占用当前内存块的进程id,若未分配则为-1
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SegmentMemory {
    public int pid;
    public int startIndex;
    public int endIndex;
}
