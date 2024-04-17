package com.example.softwarepatternsca4.models;

public class Review {

    String customComment;
    Float customRating;
    String customEmail;

    public Review(String customComment, Float customRating, String customEmail) {
        this.customComment = customComment;
        this.customRating = customRating;
        this.customEmail = customEmail;
    }

    public Review(){
        // Required empty public constructor
    }

    @Override
    public String toString() {
        return "CustomReview{" +
                "customComment='" + customComment + '\'' +
                ", customRating=" + customRating +
                ", customEmail='" + customEmail + '\'' +
                '}';
    }

    public String getCustomComment() {
        return customComment;
    }

    public void setCustomComment(String customComment) {
        this.customComment = customComment;
    }

    public Float getCustomRating() {
        return customRating;
    }

    public void setCustomRating(Float customRating) {
        this.customRating = customRating;
    }

    public String getCustomEmail() {
        return customEmail;
    }

    public void setCustomEmail(String customEmail) {
        this.customEmail = customEmail;
    }
}

