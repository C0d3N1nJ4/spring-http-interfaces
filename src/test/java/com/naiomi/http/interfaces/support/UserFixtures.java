package com.naiomi.http.interfaces.support;

import com.naiomi.http.interfaces.model.Address;
import com.naiomi.http.interfaces.model.Company;
import com.naiomi.http.interfaces.model.User;

public final class UserFixtures {

    private UserFixtures() {
    }

    public static User user(Integer id, String name) {
        String normalized = name.toLowerCase();
        return new User(
                id,
                name,
                normalized,
                normalized + "@example.com",
                new Address("Street 123", "Suite 1", "CityX", "12345", null),
                "123-456-7890",
                "www." + normalized + ".com",
                new Company(name + " Inc.", "Innovating " + name, "Tech")
        );
    }
}