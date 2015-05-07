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
    private final Func1<String, Observable<Page>> wikiService;

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

    private String extractFullNamesFromRedirect(Page page) {
        String currentContent = page.getCurrentContent();
        return StringUtils.substringBetween(currentContent, "[[", "]]");
    }

    private String extractPersonInfoFromPage(Page page) {
        String currentContent = page.getCurrentContent();
        return Arrays.stream(currentContent.split("\\n")).filter(line -> line.contains("|birth")).collect(Collectors.joining(" "));
    }

    private Observable<Page> queryWiki(String titleString) {
        return Observable.from(user.queryContent(Arrays.asList(titleString)));
    }

    public static void main(String[] args) throws InterruptedException {
        new WikiExp().run();
    }
}