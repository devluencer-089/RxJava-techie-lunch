package com.senacor.rxjavatechielunch;

import info.bliki.api.Page;
import info.bliki.api.User;
import org.apache.commons.lang3.StringUtils;
import rx.Observable;
import rx.functions.Func1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class WikiExp {
    private final User user;
    private final Func1<String, Observable<String>> wikiService;

    public WikiExp() {
        user = new User("", "", "http://en.wikipedia.org/w/api.php");
        user.login();

        wikiService = this::queryWiki;
    }

    private void run() throws InterruptedException {
        Observable<String> listOfTitleStrings = Observable.just("Goethe", "Schiller");

        listOfTitleStrings
                .flatMap(wikiService)
                .map(this::extractFullNamesFromRedirect)
                .flatMap(wikiService)
                .map(this::extractPersonInfoFromPage)
                .subscribe(System.out::println);

    }

    private String extractFullNamesFromRedirect(String page) {
        return StringUtils.substringBetween(page, "[[", "]]");
    }

    private String extractPersonInfoFromPage(String page) {
        return Arrays.stream(page.split("\\n")).filter(line -> line.contains("|birth")).collect(Collectors.joining(" "));
    }

    private Observable<String> queryWiki(String titleString) {
        return Observable.from(user.queryContent(Arrays.asList(titleString))).map(Page::getCurrentContent);
    }

    public static void main(String[] args) throws InterruptedException {
        new WikiExp().run();
    }
}