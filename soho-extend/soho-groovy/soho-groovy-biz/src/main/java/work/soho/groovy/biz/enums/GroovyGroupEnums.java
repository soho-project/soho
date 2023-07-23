package work.soho.groovy.biz.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class GroovyGroupEnums {
    @Getter
    @RequiredArgsConstructor
    public enum Status {
        NORMAL(1,"正常"),
        DISABLED(2,"禁用");
        private int id;
        private String name;


        Status(int i, String name) {
            this.id = i;
            this.name = name;
        }
    }
}
