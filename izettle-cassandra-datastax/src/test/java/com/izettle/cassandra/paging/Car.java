package com.izettle.cassandra.paging;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;
import com.izettle.java.UUIDFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

@Table(name = "car", keyspace = "paging")
public class Car {

    @Column
    @PartitionKey
    private String owner;
    @Column
    @PartitionKey(1)
    private UUID id;
    @Column
    private String brand;
    @Column
    private int year;

    public static Car random(String owner) {
        final Car car = new Car();
        car.setOwner(owner);
        car.setId(UUIDFactory.createUUID1());
        car.setBrand(randomAlphabetic(5));
        car.setYear(Integer.parseInt("19" + randomNumeric(2)));
        return car;
    }

    public static List<Car> random(String owner, int number) {
        final List<Car> cars = new ArrayList<>();
        IntStream.range(0, number).forEach(value -> cars.add(Car.random(owner)));
        return cars;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(final String owner) {
        this.owner = owner;
    }

    public UUID getId() {
        return id;
    }

    public void setId(final UUID id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(final String brand) {
        this.brand = brand;
    }

    public int getYear() {
        return year;
    }

    public void setYear(final int year) {
        this.year = year;
    }

    @Override
    public boolean equals(final Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
