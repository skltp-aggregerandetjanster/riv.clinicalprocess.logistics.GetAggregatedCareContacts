package se.skltp.aggregatingservices.riv.clinicalprocess.logistics.getcarecontacts.integrationtest;

import static se.skltp.agp.test.producer.TestProducerDb.TEST_RR_ID_ONE_HIT;

import javax.xml.ws.Holder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.riv.clinicalprocess.logistics.getcarecontactsrequest.v2.GetCareContactsResponderInterface;
import se.riv.clinicalprocess.logistics.getcarecontactsresponder.v2.GetCareContactsResponseType;
import se.riv.clinicalprocess.logistics.getcarecontactsresponder.v2.GetCareContactsType;
import se.riv.clinicalprocess.logistics.v2.PatientIdType;
import se.skltp.aggregatingservices.CareContactMuleServer;
import se.skltp.agp.riv.interoperability.headers.v1.ProcessingStatusType;
import se.skltp.agp.test.consumer.AbstractTestConsumer;
import se.skltp.agp.test.consumer.SoapHeaderCxfInterceptor;

public class CareContactTestConsumer extends AbstractTestConsumer<GetCareContactsResponderInterface>{

    private static final Logger log = LoggerFactory.getLogger(CareContactTestConsumer.class);

    public static void main(String[] args) {
        log.info("URL: " + CareContactMuleServer.getAddress("SERVICE_INBOUND_URL"));
        String serviceAddress = CareContactMuleServer.getAddress("SERVICE_INBOUND_URL");
        String personnummer = TEST_RR_ID_ONE_HIT;

        CareContactTestConsumer consumer = new CareContactTestConsumer(serviceAddress, SAMPLE_ORIGINAL_CONSUMER_HSAID);
        Holder<GetCareContactsResponseType> responseHolder = new Holder<GetCareContactsResponseType>();
        Holder<ProcessingStatusType> processingStatusHolder = new Holder<ProcessingStatusType>();
        long now = System.currentTimeMillis();
        consumer.callService("logical-adress", personnummer, processingStatusHolder, responseHolder);
        log.info("Returned #care contact = " + responseHolder.value.getCareContact().size() + " in " + (System.currentTimeMillis() - now) + " ms.");	
    }

    public CareContactTestConsumer(String serviceAddress, String originalConsumerHsaId) {
        // Setup a web service proxy for communication using HTTPS with Mutual Authentication
        super(GetCareContactsResponderInterface.class, serviceAddress, originalConsumerHsaId); 
    }

    public void callService(String logicalAddress, String id, Holder<ProcessingStatusType> processingStatusHolder, Holder<GetCareContactsResponseType> responseHolder) {
        log.debug("Calling GetCareContact-soap-service with id = {}", id);

        GetCareContactsType request = new GetCareContactsType();
        PatientIdType patientId = new PatientIdType();
        patientId.setId(id);
        request.setPatientId(patientId);

        GetCareContactsResponseType response = _service.getCareContacts(logicalAddress, request);
        responseHolder.value = response;

        processingStatusHolder.value = SoapHeaderCxfInterceptor.getLastFoundProcessingStatus();
    }
}