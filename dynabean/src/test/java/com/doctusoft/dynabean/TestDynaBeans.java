package com.doctusoft.dynabean;

import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class TestDynaBeans {
    
    private DynaBeanFactory factory;
    
    @Before
    public void setup() {
        factory = new LocalDynaBeanFactory();
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void cannotCreateForClasses() {
        factory.create(SimpleClass.class);
    }
    
    public static class SimpleClass {}
    
    @Test
    public void smokeTestForSimpleBean() {
        SimpleBean bean = factory.create(SimpleBean.class);
        assertNotNull(bean);
        assertSimpleBean(bean);
    }
    
    public interface SimpleBean {
        
        String getStr();
        
        void setStr(String str);
        
        Long getValue();
        
        void setValue(Long value);
        
    }
    
    private void assertSimpleBean(SimpleBean bean) {
        assertNull(bean.getStr());
        assertNull(bean.getValue());
        
        Random rnd = new Random();
        for (int i = 0; i < 5; ++i) {
            
            String str = rnd.nextBoolean()
                ? "some string value"
                : null;
            Long val = rnd.nextBoolean()
                ? rnd.nextLong()
                : null;
            
            bean.setStr(str);
            assertEquals(str, bean.getStr());
            
            bean.setValue(val);
            assertEquals(val, bean.getValue());
            assertEquals(str, bean.getStr());
        }
    }
    
    @Test
    public void beanCanHaveExtraMethods() {
        
        BeanWithExtraMethods bean = factory.create(BeanWithExtraMethods.class);
        String str1 = "some string value";
        
        assertExtraMethods(bean, null);
        
        bean.setStr(str1);
        assertExtraMethods(bean, str1);
    }
    
    public interface BeanWithExtraMethods {
        
        String getStr();
        
        void setStr(String str);
        
        void setStr(String str, int count);
        
        String getStr(int i);
        
        Object doSomething();
        
        Object doSomethingWithArguments(Object arg1, int arg2);
        
    }
    
    private void assertExtraMethods(BeanWithExtraMethods bean, Object expected) {
        int i = 5;
        
        bean.getStr(i);
        assertEquals(expected, bean.getStr());
        
        bean.setStr("another str", 15);
        assertEquals(expected, bean.getStr());
        assertNull(bean.getStr(i));
        
        assertEquals(expected, bean.getStr());
        assertNull(bean.getStr(i));
        
        bean.doSomething();
        assertEquals(expected, bean.getStr());
        assertNull(bean.getStr(i));
        
        bean.doSomethingWithArguments(null, 7);
        assertEquals(expected, bean.getStr());
        assertNull(bean.getStr(i));
    }
    
    @Test
    public void beanInterfaceInheritance() {
        SubBean bean = factory.create(SubBean.class);
        String val = "some long string value with spaces";
        
        assertNull(bean.getCode());
        bean.setCode(val);
        assertSame(val, bean.getCode());
        
        assertSimpleBean(bean);
        
        assertSame(val, bean.getCode());
        bean.setCode(null);
        assertNull(bean.getCode());
    }
    
    public interface SubBean extends SimpleBean {
        
        String getCode();
        
        void setCode(String code);
        
    }
    
    @Test
    public void autoboxingSupport() {
        AutoboxingBean bean = factory.create(AutoboxingBean.class);
        Random rnd = new Random();
        int index = rnd.nextInt();
        
        bean.setIndex(index);
        assertEquals(index, bean.getIndex());
        
        bean.setFlag(true);
        assertTrue(bean.getFlag());
        bean.setFlag(false);
        assertFalse(bean.getFlag());
    }

    @Test
    public void autoboxingUsesDefaultValues() {
        AutoboxingBean bean = factory.create(AutoboxingBean.class);
        assertEquals(0, bean.getIndex());
        assertFalse(bean.getFlag());
    }

    public interface AutoboxingBean {

        int getIndex();

        void setIndex(int index);

        boolean getFlag();

        void setFlag(boolean flag);

    }

    @Test
    public void getterSetterNamesMustMatch() {
        TypoBean bean = factory.create(TypoBean.class);
        String example = "str value";
        bean.setPorp(example);
        assertNull(bean.getProp());
        bean.setValue(example);
        assertEquals(example, bean.getValue());
    }

    public interface TypoBean {

        String getProp();

        void setPorp(String prop);

        String getValue();

        void setValue(String valeu);

    }

    @Test
    public void genericPropertySupport() {
        BeanWithGenericProperty<String> strBean = factory.create(BeanWithGenericProperty.class);
        String str1 = "some string value";

        assertNull(strBean.getValue());
        strBean.setValue(str1);
        assertEquals(str1, strBean.getValue());

        BeanWithGenericProperty<Long> longBean = factory.create(BeanWithGenericProperty.class);
        Long ln = new Random().nextLong();

        assertNull(longBean.getValue());
        longBean.setValue(ln);
        assertEquals(ln, longBean.getValue());
    }

    public interface BeanWithGenericProperty<T> {

        T getValue();

        void setValue(T value);

    }

    @Test
    public void getterNamesSupportIsPrefixForBooleanProperties() {

        BooleanProperties bean = factory.create(BooleanProperties.class);
        assertFalse("boolean initial value is false", bean.isMarked());
        assertFalse("boolean initial value is false", bean.getFlag1());
        assertNull("Boolean initial value is null", bean.getFlag2());
        assertNull("Boolean initial value is null", bean.isWrapped());

        bean.setMarked(true);
        assertTrue(bean.isMarked());
        bean.setMarked(false);
        assertFalse(bean.isMarked());

        bean.setFlag1(true);
        assertTrue(bean.getFlag1());
        bean.setFlag1(false);
        assertFalse(bean.getFlag1());

        bean.setFlag2(true);
        assertEquals(Boolean.TRUE, bean.getFlag2());
        bean.setFlag2(false);
        assertEquals(Boolean.FALSE, bean.getFlag2());
        bean.setFlag2(null);
        assertNull(bean.getFlag2());

        bean.setWrapped(true);
        assertEquals(Boolean.TRUE, bean.isWrapped());
        bean.setWrapped(false);
        assertEquals(Boolean.FALSE, bean.isWrapped());
        bean.setWrapped(null);
        assertNull(bean.isWrapped());

        bean.setMixed(true);
        assertTrue(bean.isMixed());
        assertEquals(Boolean.TRUE, bean.getMixed());
        bean.setMixed(null);
        assertFalse(bean.isMixed());
        assertNull(bean.getMixed());
        bean.setMixed(true);
        bean.setMixed(false);
        assertFalse(bean.isMixed());
        assertEquals(Boolean.FALSE, bean.getMixed());

        assertNull(bean.getSpecified());
        bean.setSpecified(true);
        assertEquals(Boolean.TRUE, bean.getSpecified());
        bean.setSpecified(false);
        assertEquals(Boolean.FALSE, bean.getSpecified());
    }

    public interface BooleanProperties {

        boolean isMarked();

        void setMarked(boolean marked);

        boolean getFlag1();

        void setFlag1(boolean flag1);

        Boolean getFlag2();

        void setFlag2(Boolean flag2);

        Boolean isWrapped();

        void setWrapped(Boolean wrapped);

        boolean isMixed();

        Boolean getMixed();

        void setMixed(Boolean mixed);

        Boolean getSpecified();

        void setSpecified(boolean specified);

    }

    @Test
    public void createWithInitialValues() {
        HashMap<String, Object> initialValues = new HashMap<>();
        initialValues.put("value", 10L);
        SimpleBean bean = factory.createWithInitialValues(SimpleBean.class, initialValues);
        assertNull(bean.getStr());
        assertEquals(Long.valueOf(10), bean.getValue());
    }

    @Test
    public void initialValuesMapIsCopied() {
        HashMap<String, Object> initialValues = new HashMap<>();
        initialValues.put("value", 10L);
        SimpleBean bean = factory.createWithInitialValues(SimpleBean.class, initialValues);
        initialValues.put("str", "a string");
        assertNull(bean.getStr());
        assertEquals(Long.valueOf(10), bean.getValue());
        SimpleBean another = factory.createWithInitialValues(SimpleBean.class, initialValues);
        assertEquals("a string", another.getStr());
        assertEquals(Long.valueOf(10), another.getValue());
    }

    @Test
    public void initialValuesForNonExistingPropertiesAreIgnored() {
        HashMap<String, Object> initialValues = new HashMap<>();
        initialValues.put("value", 10L);
        initialValues.put("age", 18);
        SimpleBean bean = factory.createWithInitialValues(SimpleBean.class, initialValues);
        assertNull(bean.getStr());
        assertEquals(Long.valueOf(10), bean.getValue());
        BeanProperties properties = DynaBeanInstance.accessProperties(bean);
        assertEquals(Long.valueOf(10), properties.get("value"));
        assertNull(properties.get("age"));
    }

    @Test
    public void nullAsInitialValue() {
        HashMap<String, Object> initialValues = new HashMap<>();
        initialValues.put("str", null);
        initialValues.put("value", 10L);
        SimpleBean bean = factory.createWithInitialValues(SimpleBean.class, initialValues);
        assertNull(bean.getStr());
        assertEquals(Long.valueOf(10), bean.getValue());
    }

    @Test
    public void getClassReturnsNonNull() {
        SimpleBean bean = factory.create(SimpleBean.class);
        assertNotNull(bean.getClass());
    }

    @Test
    public void equalsAndHashCode() {
        HashMap<String, Object> initialValues = new HashMap<>();
        initialValues.put("str", "a string literal");
        SimpleBean firstBean = factory.createWithInitialValues(SimpleBean.class, initialValues);
        assertEquals(firstBean, firstBean);
        assertEquals(firstBean.hashCode(), firstBean.hashCode());

        SimpleBean secondBean = factory.createWithInitialValues(SimpleBean.class, initialValues);
        assertEquals(firstBean, secondBean);
        assertEquals(firstBean.hashCode(), secondBean.hashCode());

        secondBean.setValue(-1L);
        assertNotEquals(firstBean, secondBean);

        secondBean.setValue(null);
        assertEquals(firstBean, secondBean);
        assertEquals(firstBean.hashCode(), secondBean.hashCode());
    }

    @Test
    public void dynabeanNotEqualToDifferentDynabeanTypeWithSamePropertyValues() {
        HashMap<String, Object> initialValues = new HashMap<>();
        initialValues.put("str", "a string literal");
        SimpleBean firstBean = factory.createWithInitialValues(SimpleBean.class, initialValues);
        SimpleBean secondBean = factory.createWithInitialValues(SubBean.class, initialValues);
        assertNotEquals(firstBean, secondBean);
    }

    @Test
    public void dynabeanNotEqualToImplementationInstanceWithSamePropertyValues() {
        HashMap<String, Object> initialValues = new HashMap<>();
        initialValues.put("str", "a string literal");
        SimpleBean firstBean = factory.createWithInitialValues(SimpleBean.class, initialValues);
        SimpleBean secondBean = new SimpleVO();
        secondBean.setStr(firstBean.getStr());
        assertNotEquals(firstBean, secondBean);
    }

    @Test
    public void equalsAndHashCodeForPrimitiveProperties() {
        AutoboxingBean firstBean = factory.create(AutoboxingBean.class);
        AutoboxingBean secondBean = factory.create(AutoboxingBean.class);
        assertEquals(firstBean, secondBean);
        assertEquals(firstBean.hashCode(), secondBean.hashCode());

        secondBean.setFlag(false);
        assertNotEquals(firstBean, secondBean);

        firstBean.setFlag(false);
        assertEquals(firstBean, secondBean);
        assertEquals(firstBean.hashCode(), secondBean.hashCode());

        firstBean.setIndex(1);
        assertNotEquals(firstBean, secondBean);
    }

    public static class SimpleVO implements SimpleBean {

        private String str;
        private Long value;

        public String getStr() {
            return str;
        }

        public void setStr(String str) {
            this.str = str;
        }

        public Long getValue() {
            return value;
        }

        public void setValue(Long value) {
            this.value = value;
        }
    }

}
