package com.admin;

import java.util.UUID;

class AdminStub  {
    private String id;
    private String name;
    private String password;

    public AdminStub(){

    }
    public AdminStub(String name, String password){
        String id = UUID.randomUUID().toString();
        this.setId(id);
        this.name = name;
        this.password = password;
    }

    // getter
    public String getId(){
        return this.id;
    }
    public String getName(){
        return this.name;
    }
    public String getPassword(){
        return this.password;
    }

    // setter
    public void setId(String id){
        this.id = id;
    }
    public void setName(String name){
        this.name = name;
    }
    public void setPassword(String password){
        this.password = password;
    }

    public void test(){

    }

    // public static void main(String[] args) {
    //     AdminStub run = new AdminStub();
    //     run.test()
    // }
}
