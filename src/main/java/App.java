import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import org.mitre.synthea.client.IPersonService;
import org.mitre.synthea.engine.Generator;
import org.mitre.synthea.export.FhirR4;
import org.mitre.synthea.export.cp.IPersonFetcher;
import org.mitre.synthea.export.cp.PersonFetcher;
import org.mitre.synthea.helpers.Config;

/*
 * This Java source file was generated by the Gradle 'init' task.
 */
public class App {

  /**
   * Display usage info - what are the command line args, examples, etc.
   */
  public static void usage() {
    System.out.println("Usage: run_synthea [options] [state [city]]");
    System.out.println("Options: [-s seed] [-cs clinicianSeed] [-p populationSize]");
    System.out.println("         [-g gender] [-a minAge-maxAge]");
    System.out.println("         [-o overflowPopulation]");
    System.out.println("         [-m moduleFileWildcardList]");
    System.out.println("         [-c localConfigFilePath]");
    System.out.println("         [-d localModulesDirPath]");
    System.out.println("         [-i initialPopulationSnapshotPath]");
    System.out.println("         [-u updatedPopulationSnapshotPath]");
    System.out.println("         [-t updateTimePeriodInDays]");
    System.out.println("         [--config* value]");
    System.out.println("          * any setting from src/main/resources/synthea.properties");
    System.out.println("Examples:");
    System.out.println("run_synthea Massachusetts");
    System.out.println("run_synthea Alaska Juneau");
    System.out.println("run_synthea -s 12345");
    System.out.println("run_synthea -p 1000)");
    System.out.println("run_synthea -s 987 Washington Seattle");
    System.out.println("run_synthea -s 21 -p 100 Utah \"Salt Lake City\"");
    System.out.println("run_synthea -g M -a 60-65");
    System.out.println("run_synthea -p 10 --exporter.fhir.export true");
    System.out.println("run_synthea -m moduleFilename" + File.pathSeparator + "anotherModule"
        + File.pathSeparator + "module*");
    System.out.println("run_synthea --exporter.baseDirectory \"./output_tx/\" Texas");
  }

  /**
   * Run Synthea generation.
   * @param args None. See documentation on configuration.
   * @throws Exception On errors.
   */
  public static void main(String[] args) throws Exception {
    Generator.GeneratorOptions options = new Generator.GeneratorOptions();

    boolean validArgs = true;
    if (args != null && args.length > 0) {
      try {
        Queue<String> argsQ = new LinkedList<String>(Arrays.asList(args));

        while (!argsQ.isEmpty()) {
          String currArg = argsQ.poll();

          if (currArg.equalsIgnoreCase("-h")) {
            usage();
            System.exit(0);
          } else if (currArg.equalsIgnoreCase("-s")) {
            String value = argsQ.poll();
            options.seed = Long.parseLong(value);
          } else if (currArg.equalsIgnoreCase("-fhir")) {
            options.sendToFhir = true;
          }else if (currArg.equalsIgnoreCase("-local")) {
            options.saveLocally = true;
            options.population = 10;
          }else if (currArg.equalsIgnoreCase("-s3")) {

              options.saveIntoS3 = true;
          }else if (currArg.equalsIgnoreCase("-gluu")) {
            /*String gluuData = argsQ.poll();
            String[] data = gluuData.split(",");
            if((data.length%2)!=0){
              throw new Exception("Invalid gluu data.");
            }
            int population = 0;
            Map<String,Integer> config = new HashMap<>();
            for(int x=0;x<data.length-1;x+=2){
              String username = data[x];
              int patients = Integer.parseInt(data[x+1]);
              population+=patients;
              config.put(username,patients);
            }*/
              options.population = 10;
              //FhirR4.population = config;
            IPersonFetcher personService = new PersonFetcher();
            FhirR4.persons = personService.getTcpPersons();
          }else if (currArg.equalsIgnoreCase("-cs")) {
            String value = argsQ.poll();
            options.clinicianSeed = Long.parseLong(value);
          }  else if (currArg.equalsIgnoreCase("-o")) {
            String value = argsQ.poll();
            options.overflow = Boolean.parseBoolean(value);
          } else if (currArg.equalsIgnoreCase("-g")) {
            String value = argsQ.poll();
            if (value.equals("M") || value.equals("F")) {
              options.gender = value;
            } else {
              throw new Exception("Legal values for gender are 'M' or 'F'.");
            }
          } else if (currArg.equalsIgnoreCase("-a")) {
            String value = argsQ.poll();
            if (value.contains("-")) {
              String[] values = value.split("-");
              options.ageSpecified = true;
              options.minAge = Integer.parseInt(values[0]);
              options.maxAge = Integer.parseInt(values[1]);
            } else {
              throw new Exception("Age format: minAge-maxAge. E.g. 60-65.");
            }
          } else if (currArg.equalsIgnoreCase("-m")) {
            String value = argsQ.poll();
            String[] values = value.split(File.pathSeparator);
            options.enabledModules = Arrays.asList(values);
          } else if (currArg.equalsIgnoreCase("-c")) {
            // TODO: if the user specifies configuration options either here or
            // using --config.setting, those values will not be used to
            // initialize the Generator.GeneratorOptions options declared above.
            // Currently only generate.default_population Config setting is used
            // by Generator.GeneratorOptions so this should not be an issue.
            String value = argsQ.poll();
            File configFile = new File(value);
            Config.load(configFile);
          } else if (currArg.equalsIgnoreCase("-d")) {
            String value = argsQ.poll();
            File localModuleDir = new File(value);
            if (localModuleDir.exists() && localModuleDir.isDirectory()) {
              options.localModuleDir = localModuleDir;
            } else {
              throw new FileNotFoundException(String.format(
                      "Specified local module directory (%s) is not a directory",
                      localModuleDir.getAbsolutePath()));
            }
          } else if (currArg.equalsIgnoreCase("-u")) {
            String value = argsQ.poll();
            File file = new File(value);
            try {
              if (file.createNewFile()) {
                options.updatedPopulationSnapshotPath = file;
              } else {
                throw new IOException("File exists");
              }
            } catch (IOException ex) {
              throw new IOException(String.format("Unable to create snapshot file (%s): %s",
                      file.getAbsolutePath(), ex.getMessage()));
            }
          } else if (currArg.equalsIgnoreCase("-i")) {
            String value = argsQ.poll();
            File file = new File(value);
            try {
              if (file.exists() && file.canRead()) {
                options.initialPopulationSnapshotPath = file;
              } else {
                throw new IOException("File does not exist or is not readable");
              }
            } catch (IOException ex) {
              throw new IOException(String.format("Unable to load snapshot file (%s): %s",
                      file.getAbsolutePath(), ex.getMessage()));
            }
          } else if (currArg.startsWith("-t")) {
            String value = argsQ.poll();
            try {
              options.daysToTravelForward = Integer.parseInt(value);
              if (options.daysToTravelForward < 1) {
                throw new NumberFormatException("Must be a positive, non-zero integer");
              }
            } catch (NumberFormatException ex) {
              throw new IllegalArgumentException(
                      String.format(
                              "Error in specified updateTimePeriodInDays (%s): %s",
                              value,
                              ex.getMessage()));
            }
          } else if (currArg.startsWith("--")) {
            String configSetting;
            String value;
            // accept either:
            // --config.setting=value
            // --config.setting value
            if (currArg.contains("=")) {
              String[] parts = currArg.split("=", 2);
              configSetting = parts[0].substring(2);
              value = parts[1];
            } else {
              configSetting = currArg.substring(2);
              value = argsQ.poll();
            }

            Config.set(configSetting, value);
          } else if (options.state == null) {
            options.state = currArg;
          } else {
            // assume it must be the city
            options.city = currArg;
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
        usage();
        validArgs = false;
      }
    }

    if (validArgs) {
      Generator generator = new Generator(options);
      generator.run();
    }
  }
}
