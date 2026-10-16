package sg.edu.ntu.taskflowapi.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Repository;
import sg.edu.ntu.taskflowapi.model.Task;

@Repository
public class TaskRepository {
    private final Map<Long, Task> tasks = new HashMap<>();
    private long nextId = 1;

    public synchronized List<Task> findAll() {
        return new ArrayList<>(tasks.values());
    }

    public synchronized Optional<Task> findById(Long id) {
        return Optional.ofNullable(tasks.get(id));
    }

    public synchronized Task save(Task task) {
        if (task.getId() == null) {
            task.setId(nextId++);
        }
        tasks.put(task.getId(), task);
        return task;
    }

    public synchronized void deleteById(Long id) {
        tasks.remove(id);
    }
}
