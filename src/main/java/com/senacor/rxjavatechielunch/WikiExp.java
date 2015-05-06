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
        Observable<String> listOfTitleStrings = Observable.just("Goethe", "Schiller");

        Observable<Observable<Page>> wikiResult = queryWiki(listOfTitleStrings);

        wikiResult
                .map(this::extractFullNamesFromRedirect)
                .flatMap(fullNames -> queryWiki(fullNames))
                .map(this::extractPersonInfoFromPage)
                .subscribe(infos -> infos.forEach(System.out::println));

        Thread.sleep(5000);
    }

    private Observable<String> extractFullNamesFromRedirect(Observable<Page> pages) {
        return  pages
                .map(Page::getCurrentContent)
                .map(s -> StringUtils.substringBetween(s, "[[", "]]"));
    }
    private Observable<String> extractPersonInfoFromPage(Observable<Page> pages) {
        return  pages
                .map(Page::getCurrentContent)
                .map(page -> Arrays.stream(page.split("\\n")).filter(line -> line.contains("|birth")).collect(Collectors.joining(" ")));
    }

    private Observable<Observable<Page>> queryWiki(Observable<String> titleStrings) {
        return Async.fromCallable(() -> Observable.from(user.queryContent(titleStrings.toList().toBlocking().single())));
    }

    public static void main(String[] args) throws InterruptedException {
        new WikiExp().run();
    }
}