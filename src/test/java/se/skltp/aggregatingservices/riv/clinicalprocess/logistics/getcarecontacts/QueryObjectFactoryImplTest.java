package se.skltp.aggregatingservices.riv.clinicalprocess.logistics.getcarecontacts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

import se.riv.clinicalprocess.logistics.getcarecontactsresponder.v2.GetCareContactsType;
import se.riv.clinicalprocess.logistics.getcarecontactsresponder.v2.ObjectFactory;
import se.riv.clinicalprocess.logistics.v2.PersonIdType;
import se.skltp.agp.service.api.QueryObject;

public class QueryObjectFactoryImplTest {

    private static final String CATEGORIZATION = "voo";
    private static final String SERVICE_DOMAIN = "riv:clinicalprocess:healthcond:description";
    private static final String RR_ID = "1212121212"; 
    private static final String SOURCE_SYSTEM = "SS1"; 

    QueryObjectFactoryImpl objectFactory = new QueryObjectFactoryImpl();
    
    @Before
    public void setup(){
        objectFactory.setEiCategorization(CATEGORIZATION);
        objectFactory.setEiServiceDomain(SERVICE_DOMAIN);
    }
    
    @Test
    public void createQueryObject() throws Exception{
        GetCareContactsType getCareDoc = new GetCareContactsType();
        PersonIdType patientId = new PersonIdType();
        patientId.setId(RR_ID);
        getCareDoc.setPatientId(patientId);
        
        Document doc = createDocument(getCareDoc);
        QueryObject queryObj = objectFactory.createQueryObject(doc);
        
        assertNotNull(queryObj.getFindContent());
        assertEquals(SERVICE_DOMAIN, queryObj.getFindContent().getServiceDomain());
        assertEquals(CATEGORIZATION, queryObj.getFindContent().getCategorization());
        assertEquals(RR_ID, queryObj.getFindContent().getRegisteredResidentIdentification());
        assertNull(queryObj.getFindContent().getSourceSystem());
    }
    
    @Test
    public void createQueryObject_with_source_system() throws Exception{
        GetCareContactsType getCareDoc = new GetCareContactsType();
        PersonIdType patientId = new PersonIdType();
        patientId.setId(RR_ID);
        getCareDoc.setPatientId(patientId);
        getCareDoc.setSourceSystemHSAId(SOURCE_SYSTEM);
        
        Document doc = createDocument(getCareDoc);
        QueryObject queryObj = objectFactory.createQueryObject(doc);
        
        assertNotNull(queryObj.getFindContent());
        assertEquals(SOURCE_SYSTEM, queryObj.getFindContent().getSourceSystem());   
    }
    
    private Document createDocument(GetCareContactsType getCareContact) throws Exception {
        ObjectFactory of = new ObjectFactory();
        JAXBContext jaxbContext = JAXBContext.newInstance(GetCareContactsType.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        Document doc = dbf.newDocumentBuilder().newDocument();
        marshaller.marshal(of.createGetCareContacts(getCareContact), doc);
        return doc;
    }
   
}
