package Proiect1.services;

import Proiect1.domain.Goal;
import Proiect1.domain.User;
import Proiect1.dtos.GoalDTO;
import Proiect1.exceptions.ItemNotFound;
import Proiect1.repositories.GoalRepository;
import Proiect1.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoalServiceImpl implements GoalService{

    private static final Logger logger = LoggerFactory.getLogger(GoalServiceImpl.class);

    private final UserRepository userRepository;
    private final GoalRepository goalRepository;

    GoalServiceImpl(UserRepository userRepository, GoalRepository goalRepository){
        this.userRepository = userRepository;
        this.goalRepository = goalRepository;
    }

    @Override
    public GoalDTO createGoal(Long userId, GoalDTO goalDTO) {

        logger.debug("Creating goal for userId={} with name={}", userId, goalDTO.getGoalName());

        User user = userRepository.findById(userId)
                .orElseThrow(()  -> {
                    logger.error("User not found with id={}", userId);
                    return new ItemNotFound("User");
                });

        Goal goal = new Goal();
        goal.setGoalName(goalDTO.getGoalName());
        goal.setTargetAmount(goalDTO.getTargetAmount());
        goal.setSavedAmount(goalDTO.getSavedAmount());
        goal.setDeadline(goalDTO.getDeadline());
        goal.setUser(user);

        goalRepository.save(goal);
        logger.info("Goal created with id={}", goal.getId());
        return convertToDTO(goal);
    }

    @Override
    public List<GoalDTO> getUserGoals(Long userId) {
        logger.debug("Fetching goals for userId={}", userId);
        return goalRepository.findByUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private GoalDTO convertToDTO(Goal goal) {
        GoalDTO dto = new GoalDTO();
        dto.setId(goal.getId());
        dto.setGoalName(goal.getGoalName());
        dto.setTargetAmount(goal.getTargetAmount());
        dto.setSavedAmount(goal.getSavedAmount());
        dto.setDeadline(goal.getDeadline());
        dto.setUserId(goal.getUser().getId());
        return dto;
    }

    @Override
    public void deleteGoal(Long goalId) {
        logger.warn("Attempting to delete goal with id={}", goalId);
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> {
                    logger.error("Goal not found with id={}", goalId);
                    return new ItemNotFound("Goal");
                });
        goalRepository.delete(goal);
    }

}
