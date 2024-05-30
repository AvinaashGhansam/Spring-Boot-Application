package com.ag.dto;

public record CustomerRegistrationRequest (
    String name,
    String email,
    Integer age
) {}
