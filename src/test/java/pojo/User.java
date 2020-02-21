package pojo;

import lombok.*;

import java.util.concurrent.atomic.AtomicInteger;

@Setter(value = AccessLevel.PUBLIC)
@Getter(value = AccessLevel.PUBLIC)
@ToString(of = {"id", "name", "age"})
@AllArgsConstructor
public class User {
    private int id;
    private String name;
    private int age;

    private User(Builder builder) {
        this.id = Builder.id.getAndIncrement();
        this.name = builder.name;
        this.age = builder.age;
    }

    public static class Builder {
        private static AtomicInteger id = new AtomicInteger(1);
        private String name;
        private int age;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder age(int age) {
            this.age = age;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }
}
