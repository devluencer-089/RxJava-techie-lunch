package com.senacor.rxjavatechielunch;

import rx.Observable;
import rx.Subscription;

import java.util.concurrent.TimeUnit;

public class Exp {

    public static void main(String[] args) throws InterruptedException {
        Observable<Long> ticker = Observable.timer(0, 500, TimeUnit.MILLISECONDS);

        Observable<String> peoplesName = Observable.just("hans", "heidi");
        Observable<Integer> peoplesAge = Observable.just(30, 27);

        Observable<Person> people = Observable.zip(peoplesName, peoplesAge, Person::new);

        Observable<Person> tickingPeople = Observable.zip(people, ticker, (person, tick) -> person);

        Subscription subscription = tickingPeople.subscribe(
                System.out::println,
                System.out::println,
                () -> System.out.println("on Complete!"));

        System.out.println(subscription.isUnsubscribed());

        Thread.sleep(5000);
        System.out.println(subscription.isUnsubscribed());
    }

    private static class Person {
        public final String name;
        public final Integer age;

        public Person(String name, Integer age) {
            this.name = name;
            this.age = age;
        }

        @Override
        public String toString() {
            return name + " " + age;
        }
    }
}
