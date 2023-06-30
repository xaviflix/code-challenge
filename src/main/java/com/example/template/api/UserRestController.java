package com.example.template.api;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.template.models.User;
import com.example.template.models.UserRepository;

@RestController
@RequestMapping(path="/restdbapi")
public class UserRestController {
    @Autowired
    private UserRepository userRepository;

    @PostMapping(path="/add")
    public UserApiModel addNewUser (@RequestParam String name
            , @RequestParam String email) {
        // curl -vS -X POST "http://localhost:8080/restdbapi/add" -d name="XXX" -d email="XXX"
        User dbUser = new User();
        dbUser.setName(name);
        dbUser.setEmail(email);
        userRepository.save(dbUser);
        return new UserApiModel(dbUser.getId(), dbUser.getName(), dbUser.getEmail());
    }

    @GetMapping(path="/list")
    public Iterable<UserApiModel> getAllUsers() {
        Iterable<User> dbUserList = userRepository.findAll();
        List<UserApiModel> apiUserList = new ArrayList();
        for (User dbUser : dbUserList) {
            apiUserList.add(new UserApiModel(dbUser.getId(), dbUser.getName(), dbUser.getEmail()));
        }
        return apiUserList;
    }
}