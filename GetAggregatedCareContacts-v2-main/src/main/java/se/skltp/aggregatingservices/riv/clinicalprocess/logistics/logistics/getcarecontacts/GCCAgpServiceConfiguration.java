package se.skltp.aggregatingservices.riv.clinicalprocess.logistics.logistics.getcarecontacts;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import riv.clinicalprocess.logistics.logistics.getcarecontactsrequest.v2.GetCareContactsResponderInterface;
import riv.clinicalprocess.logistics.logistics.getcarecontactsrequest.v2.GetCareContactsResponderService;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "getaggregatedcarecontacts.v2")
public class GCCAgpServiceConfiguration extends se.skltp.aggregatingservices.configuration.AgpServiceConfiguration {

  public static final String SCHEMA_PATH = "/schemas/clinicalprocess_logistics_logistics_2.0.0/interactions/GetCareContactsInteraction/GetCareContactsInteraction_2.0_RIVTABP21.wsdl";

  public GCCAgpServiceConfiguration() {

    setServiceName("GetAggregatedCareContacts-v2");
    setTargetNamespace("urn:riv:clinicalprocess:logistics:logistics:GetCareContacts:2:rivtabp21");

    // Set inbound defaults
    setInboundServiceURL("http://0.0.0.0:9002/GetAggregatedCareContacts/service/v2");
    setInboundServiceWsdl(SCHEMA_PATH);
    setInboundServiceClass(GetCareContactsResponderInterface.class.getName());
    setInboundPortName(GetCareContactsResponderService.GetCareContactsResponderPort.toString());

    // Set outbound defaults
    setOutboundServiceWsdl(SCHEMA_PATH);
    setOutboundServiceClass(getInboundServiceClass());
    setOutboundPortName(getInboundPortName());

    // FindContent
    setEiServiceDomain("riv:clinicalprocess:logistics:logistics");
    setEiCategorization("vko");

    // TAK
    setTakContract("urn:riv:clinicalprocess:logistics:logistics:GetCareContactsResponder:2");

    // Set service factory
    setServiceFactoryClass(GCCAgpServiceFactoryImpl.class.getName());
    }


}
