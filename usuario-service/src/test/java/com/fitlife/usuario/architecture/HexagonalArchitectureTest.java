package com.fitlife.usuario.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

@AnalyzeClasses(packages = "com.fitlife.usuario", importOptions = ImportOption.DoNotIncludeTests.class)
class HexagonalArchitectureTest {
    @ArchTest
    static final ArchRule dominio_nao_depende_das_camadas_externas = noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat().resideInAnyPackage("..application..", "..infrastructure..", "org.springframework..");

    @ArchTest
    static final ArchRule aplicacao_nao_depende_de_infraestrutura_ou_spring = noClasses()
            .that().resideInAPackage("..application..")
            .should().dependOnClassesThat().resideInAnyPackage("..infrastructure..", "org.springframework..");

    @ArchTest
    static final ArchRule adaptadores_de_entrada_nao_acessam_adaptadores_de_saida = noClasses()
            .that().resideInAPackage("..infrastructure.adapter.in..")
            .should().dependOnClassesThat().resideInAPackage("..infrastructure.adapter.out..");

    @ArchTest
    static final ArchRule dominio_nao_depende_de_adaptadores = noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat().resideInAnyPackage("..infrastructure..", "org.springframework..", "com.azure..", "java.sql..", "com.fasterxml..", "org.bson..", "com.mongodb..")
            .because("O domínio deve permanecer independente de frameworks, bancos e mensageria.");

    @ArchTest
    static final ArchRule pacotes_sem_ciclos = slices()
            .matching("com.fitlife.usuario.(*)..")
            .should().beFreeOfCycles();
}
