import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TaskEditorApplication extends JFrame {
    private List<Task> tasks = new ArrayList<>();
    private DefaultTableModel tableModel;
    private Timer deadlineTimer = new Timer(true);

    public TaskEditorApplication() {
        setTitle("Task Editor");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Initialize the UI
        initUI();

        // Load tasks and start notifications
        startDeadlineNotifier();
    }

    private void initUI() {
        // Table to display tasks
        String[] columnNames = {"ID", "Name", "Description", "Deadline", "Completed"};
        tableModel = new DefaultTableModel(columnNames, 0);
        JTable taskTable = new JTable(tableModel);
        taskTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        enableDragAndDrop(taskTable);
        JScrollPane scrollPane = new JScrollPane(taskTable);

        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Task");
        JButton updateButton = new JButton("Update Task");
        JButton deleteButton = new JButton("Delete Task");
        JButton markCompletedButton = new JButton("Mark Completed");
        JButton exitButton = new JButton("Exit");

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(markCompletedButton);
        buttonPanel.add(exitButton);

        // Add components to frame
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Add keyboard shortcuts
        addKeyboardShortcuts(taskTable);

        // Add button actions
        addButton.addActionListener(e -> addTask());
        updateButton.addActionListener(e -> updateTask(taskTable));
        deleteButton.addActionListener(e -> deleteTask(taskTable));
        markCompletedButton.addActionListener(e -> markTaskCompleted(taskTable));
        exitButton.addActionListener(e -> System.exit(0));
    }

    private void addTask() {
        String name = JOptionPane.showInputDialog("Enter Task Name:");
        if (name == null || name.trim().isEmpty()) return;

        String description = JOptionPane.showInputDialog("Enter Task Description:");
        if (description == null || description.trim().isEmpty()) return;

        String deadlineInput = JOptionPane.showInputDialog("Enter Deadline (YYYY-MM-DD):");
        LocalDate deadline = null;
        if (deadlineInput != null && !deadlineInput.trim().isEmpty()) {
            deadline = LocalDate.parse(deadlineInput);
        }

        Task newTask = new Task(tasks.size() + 1, name, description, deadline, false);
        tasks.add(newTask);
        tableModel.addRow(new Object[]{newTask.getId(), newTask.getName(), newTask.getDescription(), newTask.getDeadline(), newTask.isCompleted()});
        JOptionPane.showMessageDialog(null, "Task added successfully!");
    }

    private void updateTask(JTable taskTable) {
        int selectedRow = taskTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a task to update.");
            return;
        }

        int taskId = (int) tableModel.getValueAt(selectedRow, 0);
        Task task = tasks.stream().filter(t -> t.getId() == taskId).findFirst().orElse(null);
        if (task == null) return;

        String newName = JOptionPane.showInputDialog("Enter New Task Name:", task.getName());
        String newDescription = JOptionPane.showInputDialog("Enter New Task Description:", task.getDescription());
        String newDeadlineInput = JOptionPane.showInputDialog("Enter New Deadline (YYYY-MM-DD):", task.getDeadline());
        LocalDate newDeadline = null;
        if (newDeadlineInput != null && !newDeadlineInput.trim().isEmpty()) {
            newDeadline = LocalDate.parse(newDeadlineInput);
        }

        task.setName(newName);
        task.setDescription(newDescription);
        task.setDeadline(newDeadline);

        tableModel.setValueAt(newName, selectedRow, 1);
        tableModel.setValueAt(newDescription, selectedRow, 2);
        tableModel.setValueAt(newDeadline, selectedRow, 3);
        JOptionPane.showMessageDialog(null, "Task updated successfully!");
    }

    private void deleteTask(JTable taskTable) {
        int selectedRow = taskTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a task to delete.");
            return;
        }

        int taskId = (int) tableModel.getValueAt(selectedRow, 0);
        tasks.removeIf(t -> t.getId() == taskId);
        tableModel.removeRow(selectedRow);
        JOptionPane.showMessageDialog(null, "Task deleted successfully!");
    }

    private void markTaskCompleted(JTable taskTable) {
        int selectedRow = taskTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a task to mark as completed.");
            return;
        }

        int taskId = (int) tableModel.getValueAt(selectedRow, 0);
        Task task = tasks.stream().filter(t -> t.getId() == taskId).findFirst().orElse(null);
        if (task == null) return;

        task.setCompleted(true);
        tableModel.setValueAt(true, selectedRow, 4);
        JOptionPane.showMessageDialog(null, "Task marked as completed!");
    }

    private void enableDragAndDrop(JTable table) {
        table.setDragEnabled(true);
        table.setDropMode(DropMode.INSERT_ROWS);
        table.setTransferHandler(new TransferHandler() {
            // Optional: Implement specific drag-and-drop behavior here
        });
    }

    private void addKeyboardShortcuts(JTable taskTable) {
        JRootPane rootPane = this.getRootPane();

        // Add Task Shortcut
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ctrl N"), "addTask");
        rootPane.getActionMap().put("addTask", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addTask();
            }
        });

        // Delete Task Shortcut
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ctrl D"), "deleteTask");
        rootPane.getActionMap().put("deleteTask", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteTask(taskTable);
            }
        });
    }

    private void startDeadlineNotifier() {
        if (!SystemTray.isSupported()) {
            System.out.println("SystemTray is not supported.");
            return;
        }

        deadlineTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                for (Task task : tasks) {
                    if (task.getDeadline() != null && task.getDeadline().equals(LocalDate.now())) {
                        showNotification("Task Deadline Alert", "Task: " + task.getName() + " is due today!");
                    }
                }
            }
        }, 0, 60000); // Check deadlines every minute
    }

    private void showNotification(String title, String message) {
        try {
            SystemTray tray = SystemTray.getSystemTray();
            Image image = Toolkit.getDefaultToolkit().createImage("path/to/icon.png");
            TrayIcon trayIcon = new TrayIcon(image, "Task Notifications");
            trayIcon.setImageAutoSize(true);
            tray.add(trayIcon);

            trayIcon.displayMessage(title, message, TrayIcon.MessageType.INFO);

            tray.remove(trayIcon); // Remove after showing
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TaskEditorApplication app = new TaskEditorApplication();
            app.setVisible(true);
        });
    }
}

class Task {
    private int id;
    private String name;
    private String description;
    private LocalDate deadline;
    private boolean completed;

    public Task(int id, String name, String description, LocalDate deadline, boolean completed) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.deadline = deadline;
        this.completed = completed;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public LocalDate getDeadline() { return deadline; }
    public boolean isCompleted() { return completed; }

    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }
    public void setCompleted(boolean completed) { this.completed = completed; }
}

