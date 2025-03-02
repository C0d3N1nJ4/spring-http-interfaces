package com.naiomi.http.interfaces.model;

public record Address(
        String street,
        String suite,
        String city,
        String zipcode,
        Geo geo
) {
}
