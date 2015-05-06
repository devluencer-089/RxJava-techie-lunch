package com.senacor.rxjavatechielunch;

import info.bliki.api.Page;
import info.bliki.api.User;
import org.apache.commons.lang3.StringUtils;
import rx.Observable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class WikiExp {
    private final User user;

    public WikiExp() {
        user = new User("", "", "http://en.wikipedia.org/w/api.php");
        user.login();
    }

    private void run() throws InterruptedException {
        Observable<String> listOfTitleStrings = Observable.just("Goethe", "Schiller");

        listOfTitleStrings
                .flatMap(name -> queryWiki(name))
                .map(this::extractFullNamesFromRedirect)
                .flatMap(fullName -> queryWiki(fullName))
                .map(this::extractPersonInfoFromPage)
                .subscribe(System.out::println);

        Thread.sleep(5000);
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
        List<String> titles = new ArrayList<>();
        titles.add(titleString);
        return Observable.from(user.queryContent(titles));
    }

    public static void main(String[] args) throws InterruptedException {
        new WikiExp().run();
    }
}