package com.clara.ops.challenge.document_management_service_challenge.service;

import com.clara.ops.challenge.document_management_service_challenge.domain.entities.User;
import com.clara.ops.challenge.document_management_service_challenge.domain.repository.UserRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;

  @Autowired
  public UserServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public User validateAndCreateUser(String name) {
    Optional<User> userOpt = userRepository.findOneByName(name);
    if (userOpt.isPresent()) {
      return userOpt.get();
    }
    User user = new User();
    user.setName(name);
    return userRepository.save(user);
  }

  @Override
  public User getUser(Integer id) {
    return userRepository.findById(id).orElse(null);
  }
}
