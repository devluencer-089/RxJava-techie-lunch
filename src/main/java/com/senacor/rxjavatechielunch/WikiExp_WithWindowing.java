package com.senacor.rxjavatechielunch;

import info.bliki.api.Page;
import info.bliki.api.User;
import org.apache.commons.lang3.StringUtils;
import rx.Observable;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class WikiExp_WithWindowing {
    private final User user;

    public WikiExp_WithWindowing() {
        user = new User("", "", "http://en.wikipedia.org/w/api.php");
        user.login();
    }

    private void run() throws InterruptedException {
        Observable<String> listOfTitleStrings = Observable.just("Goethe", "Schiller");

        listOfTitleStrings
                .buffer(500, TimeUnit.MILLISECONDS, 10)
                .flatMap(names -> queryWiki(names))
                .map(this::extractFullNamesFromRedirect)
                .buffer(500, TimeUnit.MILLISECONDS, 10)
                .flatMap(fullName -> queryWiki(fullName))
                .map(this::extractPersonInfoFromPage)
                .subscribe(System.out::println);

        Thread.sleep(5000);
    }

    private String extractFullNamesFromRedirect(Page page) {
        ThreadTracer.printThreadName();
        String currentContent = page.getCurrentContent();
        return StringUtils.substringBetween(currentContent, "[[", "]]");
    }

    private String extractPersonInfoFromPage(Page page) {
        ThreadTracer.printThreadName();
        String currentContent = page.getCurrentContent();
        return Arrays.stream(currentContent.split("\\n")).filter(line -> line.contains("|birth")).collect(Collectors.joining(" "));
    }

    private Observable<Page> queryWiki(List<String> titles) {
        ThreadTracer.printThreadName();
        List<Page> pages = user.queryContent(titles);
        return Observable.from(pages);
    }

    public static void main(String[] args) throws InterruptedException {
        new WikiExp_WithWindowing().run();
    }
}