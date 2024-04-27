package com.kodilla.batchtask;

import org.springframework.batch.item.ItemProcessor;

import java.time.LocalDate;
import java.time.Period;

public class PersonProcessor implements ItemProcessor<Person, PersonProcessed> {

    @Override
    public PersonProcessed process(Person item) {
        return new PersonProcessed(item.getName(), item.getSurname(), Period.between(LocalDate.parse(item.getBirthday()), LocalDate.now()).getYears());
    }
}
