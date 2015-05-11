package com.senacor.rxjavatechielunch;

import info.bliki.api.Page;
import info.bliki.api.User;
import org.apache.commons.lang3.StringUtils;
import rx.Observable;
import rx.Scheduler;
import rx.schedulers.Schedulers;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class WikiExp_WithWindowing_WithSchedulers {
    private final User user;

    public WikiExp_WithWindowing_WithSchedulers() {
        user = new User("", "", "http://en.wikipedia.org/w/api.php");
        user.login();
    }

    private void run() throws InterruptedException {
        Observable<String> listOfTitleStrings = Observable.from(Arrays.asList(
                "Goethe", "Schiller",
                "Goethe", "Schiller",
                "Goethe", "Schiller",
                "Goethe", "Schiller",
                "Goethe", "Schiller",
                "Goethe", "Schiller",
                "Goethe", "Schiller",
                "Goethe", "Schiller",
                "Goethe", "Schiller",
                "Goethe", "Schiller",
                "Goethe", "Schiller",
                "Goethe", "Schiller",
                "Goethe", "Schiller",
                "Goethe", "Schiller",
                "Goethe", "Schiller",
                "Goethe", "Schiller",
                "Goethe", "Schiller",
                "Goethe", "Schiller"
                ));

        Scheduler wikiScheduler = CustomerSchedulers.scheduler("wiki", 10);
        listOfTitleStrings
                .buffer(500, TimeUnit.MILLISECONDS, 5)
                .observeOn(wikiScheduler)
                .flatMap(names -> queryWiki(names))
                .observeOn(Schedulers.computation())
                .map(this::extractFullNamesFromRedirect)
                .buffer(500, TimeUnit.MILLISECONDS, 5)
                .observeOn(wikiScheduler)
                .flatMap(fullName -> queryWiki(fullName))
                .map(this::extractPersonInfoFromPage)
                .subscribeOn(Schedulers.computation())
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
        new WikiExp_WithWindowing_WithSchedulers().run();
    }
}