package org.mitre.synthea.export.cp;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.securitytoken.AWSSecurityTokenService;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClientBuilder;
import com.amazonaws.services.securitytoken.model.AssumeRoleRequest;
import com.amazonaws.services.securitytoken.model.AssumeRoleResult;
import com.amazonaws.services.securitytoken.model.Credentials;
import org.mitre.synthea.export.Exporter;
import org.mitre.synthea.export.FhirR4;
import org.mitre.synthea.helpers.Config;
import org.mitre.synthea.world.agents.Person;

public class S3Exporter implements IExporter {
    AmazonS3 s3;

    public S3Exporter() {
        AWSSecurityTokenService stsClient = AWSSecurityTokenServiceClientBuilder.standard()
                .withCredentials(new ProfileCredentialsProvider())
                .withRegion(Regions.US_EAST_1)
                .build();
        AssumeRoleRequest roleRequest = new AssumeRoleRequest()
                .withRoleArn(Config.get("exporter.aws.role"))
                .withRoleSessionName("customSession");
        AssumeRoleResult roleResponse = stsClient.assumeRole(roleRequest);
        Credentials sessionCredentials = roleResponse.getCredentials();
        BasicSessionCredentials awsCredentials = new BasicSessionCredentials(
                sessionCredentials.getAccessKeyId(),
                sessionCredentials.getSecretAccessKey(),
                sessionCredentials.getSessionToken());


        this.s3 = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .withRegion(Regions.US_EAST_1)
                .build();
    }

    @Override
    public void export(Person person, long stopTime) {
        String fileName = Exporter.filename(person, "", "json");
        try {
            String bundleJson = FhirR4.convertToFHIRJson(person, stopTime);
            this.s3.putObject(Config.get("exporter.aws.bucket"), fileName, bundleJson);
            System.out.println("File " + fileName + " successfully saved into s3 bucket");
        } catch (Exception e) {
            System.out.println("Error sending file " + fileName + "" +
                    " to S3, code: ");
            e.printStackTrace();
        }
    }
}
