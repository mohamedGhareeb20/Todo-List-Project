package todo;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class GUI extends Frame implements UpdateListener {

    private final Manager manager;
    private java.awt.List taskListUI;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public GUI(Manager manager) {
        super("Todo List");
        this.manager = manager;
        setupWindow();
        setupComponents();
    }

    public void initializeListener() {
        this.manager.setUpdateListener(this);
    }

    private void setupWindow() {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        setSize(850, 450);
        setLocation((screenSize.width - getWidth()) / 2, (screenSize.height - getHeight()) / 2);
        setLayout(new BorderLayout());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                System.exit(0);
            }
        });
    }

    private void setupComponents() {
        Label headerLabel = new Label(String.format(" %-30s | %-50s | %-16s | %-10s | %s", 
                              "Title", "Description", "Due Date", "Priority", "Status"));
        headerLabel.setFont(new Font(Font.MONOSPACED, Font.BOLD, 14));
        headerLabel.setBackground(new Color(220, 220, 220));
        add(headerLabel, BorderLayout.NORTH);

        taskListUI = new java.awt.List();
        taskListUI.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        add(taskListUI, BorderLayout.CENTER);

        Panel buttonPanel = new Panel();
        buttonPanel.setLayout(new FlowLayout());

        Button btnAdd = new Button("Add Task");
        Button btnEdit = new Button("Edit Task");
        Button btnDelete = new Button("Delete Task");
        Button btnMarkDone = new Button("Mark as Done");

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnMarkDone);
        add(buttonPanel, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> showTaskDialog(null));

        btnEdit.addActionListener(e -> {
            int index = taskListUI.getSelectedIndex();
            if (index >= 0) {
                showTaskDialog(manager.getTask(index));
            } else {
                showMessage("Please select a task to edit.");
            }
        });

        btnDelete.addActionListener(e -> {
            int index = taskListUI.getSelectedIndex();
            if (index >= 0) {
                manager.removeTask(index);
            } else {
                showMessage("Please select a task to delete.");
            }
        });

        btnMarkDone.addActionListener(e -> {
            int index = taskListUI.getSelectedIndex();
            if (index >= 0) {
                Task task = manager.getTask(index);
                task.setStatus("Done");
                manager.notifyDataChanged();
            } else {
                showMessage("Please select a task.");
            }
        });
    }

    @Override
    public void onDataChanged() {
        EventQueue.invokeLater(this::refreshListUI);
    }

    @Override
    public void onTaskOverdue() {
        EventQueue.invokeLater(() -> {
            Toolkit.getDefaultToolkit().beep(); 
            refreshListUI();
        });
    }

    private void refreshListUI() {
        int selectedIndex = taskListUI.getSelectedIndex();
        taskListUI.removeAll();
        
        List<Task> tasks = manager.getAllTasks();
        for (Task t : tasks) {
            boolean isOverdue = "OVERDUE".equals(t.getStatus());
            String marker = isOverdue ? "  << !!! OVERDUE !!! >>" : "";
            
            String row = String.format(" %-30s | %-50s | %-16s | %-10s | %s%s",
            truncate(t.getTitle(), 28),         
            truncate(t.getDescription(), 48),   
            t.getDueDate().format(dateFormatter),
            t.getPriority(),
            t.getStatus(),
            marker);
            taskListUI.add(row);
        }
        
        if (selectedIndex >= 0 && selectedIndex < taskListUI.getItemCount()) {
            taskListUI.select(selectedIndex);
        }
    }

    private String truncate(String text, int length) {
        if (text.length() <= length) return text;
        return text.substring(0, length - 3) + "...";
    }

    private void showTaskDialog(Task taskToEdit) {
        Dialog dialog = new Dialog(this, taskToEdit == null ? "Add Task" : "Edit Task", true);
        dialog.setLayout(new GridLayout(6, 2, 10, 10));
        dialog.setSize(450, 300);
        dialog.setLocationRelativeTo(this);

        dialog.add(new Label("  Title:"));
        TextField titleField = new TextField();
        dialog.add(titleField);

        dialog.add(new Label("  Description:"));
        TextArea descArea = new TextArea(3, 20);
        dialog.add(descArea);

        dialog.add(new Label("  Due Date (yyyy-MM-dd HH:mm):"));
        TextField dateField = new TextField("yyyy-MM-dd HH:mm");
        dialog.add(dateField);

        dialog.add(new Label("  Priority:"));
        Choice priorityChoice = new Choice();
        priorityChoice.add("High"); priorityChoice.add("Medium"); priorityChoice.add("Low");
        dialog.add(priorityChoice);

        dialog.add(new Label("  Status:"));
        Choice statusChoice = new Choice();
        statusChoice.add("Pending"); statusChoice.add("In-progress"); statusChoice.add("Done"); statusChoice.add("OVERDUE");
        dialog.add(statusChoice);

        if (taskToEdit != null) {
            titleField.setText(taskToEdit.getTitle());
            descArea.setText(taskToEdit.getDescription());
            dateField.setText(taskToEdit.getDueDate().format(dateFormatter));
            priorityChoice.select(taskToEdit.getPriority());
            statusChoice.select(taskToEdit.getStatus());
        }

        Button btnSave = new Button("Save");
        Button btnCancel = new Button("Cancel");

        btnSave.addActionListener(e -> {
            try {
                String title = titleField.getText().trim();
                String desc = descArea.getText().trim();
                LocalDateTime dueDate = LocalDateTime.parse(dateField.getText().trim(), dateFormatter);
                String priority = priorityChoice.getSelectedItem();
                String status = statusChoice.getSelectedItem();

                if (title.isEmpty()) throw new IllegalArgumentException("Title cannot be empty.");

                if (taskToEdit == null) {
                    manager.addTask(new Task(title, desc, dueDate, priority, status));
                } else {
                    taskToEdit.setTitle(title);
                    taskToEdit.setDescription(desc);
                    taskToEdit.setDueDate(dueDate);
                    taskToEdit.setPriority(priority);
                    taskToEdit.setStatus(status);
                    manager.notifyDataChanged();
                }
                
                manager.checkOverdueTasks(); 
                dialog.setVisible(false);
            } catch (DateTimeParseException ex) {
                showMessage("Invalid Date! Please use yyyy-MM-dd HH:mm");
            } catch (IllegalArgumentException ex) {
                showMessage(ex.getMessage());
            }
        });

        btnCancel.addActionListener(e -> dialog.setVisible(false));

        Panel bp = new Panel();
        bp.add(btnSave);
        bp.add(btnCancel);
        dialog.add(new Label("")); 
        dialog.add(bp);

        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) { dialog.setVisible(false); }
        });

        dialog.setVisible(true);
    }

    private void showMessage(String msg) {
        Dialog msgDialog = new Dialog(this, "Notification", true);
        msgDialog.setLayout(new FlowLayout());
        msgDialog.setSize(350, 100);
        msgDialog.setLocationRelativeTo(this);
        msgDialog.add(new Label(msg));
        Button ok = new Button("OK");
        ok.addActionListener(e -> msgDialog.setVisible(false));
        msgDialog.add(ok);
        msgDialog.setVisible(true);
    }
}