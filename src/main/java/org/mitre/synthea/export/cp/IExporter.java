package org.mitre.synthea.export.cp;

import org.mitre.synthea.world.agents.Person;

public interface IExporter {
    void export(Person person, long stopTime);
}
