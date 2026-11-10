package com.fitlife.treino.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import com.tngtech.archunit.library.Architectures;

@AnalyzeClasses(packages = "com.fitlife.treino", importOptions = ImportOption.DoNotIncludeTests.class)
class TreinoArchitectureTest {
    @ArchTest
    static final ArchRule camadas_respeitam_a_arquitetura_hexagonal = Architectures.layeredArchitecture()
            .consideringAllDependencies()
            .layer("Domain").definedBy("..domain..")
            .layer("Application").definedBy("..application..")
            .layer("Adapters").definedBy("..adapters..")
            .layer("Configuration").definedBy("..config..")
            .whereLayer("Domain").mayOnlyBeAccessedByLayers("Application", "Adapters")
            .whereLayer("Application").mayOnlyBeAccessedByLayers("Adapters", "Configuration");

    @ArchTest
    static final ArchRule adaptadores_web_nao_acessam_persistencia = noClasses()
            .that().resideInAPackage("..adapters.in.web..")
            .should().dependOnClassesThat().resideInAPackage("..adapters.out.persistence..");
}
