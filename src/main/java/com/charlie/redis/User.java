package com.charlie.redis;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.annotation.Id;

import java.io.Serializable;

@Cacheable(value = "User")
@Data
@AllArgsConstructor
public class User implements Serializable {
    @Id
    private long id;

    private String firstName;
    private String lastName;
    private String email;
}
