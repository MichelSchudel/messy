package nl.craftsmen.orders;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.library.Architectures;
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition;

@AnalyzeClasses(
        packages = "nl.craftsmen.orders",
        importOptions = {
                ImportOption.DoNotIncludeTests.class,
                ImportOption.DoNotIncludeJars.class
        }
)
class ArchitectureTest {

    @ArchTest
    static final com.tngtech.archunit.lang.ArchRule onion_architecture_is_respected =
            Architectures.onionArchitecture()
                    .domainModels("nl.craftsmen.orders.application.domain..")
                    // If you later introduce explicit domain services (separate from use-cases),
                    // put them under application.domain.services.. and adapt this line.
                    .domainServices("nl.craftsmen.orders.application.domain..")
                    .applicationServices("nl.craftsmen.orders.application..")
                    .adapter("controller", "nl.craftsmen.orders.adapters.controller..")
                    .adapter("repository", "nl.craftsmen.orders.adapters.repository..")
                    .adapter("messages", "nl.craftsmen.orders.adapters.consumer..", "nl.craftsmen.orders.adapters.publisher..")
                    .adapter("clients", "nl.craftsmen.orders.adapters.restclient..");

    @ArchTest
    static final com.tngtech.archunit.lang.ArchRule no_package_cycles_exist =
            SlicesRuleDefinition.slices()
                    .matching("nl.craftsmen.orders.(*)..")
                    .should()
                    .beFreeOfCycles();
}