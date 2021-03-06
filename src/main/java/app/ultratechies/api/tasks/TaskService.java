package app.ultratechies.api.tasks;

import app.ultratechies.api.AppUsers.UserDTO.UserDto;
import app.ultratechies.api.AppUsers.UserService;
import app.ultratechies.api.exceptions.BadRequestException;
import app.ultratechies.api.exceptions.TaskNotfoundException;
import app.ultratechies.api.utils.DateTimeUtil;
import lombok.*;
import org.springframework.stereotype.Service;


import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Builder
public class TaskService {

    final TaskRepository taskRepository;
    final UserService userService;

    Task save(Task task) {
        return taskRepository.save(task);
    }

    public Task convertDTOtoTask(Long userId,TaskDTO dto){

        checkForNull(dto);

        return  Task.builder()
                .title(dto.getTitle())
                .description(dto.description)
                .dueDate(DateTimeUtil.convertStringToInstant(dto.getDueDate()))
                .createdTime(DateTimeUtil.convertStringToInstant(dto.getCreatedTime()))
                .appUser(userService.getAppUser(userId))
                .status(TaskStatus.created)
                .build();
    }

    public Task createTaskAndSave(Long userId,TaskDTO dto){
        Task newTask = convertDTOtoTask(userId,dto);
        return save(newTask);
    }

    public void checkForNull(TaskDTO dto) {

        if (dto == null) {
            throw new BadRequestException(
                    "request body is null"
            );
        }
        if (dto.getTitle() == null) {
            throw new BadRequestException(
                    "Title cannot be null"
            );
        }
        if (dto.getDescription() == null) {
            throw new BadRequestException(
                    "Description cannot be null"
            );
        }
        if (dto.getCreatedTime()== null) {
            throw new BadRequestException(
                    "created time cannot be null"
            );
        }
        if (dto.getDueDate() == null) {
            throw new BadRequestException(
                    "Due date cannot be null"
            );
        }

    }

    public Task updateTask(Long id, TaskDTO dto){
        Task task = findByIdOrThrow(id);
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setDueDate(DateTimeUtil.convertStringToInstant(dto.getDueDate()));
        task.setCreatedTime(DateTimeUtil.convertStringToInstant(dto.getCreatedTime()));
        task.setStatus(TaskStatus.valueOf(dto.getStatus()));

        return task;
    }

    public Task updateTaskAndSave(Long id, TaskDTO dto){
        return save(updateTask(id,dto));
    }

    public List<TaskDTO> getTaskByUserId(Long id) {
        Optional<UserDto> user = userService.getUsersById(id);
        if(user.isPresent()){
        return taskRepository
                .findAllByAppUser_Id(id)
                .stream()
                .map(this::convertTaskToTaskDTO)
                .collect(Collectors.toList());}
        else return Collections.emptyList();
    }


    public TaskDTO convertTaskToTaskDTO(Task task){
        return TaskDTO.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .dueDate(DateTimeUtil.convertInstantToString(task.getDueDate()))
                .status(task.getStatus().toString())
                .createdTime(DateTimeUtil.convertInstantToString(task.getCreatedTime()))
                .build();
    }

    public Task findByIdOrThrow(Long taskId){
        return taskRepository.findById(taskId).orElseThrow(() -> new TaskNotfoundException("Task does not exist"));
    }

    public void deleteTask(Long taskId){
        findByIdOrThrow(taskId);
        taskRepository.deleteById(taskId);
    }

    public void  deleteTaskByUserId(Long userId){
        List<TaskDTO> tasksList = getTaskByUserId(userId);
        for (TaskDTO i : tasksList) {
            Long task_id=i.getId();
            taskRepository.deleteById(task_id);
        }
    }
}