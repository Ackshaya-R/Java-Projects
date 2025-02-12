import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class Taskeditorapp extends JFrame {
    private List<Task> tasks = new ArrayList<>();
    private DefaultTableModel tableModel;

    public Taskeditorapp() {
        setTitle("Task Editor");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Task Table
        String[] columnNames = {"ID", "Name", "Description", "Completed"};
        tableModel = new DefaultTableModel(columnNames, 0);
        JTable taskTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(taskTable);

        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Task");
        JButton updateButton = new JButton("Update Task");
        JButton deleteButton = new JButton("Delete Task");
        JButton exitButton = new JButton("Exit");

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(exitButton);

        // Add Components to Frame
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Action Listeners
        addButton.addActionListener(new AddTaskAction());
        updateButton.addActionListener(new UpdateTaskAction(taskTable));
        deleteButton.addActionListener(new DeleteTaskAction(taskTable));
        exitButton.addActionListener(e -> System.exit(0));
    }

    // Action for Adding a Task
    private class AddTaskAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String name = JOptionPane.showInputDialog("Enter Task Name:");
            if (name == null || name.trim().isEmpty()) return;

            String description = JOptionPane.showInputDialog("Enter Task Description:");
            if (description == null || description.trim().isEmpty()) return;

            Task newTask = new Task(tasks.size() + 1, name, description, false);
            tasks.add(newTask);

            tableModel.addRow(new Object[]{newTask.getId(), newTask.getName(), newTask.getDescription(), newTask.isCompleted()});
            JOptionPane.showMessageDialog(null, "Task added successfully!");
        }
    }

    // Action for Updating a Task
    private class UpdateTaskAction implements ActionListener {
        private final JTable taskTable;

        public UpdateTaskAction(JTable taskTable) {
            this.taskTable = taskTable;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
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
            if (newName == null || newDescription == null) return;

            task.setName(newName);
            task.setDescription(newDescription);

            tableModel.setValueAt(newName, selectedRow, 1);
            tableModel.setValueAt(newDescription, selectedRow, 2);

            JOptionPane.showMessageDialog(null, "Task updated successfully!");
        }
    }

    // Action for Deleting a Task
    private class DeleteTaskAction implements ActionListener {
        private final JTable taskTable;

        public DeleteTaskAction(JTable taskTable) {
            this.taskTable = taskTable;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
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
    }

    // Task Class
    private static class Task {
        private final int id;
        private String name;
        private String description;
        private boolean completed;

        public Task(int id, String name, String description, boolean completed) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.completed = completed;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public boolean isCompleted() {
            return completed;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public void setCompleted(boolean completed) {
            this.completed = completed;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Taskeditorapp app = new Taskeditorapp();
            app.setVisible(true);
        });
    }
}

