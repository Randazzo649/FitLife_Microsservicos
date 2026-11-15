package com.fitlife.comunidade.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

@AnalyzeClasses(packages = "com.fitlife.comunidade", importOptions = ImportOption.DoNotIncludeTests.class)
class ComunidadeArchitectureTest {
    @ArchTest
    static final ArchRule pacotes_sem_ciclos = slices().matching("com.fitlife.comunidade.(*)..").should().beFreeOfCycles();
}
