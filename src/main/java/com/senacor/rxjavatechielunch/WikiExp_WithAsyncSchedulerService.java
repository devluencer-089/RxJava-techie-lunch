package com.senacor.rxjavatechielunch;

import info.bliki.api.Page;
import info.bliki.api.User;
import org.apache.commons.lang3.StringUtils;
import rx.Observable;
import rx.schedulers.Schedulers;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by tkreylin on 08.05.2015.
 */
public class WikiExp_WithAsyncSchedulerService {
    private final User user;

    public WikiExp_WithAsyncSchedulerService() {
        user = new User("", "", "http://en.wikipedia.org/w/api.php");
        user.login();
    }

    private void run() throws InterruptedException {
        List<String> listOfTitleStrings = Arrays.asList("Goethe", "Schiller");

        Observable<List<Page>> wikiResult = queryWiki(listOfTitleStrings);

        wikiResult
                .map(this::extractFullNamesFromRedirect)
                .flatMap(fullNames -> queryWiki(fullNames))
                .map(this::extractPersonInfoFromPage)
                .subscribe(infos -> infos.forEach(System.out::println));

        Thread.sleep(5000);
    }

    private List<String> extractFullNamesFromRedirect(List<Page> pages) {
        return  pages.stream()
                .map(Page::getCurrentContent)
                .map(s -> StringUtils.substringBetween(s, "[[", "]]"))
                .collect(Collectors.toList());
    }

    private List<String> extractPersonInfoFromPage(List<Page> pages) {
        return  pages.stream()
                .map(Page::getCurrentContent)
                .map(page -> Stream.of(page.split("\\n")).filter(line -> line.contains("|birth")).collect(Collectors.joining(" ")))
                .collect(Collectors.toList());
    }

    private Observable<List<Page>> queryWiki(List<String> listOfTitleStrings) {
        return Observable.just(user.queryContent(listOfTitleStrings)).subscribeOn(Schedulers.io());
    }

    public static void main(String[] args) throws InterruptedException {
        new WikiExp_WithAsyncSchedulerService().run();
    }
}
