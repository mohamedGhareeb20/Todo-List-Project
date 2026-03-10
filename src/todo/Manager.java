package todo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Manager {
    private final List<Task> tasks; 
    private UpdateListener listener;

    public Manager() {
        this.tasks = new ArrayList<>();
        startReminderTimer();
    }

    public void setUpdateListener(UpdateListener listener) {
        this.listener = listener;
    }

    public void addTask(Task task) {
        tasks.add(task);
        notifyDataChanged();
    }

    public void removeTask(int index) {
        if (index >= 0 && index < tasks.size()) {
            tasks.remove(index);
            notifyDataChanged();
        }
    }

    public Task getTask(int index) {
        if (index >= 0 && index < tasks.size()) {
            return tasks.get(index);
        }
        return null;
    }

    public List<Task> getAllTasks() {
        return tasks;
    }

    private void startReminderTimer() {
        Timer timer = new Timer(true); 
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkOverdueTasks();
            }
        }, 0, 60000); 
    }

    public void checkOverdueTasks() {
        boolean newlyOverdue = false;
        LocalDateTime now = LocalDateTime.now();

        for (Task t : tasks) {
            if (!"Done".equals(t.getStatus()) && !"OVERDUE".equals(t.getStatus())) {
                if (now.isAfter(t.getDueDate())) {
                    t.setStatus("OVERDUE");
                    newlyOverdue = true;
                }
            }
        }

        if (newlyOverdue && listener != null) {
            listener.onTaskOverdue();
        }
    }

    public void notifyDataChanged() {
        if (listener != null) {
            listener.onDataChanged();
        }
    }
}