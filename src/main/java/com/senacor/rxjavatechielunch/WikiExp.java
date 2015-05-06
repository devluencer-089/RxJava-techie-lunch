package com.senacor.rxjavatechielunch;

import info.bliki.api.Page;
import info.bliki.api.User;
import org.apache.commons.lang3.StringUtils;
import rx.Observable;
import rx.util.async.Async;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WikiExp {
    private final User user;

    public WikiExp() {
        user = new User("", "", "http://en.wikipedia.org/w/api.php");
        user.login();
    }

    private void run() throws InterruptedException {
        List<String> listOfTitleStrings = Arrays.asList("Goethe", "Schiller");

        Observable<List<Page>> wikiResult = queryWiki(listOfTitleStrings);

        //TODO das kann man alles mit Observables machen
        wikiResult
                .map(this::extractFullNamesFromRedirect)
                .flatMap(fullNames -> queryWiki(fullNames))
                .map(this::extractPersonInfoFromPage)
                .subscribe(infos -> infos.forEach(System.out::println));

        Thread.sleep(5000);
    }

    //TODO ReturnType sollte Observbale sein
    private List<String> extractFullNamesFromRedirect(List<Page> pages) {
        return  pages.stream()
                .map(Page::getCurrentContent)
                .map(s -> StringUtils.substringBetween(s, "[[", "]]"))
                .collect(Collectors.toList());
    }
    //TODO ReturnType sollte Observbale sein
    private List<String> extractPersonInfoFromPage(List<Page> pages) {
        return  pages.stream()
                .map(Page::getCurrentContent)
                .map(page -> Arrays.stream(page.split("\\n")).filter(line -> line.contains("|birth")).collect(Collectors.joining(" ")))
                .collect(Collectors.toList());
    }
    //TODO ReturnType sollte Observbale<Page> sein
    private Observable<List<Page>> queryWiki(List<String> listOfTitleStrings) {
        return Async.fromCallable(() -> user.queryContent(listOfTitleStrings));
    }

    public static void main(String[] args) throws InterruptedException {
        new WikiExp().run();
    }
}