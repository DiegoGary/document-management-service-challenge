package com.clara.ops.challenge.document_management_service_challenge.service;

import com.clara.ops.challenge.document_management_service_challenge.domain.entities.User;

public interface UserService {

    User validateAndCreateUser(String name);

    User getUser(Integer id);
}
