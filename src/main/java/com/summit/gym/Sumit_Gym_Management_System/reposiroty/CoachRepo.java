package com.summit.gym.Sumit_Gym_Management_System.reposiroty;

import com.summit.gym.Sumit_Gym_Management_System.model.Coach;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoachRepo extends JpaRepository<Coach,Long> {

}
