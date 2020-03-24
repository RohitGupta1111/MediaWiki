package com.example.mediawiki;

public class Constants {
    //base url for WikiMedia API request
    public static final String baseUrl = "https://en.wikipedia.org/w/api.php?action=query&format=json&prop=pageimages|pageterms&generator=prefixsearch&redirects=1&formatversion=2&piprop=thumbnail&pithumbsize=50&pilimit=10&wbptterms=description&gpslimit=20&gpssearch=";
    //base Url for wiki page redirect
    public static final String wikiUrl = "https://en.wikipedia.org/wiki/";
    //the text for which query is sent to mediawiki API
    private static String queryText;

    public static void setQueryText(String text) {
        queryText = text;
    }

    public static String getQueryText() {
        return queryText;
    }

}
