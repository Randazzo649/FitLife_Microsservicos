package com.fitlife.treino.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

@AnalyzeClasses(packages = "com.fitlife.treino", importOptions = ImportOption.DoNotIncludeTests.class)
class TreinoArchitectureTest {
    @ArchTest
    static final ArchRule pacotes_sem_ciclos = slices().matching("com.fitlife.treino.(*)..").should().beFreeOfCycles();
}
