package nl.craftsmen.orders;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.library.Architectures;

@AnalyzeClasses(
        packages = "nl.craftsmen.orders",
        importOptions = {
                ImportOption.DoNotIncludeTests.class,
                ImportOption.DoNotIncludeJars.class
        }
)
public class ArchitectureTest {

    @ArchTest
    static final Architectures.OnionArchitecture onionArchitecture =
            Architectures.onionArchitecture()

                    .domainModels("nl.craftsmen.orders.application.domain..")
                    .domainServices("nl.craftsmen.orders.application.domain..")

                    .applicationServices("nl.craftsmen.orders.application..")

                    .adapter("controllers", "nl.craftsmen.orders.adapters.controllers..")
                    .adapter("repository", "nl.craftsmen.orders.adapters.repository..")
                    .adapter("restclient", "nl.craftsmen.orders.adapters.restclient..");
}