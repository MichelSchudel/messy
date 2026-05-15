package nl.craftsmen.orders.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.library.Architectures.onionArchitecture;

class CleanArchitectureTest {

    private static final String ROOT = "nl.craftsmen.orders";

    @Test
    void cleanArchitecture() {
        JavaClasses classes = new ClassFileImporter()
                .withImportOption(new ImportOption.DoNotIncludeTests())
                .importPackages(ROOT);

        onionArchitecture()
                .withOptionalLayers(true)
                .domainModels(ROOT + ".domain..")
                .applicationServices(ROOT + ".application..")
                .adapter("inbound", ROOT + ".adapter.inbound..")
                .adapter("outbound", ROOT + ".adapter.outbound..")
                .check(classes);
    }
}
