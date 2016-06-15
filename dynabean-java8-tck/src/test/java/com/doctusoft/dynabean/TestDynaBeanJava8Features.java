package com.doctusoft.dynabean;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.MonthDay;
import java.time.Year;
import java.util.*;

import static org.junit.Assert.*;

public class TestDynaBeanJava8Features {

    private DynaBeanFactory factory;

    @Before
    public void setup() {
        factory = new LocalDynaBeanFactory();
    }

    @Test
    public void defaultMethodImplementation() {
        BeanWithDefaultMethods bean = factory.create(BeanWithDefaultMethods.class);
        bean.setName("John Doe");
        assertEquals(Optional.empty(), bean.getAgeOptional());
        assertFalse(bean.isAdult());
        bean.setAge(18);
        assertTrue(bean.isAdult());
    }

    public interface BeanWithDefaultMethods {

        String getName();

        void setName(String name);

        Integer getAge();

        void setAge(Integer age);

        default Optional<Integer> getAgeOptional() {
            return Optional.ofNullable(getAge());
        }

        default boolean isAdult() {
            return getAgeOptional().map(age -> age.intValue() >= 18).orElse(false);
        }

    }

    @Test
    public void defaultMethodWithParameters() {
        OverriddenDefaultMethods bean = factory.create(OverriddenDefaultMethods.class);
        LocalDate today = LocalDate.of(2016, 6, 15);
        MonthDay birthday = MonthDay.of(10, 20);
        assertNull(bean.calculateYearOfBirth(today, birthday));
        bean.setAge(20);
        assertEquals(Year.of(1995), bean.calculateYearOfBirth(today, birthday));
    }

    @Test
    public void overriddenDefaultMethod() {
        BeanWithDefaultMethods bean = factory.create(OverriddenDefaultMethods.class);
        bean.setAge(17);
        assertTrue(bean.isAdult());
        bean.setAge(71);
        assertFalse(bean.isAdult());
    }

    public interface OverriddenDefaultMethods extends BeanWithDefaultMethods {

        default boolean isAdult() {
            if (getAge() == null) return false;
            int age = getAge().intValue();
            return age > 16 && age <= 65;
        }

        default Year calculateYearOfBirth(LocalDate today, MonthDay birthday) {
            if (getAge() == null) return null;
            int thisYear = today.getYear();
            Year yearOfBirth = Year.of(thisYear - getAge().intValue());
            LocalDate birthdayThisYear = birthday.atYear(thisYear);
            if (birthdayThisYear.isAfter(today)) return yearOfBirth.minusYears(1L);
            return yearOfBirth;
        }

    }

}
