package com.lovely4k.backend.architect;

import com.tngtech.archunit.core.domain.*;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

class ArchTests {

    private static JavaClasses importedClasses;

    @BeforeAll
    public static void setUp() {
        importedClasses = new ClassFileImporter()
            .importPackages("com.lovely4k.backend"); // 프로젝트의 모든 패키지 (모듈들 포함)
    }

    @DisplayName("패키지 간 의존 관계가 순환되면 안된다.")
    @Test
    void noCyclesInPackages() {
        SlicesRuleDefinition.slices()
            .matching("com.lovely4k.backend.(*)..")  // 패키지 구조를 정의 backend 패키지 내의 상위 패키지에만 적용
            .should().beFreeOfCycles()  // 순환 의존성이 없어야 함
            .check(importedClasses);  // 테스트 실행
    }

    @DisplayName("컨트롤러 서비스 클래스는 상태를 갖는 필드를 가질 수 없다.")
    @Test
    void statelessTest() {
        ArchRuleDefinition.classes()
            .that().haveSimpleNameEndingWith("Service").or().haveSimpleNameEndingWith("Controller") // 서비스, 컨트롤러로 끝나는 클래스
            .should().haveOnlyFinalFields()//final 필드만 갖는다.
            .because("we want to ensure stateless services")
            .check(importedClasses);
    }

    @DisplayName("컨트롤러 서비스의 메소드는 너무 5개 이상의 파라미터를 가질 수 없다.")
    @Test
    void methodsShouldHaveLessThanFourParameters() {
        ArchRule rule = methods()
            .that().areDeclaredInClassesThat().haveSimpleNameEndingWith("Service")
            .or().areDeclaredInClassesThat().haveSimpleNameEndingWith("Controller")
            .should(new ArchCondition<JavaMethod>("have less than 4 parameters") {
                @Override
                public void check(JavaMethod method, ConditionEvents events) {
                    if (method.getRawParameterTypes().size() >= 6) {
                        String message = String.format("%s has too many parameters (%d)", method.getFullName(), method.getRawParameterTypes().size());
                        events.add(SimpleConditionEvent.violated(method, message));
                    }
                }
            });

        rule.check(importedClasses);
    }

    @DisplayName("Controller 클래스 의존성 테스트")
    @Test
    void controllerDependencyTest() {
        ArchRule shouldClassRule = classes().that().haveSimpleNameEndingWith("Controller")  // 이름이 Controller 로 끝나는 모든 클래스 -> C-Class 라고 명명
            .should().accessClassesThat().haveSimpleNameEndingWith("Service")   // C-Class는 Service로 끝나는 클래스에만 접근해야 한다
            .orShould().accessClassesThat().haveSimpleNameEndingWith("Repository"); // C-Class는 Repository로 끝나는 클래스에 접근할 수 있다.

        shouldClassRule.check(importedClasses);
    }

    @DisplayName("Service 클래스 의존성 테스트")
    @Test
    void serviceDependencyTest() {
        ArchRule shouldClassRule = classes().that().haveSimpleNameNotEndingWith("QueryService").and().haveSimpleNameEndingWith("Service")  // Service 클래스
            .should().accessClassesThat().haveSimpleNameEndingWith("Repository");   // Service 클래스는 Repository에 접근할 수 있다.

        ArchRule shouldNotClassRule = noClasses().that().haveSimpleNameNotEndingWith("QueryService").and().haveSimpleNameEndingWith("Service")  // noClasses() -> 반대의 하위 규칙의 반대
            .should().accessClassesThat().haveSimpleNameEndingWith("Controller");   // Service 클래스는 Controller에 접근할 수 있다.

        shouldClassRule.check(importedClasses);
        shouldNotClassRule.check(importedClasses);
    }

    @DisplayName("Repository 클래스 의존성 테스트")
    @Test
    void repositoryDependencyTest() {
        ArchRule shouldNotClassRule = noClasses().that().haveSimpleNameEndingWith("Repository")  // noClasses() -> 반대의 하위 규칙의 반대
            .should().accessClassesThat().haveSimpleNameEndingWith("Service")    // Repository 클래스는 Service에 접근할 수 있다.
            .orShould().accessClassesThat().haveSimpleNameEndingWith("Controller");   // Repository 클래스는 Controller에 접근할 수 있다.

        shouldNotClassRule.check(importedClasses);
    }

    @DisplayName("Maximum Dependency Injection for Controller is 2")
    @Test
    void controllerDependencyInjectionTest() {
        ArchRule controllerDependencyInjectRule = classes().that().haveSimpleNameEndingWith("Controller")
            .should(new ArchCondition<>("have 2 or fewer final fields for dependency injection") {
                @Override
                public void check(JavaClass item, ConditionEvents events) {
                    long finalFieldCount = item.getFields().stream()
                        .filter(field -> field.getModifiers().contains(JavaModifier.FINAL))
                        .filter(field -> !field.getModifiers().contains(JavaModifier.STATIC))  // static 필드 제외
                        .count();

                    boolean satisfied = finalFieldCount <= 2;

                    String message = String.format("%s has %d final fields for dependency injection", item.getSimpleName(), finalFieldCount);

                    events.add(new SimpleConditionEvent(item, satisfied, message));
                }
            });

        controllerDependencyInjectRule.check(importedClasses);
    }

    @DisplayName("Maximum Dependency Injection for Service is 5")
    @Test
    void serviceDependencyInjectionTest() {
        ArchRule serviceDependencyInjectRule = classes().that().haveSimpleNameEndingWith("Service")
                .should(new ArchCondition<>("have 5 or fewer final fields for dependency injection") {
                    @Override
                    public void check(JavaClass item, ConditionEvents events) {
                        long finalFieldCount = item.getFields().stream()
                                .filter(field -> field.getModifiers().contains(JavaModifier.FINAL))
                                .filter(field -> !field.getModifiers().contains(JavaModifier.STATIC))  // static 필드 제외
                                .count();

                    boolean satisfied = finalFieldCount <= 5;

                    String message = String.format("%s has %d final fields for dependency injection", item.getSimpleName(), finalFieldCount);

                    events.add(new SimpleConditionEvent(item, satisfied, message));
                }
            });

        serviceDependencyInjectRule.check(importedClasses);
    }

    @DisplayName("""
            Service class에는 @Transactional(readOnly=true) 옵션이 붙어 있어야 하며,"
            find로 시작하지 않는 public 메서드는 @Transactional 옵션을 붙여 주어야 한다. 
            """)
    @Test
    void readOnlyTest() {
        ArchRule rule = classes()
            .that().areAnnotatedWith(Service.class)
            .and().haveSimpleNameNotEndingWith("QueryService")
            .and().haveSimpleNameNotEndingWith("CommandService")
            .should(haveTransactionalReadOnly())
            .andShould(haveOtherMethodsTransactional());

        rule.check(importedClasses);

    }

    private ArchCondition<JavaClass> haveTransactionalReadOnly() {
        return new ArchCondition<>("have @Transactional(readOnly = true) at class level") {
            @Override
            public void check(JavaClass item, ConditionEvents events) {
                Optional<JavaAnnotation<JavaClass>> transactionalAnnotation = findTransactionalAnnotation(item);
                boolean satisfied = true;
                if (!transactionalAnnotation.isPresent()) satisfied = false;
                Optional<Object> readOnlyValue = transactionalAnnotation.get().get("readOnly");
                if (!isReadOnlyTrue(readOnlyValue)) satisfied = false;

                events.add(new SimpleConditionEvent(item, satisfied, item.getName()));
            }
        };
    }

    private Optional<JavaAnnotation<JavaClass>> findTransactionalAnnotation(JavaClass item) {
        return item.getAnnotations().stream()
            .filter(a -> a.getType().getName().equals("org.springframework.transaction.annotation.Transactional"))
            .findFirst();
    }

    private boolean isReadOnlyTrue(Optional<Object> readOnlyValue) {
        return readOnlyValue.isPresent() && readOnlyValue.get() instanceof Boolean && (Boolean) readOnlyValue.get();
    }


    private ArchCondition<JavaClass> haveOtherMethodsTransactional() {
        return new ArchCondition<>("have @Transactional for other methods") {
            @Override
            public void check(JavaClass item, ConditionEvents events) {
                item.getMethods().stream()
                    .filter(method -> !method.getModifiers().contains(JavaModifier.PRIVATE))
                    .filter(method -> !method.getName().startsWith("find"))
                    .forEach(method -> {
                        boolean satisfied = hasTransactionalAnnotation(method);
                        events.add(new SimpleConditionEvent(method, satisfied, method.getFullName()));
                    });
            }

            private boolean hasTransactionalAnnotation(JavaMethod method) {
                return method.getAnnotationOfType("org.springframework.transaction.annotation.Transactional") != null;
            }
        };
    }

}