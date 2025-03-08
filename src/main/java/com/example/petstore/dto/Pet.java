package com.example.petstore.dto;

import java.util.List;


public class Pet {

    private Category category;
    private Long id;
    private String name;
    private List<String> photoUrls;
    private String status;
    private List<Tag> tags;

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
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

    public List<String> getPhotoUrls() {
        return photoUrls;
    }

    public void setPhotoUrls(List<String> photoUrls) {
        this.photoUrls = photoUrls;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public Pet(Category category, Long id, String name, List<String> photoUrls, String status, List<Tag> tags) {
        this.category = category;
        this.id = id;
        this.name = name;
        this.photoUrls = photoUrls;
        this.status = status;
        this.tags = tags;
    }

    public Pet() {
    }
}