package com.example.mediawiki;

public class SearchModel {
    String searchImageUrl;
    String title;
    String description;

    public SearchModel () {

    }

    public SearchModel (String searchImageUrl, String title, String description) {
        this.description = description;
        this.title = title;
        this.searchImageUrl = searchImageUrl;
    }

    public String getDescription() {
        return description;
    }

    public String getSearchImageUrl() {
        return searchImageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setSearchImageUrl(String searchImageUrl) {
        this.searchImageUrl = searchImageUrl;
    }

    public void setTitle(String title) {
        this.title = title;
    }


}
