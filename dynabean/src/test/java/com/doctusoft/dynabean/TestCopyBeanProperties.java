package com.doctusoft.dynabean;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;

public class TestCopyBeanProperties {

    private DynaBeanFactory factory;

    @Before
    public void setup() {
        factory = new LocalDynaBeanFactory();
    }

    @Test
    public void simpleAttributesCopied() {
        AddressDetails address = factory.create(AddressDetails.class);
        address.setCity("Budapest");
        address.setPostalCode(1143);
        AddressDetails addressCopy = factory.copyProperties(AddressDetails.class, address);
        assertNotSame(address, addressCopy);
        assertEquals(address.getCity(), addressCopy.getCity());
        assertEquals(address.getPostalCode(), addressCopy.getPostalCode());
        assertEquals(address.getAddress(), addressCopy.getAddress());
        assertEquals(address, addressCopy);
        address.setAddress("Hungária körút");
        assertEquals("Hungária körút", address.getAddress());
        assertNull(addressCopy.getAddress());
        assertNotEquals(address, addressCopy);

        Bean bean = factory.create(Bean.class);
        bean.setName("John");
        bean.setAge(18);
        Bean copy = factory.copyProperties(Bean.class, bean);
        assertNotSame(bean, copy);
        assertEquals(bean, copy);
        assertSame(bean.getName(), copy.getName());
        copy.setAge(19);
        assertNotEquals(bean, copy);
        bean.setAge(19);
        assertEquals(bean, copy);
    }

    @Test
    public void collectionContentsAreCopiedForListAndSet() {
        Set<String> placesLived = new HashSet<>();
        placesLived.add("Budapest");
        placesLived.add("Dublin");

        Bean bean = factory.create(Bean.class);
        bean.setName("John");
        bean.setPlacesLived(placesLived);

        Bean copy = factory.copyProperties(Bean.class, bean);
        assertNotSame(bean, copy);
        assertEquals(bean, copy);
        assertNotSame(bean.getPlacesLived(), copy.getPlacesLived());

        bean.getPlacesLived().add("New York");
        assertNotEquals(bean, copy);
        assertThat(bean.getPlacesLived(), hasItem("New York"));
        assertThat(copy.getPlacesLived(), not(hasItem("New York")));

        copy.getPlacesLived().remove("Budapest");
        assertThat(bean.getPlacesLived(), Matchers.<String>iterableWithSize(3));
        assertThat(copy.getPlacesLived(), Matchers.<String>iterableWithSize(1));

        copy.getPlacesLived().add("New York");
        assertThat(copy.getPlacesLived(), containsInAnyOrder("Dublin", "New York"));
        assertNotEquals(bean, copy);

        bean.getPlacesLived().remove("Budapest");
        assertEquals(copy, bean);
    }

    @Test
    public void cloneableValuesAreCloned() {
        Bean bean = factory.create(Bean.class);
        bean.setName("John");
        bean.setDateOfBirth(new GregorianCalendar(1999, 0, 31));

        Bean copy = factory.copyProperties(Bean.class, bean);
        assertNotSame(bean, copy);
        assertEquals(bean, copy);
        assertNotSame(bean.getDateOfBirth(), copy.getDateOfBirth());

        bean.getDateOfBirth().set(2001, 8, 11);
        assertNotEquals(bean, copy);
        assertEquals(new GregorianCalendar(2001, 8, 11), bean.getDateOfBirth());
        assertEquals(new GregorianCalendar(1999, 0, 31), copy.getDateOfBirth());
    }

    @Test
    public void dynabeanInstancesAreCloned() {
        AddressDetails address = factory.create(AddressDetails.class);
        address.setCity("Budapest");
        address.setPostalCode(1143);
        Bean bean = factory.create(Bean.class);
        bean.setName("John");
        bean.setAge(18);
        bean.setMainAddress(address);

        Bean copy = factory.copyProperties(Bean.class, bean);
        assertEquals(bean, copy);
        assertEquals(bean.getMainAddress(), copy.getMainAddress());
        assertNotSame(bean.getMainAddress(), copy.getMainAddress());
        bean.getMainAddress().setPostalCode(1111);
        assertNotEquals(bean, copy);
        assertNotEquals(bean.getMainAddress(), copy.getMainAddress());
        copy.getMainAddress().setPostalCode(1111);
        assertEquals(bean, copy);

        copy = (Bean) ((DynaBean) bean).clone();
        assertEquals(bean, copy);
        assertNotSame(bean, copy);
        assertEquals(bean.getMainAddress(), copy.getMainAddress());
        assertNotSame(bean.getMainAddress(), copy.getMainAddress());
    }

    public interface Bean {

        String getName();

        void setName(String name);

        int getAge();

        void setAge(int age);

        GregorianCalendar getDateOfBirth();

        void setDateOfBirth(GregorianCalendar dateOfBirth);

        AddressDetails getMainAddress();

        void setMainAddress(AddressDetails mainAddress);

        Set<String> getPlacesLived();

        void setPlacesLived(Set<String> placesLived);

        List<AddressDetails> getNotificationAddresses();

        void setNotificationAddresses(List<AddressDetails> notificationAddresses);

    }

    public interface AddressDetails {

        int getPostalCode();

        void setPostalCode(int postalCode);

        String getCity();

        void setCity(String city);

        String getAddress();

        void setAddress(String address);

    }

    public static class AddressDetailsVO implements AddressDetails {

        private int postalCode;
        private String city;
        private String address;

        public int getPostalCode() { return postalCode; }

        public void setPostalCode(int postalCode) { this.postalCode = postalCode; }

        public String getCity() { return city; }

        public void setCity(String city) { this.city = city; }

        public String getAddress() { return address; }

        public void setAddress(String address) { this.address = address; }
    }

}
