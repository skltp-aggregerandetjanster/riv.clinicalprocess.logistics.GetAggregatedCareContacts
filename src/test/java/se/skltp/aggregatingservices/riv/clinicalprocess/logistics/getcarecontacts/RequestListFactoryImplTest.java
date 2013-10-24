package se.skltp.aggregatingservices.riv.clinicalprocess.logistics.getcarecontacts;

import static org.junit.Assert.assertEquals;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import se.riv.clinicalprocess.logistics.getcarecontactsresponder.v2.GetCareContactsType;
import se.riv.clinicalprocess.logistics.v2.DatePeriodType;
import se.riv.clinicalprocess.logistics.v2.PersonIdType;
import se.skltp.agp.riv.itintegration.engagementindex.findcontentresponder.v1.FindContentResponseType;
import se.skltp.agp.riv.itintegration.engagementindex.findcontentresponder.v1.FindContentType;
import se.skltp.agp.riv.itintegration.engagementindex.v1.EngagementType;
import se.skltp.agp.service.api.QueryObject;
import se.skltp.agp.test.producer.TestProducerDb;

public class RequestListFactoryImplTest {

    private static final String CATEGORIZATION = "voo";
    private static final String SERVICE_DOMAIN = "riv:clinicalprocess:healthcond:description";
    private static final String RR_ID = "1212121212"; 
    
    private static final String SOURCE_SYSTEM_1 = "SS1";
    private static final String SOURCE_SYSTEM_2 = "SS2";
    
    @Test
    public void createRequestList(){
        RequestListFactoryImpl requestFactory = new RequestListFactoryImpl();
        FindContentType fc = createFindContent(RR_ID);       
        GetCareContactsType getCareDoc = createGetCareContacts(RR_ID, Collections.<String> emptyList());
        QueryObject queryObject = new QueryObject(fc, getCareDoc);
        FindContentResponseType findContentResponse = createFindContentResponse(TestProducerDb.TEST_LOGICAL_ADDRESS_1, TestProducerDb.TEST_LOGICAL_ADDRESS_2);
        List<Object[]> requestList =  requestFactory.createRequestList(queryObject, findContentResponse);
        assertEquals(2, requestList.size());
        
        assertEquals(TestProducerDb.TEST_LOGICAL_ADDRESS_2, requestList.get(0)[0]);
        GetCareContactsType request1 = (GetCareContactsType)requestList.get(0)[1];
        assertEquals(RR_ID, request1.getPatientId().getId());
        
        assertEquals(TestProducerDb.TEST_LOGICAL_ADDRESS_1, requestList.get(1)[0]);
        GetCareContactsType request2 = (GetCareContactsType)requestList.get(1)[1];
        assertEquals(RR_ID, request2.getPatientId().getId());
    }

    @Test
    public void createRequestList_different_sourceSystems(){
        RequestListFactoryImpl requestFactory = new RequestListFactoryImpl();
        FindContentType fc = createFindContent(RR_ID); 
        GetCareContactsType getCareDoc = createGetCareContacts(RR_ID, Collections.singletonList(TestProducerDb.TEST_LOGICAL_ADDRESS_1));
        QueryObject queryObject = new QueryObject(fc, getCareDoc);
        FindContentResponseType findContentResponse = createFindContentResponse(TestProducerDb.TEST_LOGICAL_ADDRESS_1, TestProducerDb.TEST_LOGICAL_ADDRESS_1);
        findContentResponse.getEngagement().get(0).setSourceSystem(SOURCE_SYSTEM_1);
        findContentResponse.getEngagement().get(1).setSourceSystem(SOURCE_SYSTEM_2);
        
        List<Object[]> requestList =  requestFactory.createRequestList(queryObject, findContentResponse);
        assertEquals(2, requestList.size());

        assertEquals(SOURCE_SYSTEM_2, requestList.get(0)[0]);
        GetCareContactsType request1 = (GetCareContactsType)requestList.get(0)[1];
        assertEquals(RR_ID, request1.getPatientId().getId());
        
        assertEquals(SOURCE_SYSTEM_1, requestList.get(1)[0]);
        GetCareContactsType request2 = (GetCareContactsType)requestList.get(1)[1];
        assertEquals(RR_ID, request2.getPatientId().getId());
    }
    
    @Test
    public void createRequestList_different_careUnits_one_sourceSystem(){
        RequestListFactoryImpl requestFactory = new RequestListFactoryImpl();
        FindContentType fc = createFindContent(RR_ID); 
        GetCareContactsType getCareDoc = createGetCareContacts(RR_ID, Collections.<String> emptyList());
        QueryObject queryObject = new QueryObject(fc, getCareDoc);
        FindContentResponseType findContentResponse = createFindContentResponse(TestProducerDb.TEST_LOGICAL_ADDRESS_1, TestProducerDb.TEST_LOGICAL_ADDRESS_2);
        findContentResponse.getEngagement().get(0).setSourceSystem(SOURCE_SYSTEM_1);
        findContentResponse.getEngagement().get(1).setSourceSystem(SOURCE_SYSTEM_1);
        List<Object[]> requestList =  requestFactory.createRequestList(queryObject, findContentResponse);
        assertEquals(1, requestList.size());
        assertEquals(SOURCE_SYSTEM_1, requestList.get(0)[0]);
        
        GetCareContactsType request = (GetCareContactsType)requestList.get(0)[1];
        assertEquals(RR_ID, request.getPatientId().getId());
    }
    
    @Test
    public void createRequestList_one_careUnit_one_sourceSystem(){
        RequestListFactoryImpl requestFactory = new RequestListFactoryImpl();
        FindContentType fc = createFindContent(RR_ID); 
        GetCareContactsType getCareDoc = createGetCareContacts(RR_ID, Collections.<String> emptyList());
        QueryObject queryObject = new QueryObject(fc, getCareDoc);
        FindContentResponseType findContentResponse = createFindContentResponse(TestProducerDb.TEST_LOGICAL_ADDRESS_1, TestProducerDb.TEST_LOGICAL_ADDRESS_1);
        findContentResponse.getEngagement().get(0).setSourceSystem(SOURCE_SYSTEM_1);
        findContentResponse.getEngagement().get(1).setSourceSystem(SOURCE_SYSTEM_1);
        List<Object[]> requestList =  requestFactory.createRequestList(queryObject, findContentResponse);
        assertEquals(1, requestList.size());
        assertEquals(SOURCE_SYSTEM_1, requestList.get(0)[0]);
        
        GetCareContactsType request = (GetCareContactsType)requestList.get(0)[1];
        assertEquals(RR_ID, request.getPatientId().getId());
    }

    @Ignore @Test // Not in use in this service domain
    public void createRequestList_timePeriod(){
        RequestListFactoryImpl requestFactory = new RequestListFactoryImpl();
        FindContentType fc = createFindContent(RR_ID);       
        GetCareContactsType getCareDoc = createGetCareContacts(RR_ID, Collections.<String> emptyList());
        DatePeriodType timePeriod = new DatePeriodType();
        timePeriod.setStart("20110101");
        timePeriod.setEnd("20110201");
        getCareDoc.setTimePeriod(timePeriod);
        QueryObject queryObject = new QueryObject(fc, getCareDoc);
        FindContentResponseType findContentResponse = createFindContentResponse(TestProducerDb.TEST_LOGICAL_ADDRESS_1, TestProducerDb.TEST_LOGICAL_ADDRESS_2);
        findContentResponse.getEngagement().get(0).setMostRecentContent("20110101120101");
        findContentResponse.getEngagement().get(1).setMostRecentContent("20110301120101");
        
        List<Object[]> requestList =  requestFactory.createRequestList(queryObject, findContentResponse);
        assertEquals(1, requestList.size());
        
        assertEquals(TestProducerDb.TEST_LOGICAL_ADDRESS_1, requestList.get(0)[0]);
        GetCareContactsType request = (GetCareContactsType)requestList.get(0)[1];
        assertEquals(RR_ID, request.getPatientId().getId());
    }
    
    private FindContentResponseType createFindContentResponse(String... logicalAddresses){
        FindContentResponseType findContentResponse = new FindContentResponseType();
        for(String logicalAddress: logicalAddresses){
            findContentResponse.getEngagement().add(createEngagement(logicalAddress, logicalAddress));    
        }
        return findContentResponse;
    }
    
    private FindContentType createFindContent(String id){
        FindContentType fc = new FindContentType();     
        fc.setRegisteredResidentIdentification(id);
        fc.setServiceDomain(SERVICE_DOMAIN);
        fc.setCategorization(CATEGORIZATION);
        return fc;
    }
    
    private GetCareContactsType createGetCareContacts(String id, List<String> careUnits){
        GetCareContactsType getCareContact = new GetCareContactsType();
        PersonIdType patientId = new PersonIdType();
        patientId.setId(RR_ID);
        getCareContact.setPatientId(patientId);
        getCareContact.getCareUnitHSAId().addAll(careUnits);
        return getCareContact;
    }
    
    private EngagementType createEngagement(String logicalAddress, String sourceSystem){
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        EngagementType engagement = new EngagementType();
        engagement.setCategorization(CATEGORIZATION);
        engagement.setServiceDomain(SERVICE_DOMAIN);
        engagement.setLogicalAddress(logicalAddress);
        engagement.setSourceSystem(sourceSystem);
        engagement.setMostRecentContent(df.format(new Date()));
        return engagement;
    }
}
