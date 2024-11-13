package org.chenzc.codeflow.service;


import org.chenzc.codeflow.domain.BasicResult;
import org.chenzc.codeflow.domain.User;

public interface UserService {
    BasicResult login(User user);
}
