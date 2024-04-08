package ui;

import backend.ProcessThread;
import bean.memory.SegmentMemory;
import bean.pcb.command.Command;
import bean.pcb.job.Job;
import bean.pcb.task.Task;
import func.ProcessorManager;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;

public class ProcessMonitorUI extends JFrame {
    private final JTextArea consoleTextArea;
    private final JTextField inputField1;
    private final JTextField inputField2;
    private final JTextField inputField3;

    public ProcessMonitorUI() {
        // Set up the main frame
        setTitle("Process Monitor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);

        // Create components
        JPanel topPanel = new JPanel(new BorderLayout());
        consoleTextArea = new JTextArea();
        consoleTextArea.setEditable(false);
        consoleTextArea.setFont(new Font("Monospaced", Font.PLAIN, 24));
        JScrollPane scrollPane = new JScrollPane(consoleTextArea);
        topPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        inputField1 = new JTextField(10);
        inputField2 = new JTextField(10);
        inputField3 = new JTextField(10);

        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(e -> {
            // Handle button click event
            Command command = new Command();
            Job job = new Job();
            job.commandList = new ArrayList<>();
            Task task = new Task();

            command.type = Integer.parseInt(inputField1.getText());

            command.time = Integer.parseInt(inputField2.getText());

            task.priority = Integer.parseInt(inputField3.getText());

            task.pid = command.type;


            for (int i = 0; i < 5; i++) {
                job.commandList.add(command);
            }
            job.size = job.commandList.size();
            job.cmdCount = 5;
            task.job = job;
            task.allocatedAddress = new SegmentMemory();
            ProcessorManager.SYSTEM.poolQueue.add(task);
            // Process the inputs as needed
        });
        bottomPanel.add(new JLabel("命令类型"));
        bottomPanel.add(inputField1);
        bottomPanel.add(new JLabel("执行时间"));
        bottomPanel.add(inputField2);
        bottomPanel.add(new JLabel("优先级"));
        bottomPanel.add(inputField3);
        bottomPanel.add(submitButton);

        // Add components to the frame
        add(topPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    // Method to redirect console output to JTextArea
    private void redirectConsoleOutput() {
        PrintStream printStream = new PrintStream(new CustomOutputStream(consoleTextArea));
        System.setOut(printStream);
        System.setErr(printStream);
    }

    public static void main(String[] args) {
        // Create and display the UI
        ProcessMonitorUI ui = new ProcessMonitorUI();
        ui.redirectConsoleOutput();
        ui.setVisible(true);
        ProcessThread cpu = new ProcessThread();
        new Thread(cpu).start();
        new Thread(cpu).start();

    }
}

// Custom OutputStream to redirect console output to JTextArea
class CustomOutputStream extends OutputStream {
    private final JTextArea textArea;

    public CustomOutputStream(JTextArea textArea) {
        this.textArea = textArea;
    }

    @Override
    public void write(int b) throws IOException {
        textArea.append(String.valueOf((char) b));
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }
}

