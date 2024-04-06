package com.charlie.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("api/v1/user")
public class UserController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ListRepository listRepository;

    @Autowired
    ObjectMapper objectMapper;

    @PostMapping
    public User saveUser(@RequestBody User user) {
        userRepository.save(user);
        return userRepository.findById(user.getId());
    }

    @PostMapping("/list")
    public Map saveList(@RequestBody HashMap map) {
        listRepository.save(1, map);
        return listRepository.findById(1L);
    }

    @PostMapping("/{usersToCreate}")
    public int create1000Users(@PathVariable int usersToCreate) {
        Set<User> users = new HashSet<>(usersToCreate);
        for (int i = 0; i < usersToCreate; i++) {
            users.add(new User(Integer.toUnsignedLong(i), "chuck" + i, "day" + i, "cuck.day@" + i + ".com"));
        }
        userRepository.saveAll(users);
        return usersToCreate;
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        return userRepository.findById(id);
    }

    @GetMapping("/list/{id}")
    public Map getList(@PathVariable Long id) {
        return listRepository.findById(id);
    }

    @GetMapping("/map/{id}")
    public User mapToUser(@PathVariable Long id) throws JsonProcessingException {
        Object resp = listRepository.findById(id).get(id.toString());
        String json = objectMapper.writeValueAsString(resp);

        return objectMapper.readValue(json, User.class);
    }

    @DeleteMapping("/{id}")
    public Boolean deleteUser(@PathVariable Long id) {
        return userRepository.evict(id);
    }

    @DeleteMapping("/all")
    @CacheEvict(value = "User", allEntries = true)
    public Boolean deleteAll() {
        return true;
    }

    @PatchMapping
    public User patchUser(@RequestBody User user) {
        userRepository.save(user);
        return userRepository.findById(user.getId());
    }

    @PostMapping("/lock")
    public String lock() {
        User temp = new User(9999, "first", "last", "email");
        userRepository.lock(temp);
        return "Locked";
    }
}
