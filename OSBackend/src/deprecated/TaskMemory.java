package bean.memory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 记录一个task在一块内存分区中的分布
 * 包括起始地址,结束地址(逻辑地址---相对于所在的动态分区)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskMemory {
    public Integer codeStartIndex;
    public Integer codeEndIndex;

//    public Integer dataStart;
//    public Integer dataEnd;


}
