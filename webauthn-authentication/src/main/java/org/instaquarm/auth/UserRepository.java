package org.instaquarm.auth;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@ApplicationScoped
public class UserRepository {

    // DEMO ONLY - Keep everything in memory
    private final List<User> users = new CopyOnWriteArrayList<>();


    public Uni<User> findByUserName(String username) {
        var user = users.stream().filter(u -> u.username.equals(username)).findFirst().orElse(null);
        return Uni.createFrom().item(user);
    }


    public void persist(User newUser) {
        users.add(newUser);
    }
}
