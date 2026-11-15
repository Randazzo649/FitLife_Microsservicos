package com.fitlife.usuario.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

@AnalyzeClasses(packages = "com.fitlife.usuario", importOptions = ImportOption.DoNotIncludeTests.class)
class UsuarioArchitectureTest {
    @ArchTest
    static final ArchRule pacotes_sem_ciclos = slices().matching("com.fitlife.usuario.(*)..").should().beFreeOfCycles();
}
