package backend;

import bean.memory.SegmentMemory;
import bean.pcb.command.Command;
import bean.pcb.job.Job;
import bean.pcb.task.Task;
import func.ProcessorManager;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        ProcessThread cpu = new ProcessThread();
        new Thread(cpu).start();
        new Thread(cpu).start();
        int pid = 1;
        while (true) {
            Command command = new Command();
            Job job = new Job();
            job.commandList = new ArrayList<>();
            Task task = new Task();
            System.out.println("请输入命令类型(int):");
            command.type = scan.nextInt();
            System.out.println("请输入命令执行时间(int, ms)");
            command.time = scan.nextInt();
            System.out.println("请输入进程优先级(int)");
            task.priority = scan.nextInt();
            task.pid = pid;
            pid += 1;

            for (int i = 0; i < 5; i++) {
                job.commandList.add(command);
            }
            job.size = job.commandList.size();
            job.cmdCount = 5;
            task.job = job;
            task.allocatedAddress = new SegmentMemory();
            ProcessorManager.SYSTEM.poolQueue.add(task);
        }
    }
}
