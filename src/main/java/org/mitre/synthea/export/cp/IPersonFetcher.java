package org.mitre.synthea.export.cp;

import org.mitre.synthea.client.TCPPerson;

import java.io.IOException;
import java.util.List;

public interface IPersonFetcher {
    List<TCPPerson> getTcpPersons() throws IOException;
}
