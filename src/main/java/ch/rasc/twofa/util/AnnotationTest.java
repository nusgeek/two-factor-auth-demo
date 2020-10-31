package ch.rasc.twofa.util;

import org.springframework.context.annotation.Bean;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

public class AnnotationTest {
    public static void main(String[] args) {
        Target t = Bean.class.getAnnotation(Target.class);
        ElementType[] elementTypes = t.value();
        for (ElementType type : elementTypes) {
            System.out.println("type = " + type.toString());
        }

        System.out.printf(Boolean.toString(Bean.class.isAnnotationPresent(Retention.class)));
        System.out.printf(Boolean.toString(Bean.class.isAnnotationPresent(Inherited.class)));
    }
}
