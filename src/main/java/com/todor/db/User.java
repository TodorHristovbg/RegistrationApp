package com.todor.db;

public  class User {
    private final int id;
    private final String first_name;
    private final String second_name;
    private final String password_hash;
    private final String email;

    public User(int id, String first_name, String second_name, String password_hash, String email){
        this.id = id;
        this.first_name = first_name;
        this.second_name = second_name;
        this.password_hash = password_hash;
        this.email = email;
    }
    
    public int getId() {
        return id;
    }

    public String getFirstName() {
        return first_name;
    }

    public String getSecondName() {
        return second_name;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return password_hash;
    }
}