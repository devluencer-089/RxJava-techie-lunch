package com.senacor.rxjavatechielunch;

import info.bliki.api.Page;
import info.bliki.api.User;
import org.apache.commons.lang3.StringUtils;
import rx.Observable;
import rx.functions.Func1;

import java.util.Arrays;
import java.util.stream.Collectors;

public class WikiExp {
    private final User user;
    private final Func1<String, Observable<String>> wikiService;

    public WikiExp() {
        user = new User("", "", "http://en.wikipedia.org/w/api.php");
        user.login();


        wikiService = this::queryWikiMock;
    }

    private void run() {
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

    private Observable<String> queryWikiMock(String titleString) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        if (titleString.equals("Goethe")) return Observable.just("REDIRECT [[Johann Wolfgang Goethe]]");
        if (titleString.equals("Schiller")) return Observable.just("REDIRECT [[Johann Christoph Friedrich Schiller]]");

        if (titleString.equals("Johann Wolfgang Goethe")) return Observable.just(
                "|birth_date = {{birth date|1749|8|28|df=y}}\\n" +
                "|birth_place = [[Frankfurt-am-Main]], [[Holy Roman Empire]]\\n");
        if (titleString.equals("Johann Christoph Friedrich Schiller")) return Observable.just(
                "|birth_name   = Johann Christoph Friedrich Schiller\\n" +
                "|birth_date   = {{birth date|1759|11|10|df=y}}\\n" +
                "|birth_place  = [[Marbach am Neckar]], [[Duchy of Württemberg|Württemberg]], Germany");

        return Observable.never();
    }

    public static void main(String[] args) throws InterruptedException {
        StopWatch.measureAndPrintTime(new WikiExp()::run);
    }
}