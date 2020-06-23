package org.mitre.synthea.export.cp;

import org.mitre.synthea.export.Exporter;
import org.mitre.synthea.export.FhirR4;
import org.mitre.synthea.world.agents.Person;

import java.io.File;
import java.nio.file.Path;

public class LocalExporter implements IExporter {
    @Override
    public void export(Person person, long stopTime) {
        File outDirectory = Exporter.getOutputFolder("fhir", person);
        String fileName = Exporter.filename(person, "", "json");
        Path outFilePath = outDirectory.toPath().resolve(fileName);
        String bundleJson = FhirR4.convertToFHIRJson(person, stopTime);
        Exporter.writeNewFile(outFilePath, bundleJson);
        System.out.println("File " + fileName + " successfully saved locally");
    }
}
