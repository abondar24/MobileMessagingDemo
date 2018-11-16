package org.abondar.experimental.mobilemessaging.model;

public class TestModel {

    private Long id;
    private String name;

    public TestModel(){}

    public TestModel(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return  id +":" + name ;
    }
}
