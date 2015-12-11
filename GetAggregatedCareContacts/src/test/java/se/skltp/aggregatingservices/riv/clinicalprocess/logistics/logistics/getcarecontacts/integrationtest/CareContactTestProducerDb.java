package se.skltp.aggregatingservices.riv.clinicalprocess.logistics.logistics.getcarecontacts.integrationtest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import riv.clinicalprocess.logistics.logistics.getcarecontactsresponder.v3.GetCareContactsResponseType;
import riv.clinicalprocess.logistics.logistics.v3.CVType;
import riv.clinicalprocess.logistics.logistics.v3.CareContactBodyType;
import riv.clinicalprocess.logistics.logistics.v3.CareContactType;
import riv.clinicalprocess.logistics.logistics.v3.HealthcareProfessionalType;
import riv.clinicalprocess.logistics.logistics.v3.OrgUnitType;
import riv.clinicalprocess.logistics.logistics.v3.PatientSummaryHeaderType;
import riv.clinicalprocess.logistics.logistics.v3.PersonIdType;
import se.skltp.agp.test.producer.TestProducerDb;

public class CareContactTestProducerDb extends TestProducerDb {

    private static final Logger log = LoggerFactory.getLogger(CareContactTestProducerDb.class);

    @Override
    public Object createResponse(Object... responseItems) {
        log.debug("Creates a response with {} items", responseItems);
        GetCareContactsResponseType response = new GetCareContactsResponseType();
        for (int i = 0; i < responseItems.length; i++) {
            response.getCareContact().add((CareContactType)responseItems[i]);
        }
        return response;
    }

    public static final String TEST_REASON_DEFAULT = "default reason";
    public static final String TEST_REASON_UPDATED = "updated reason";

    @Override
    public Object createResponseItem(String logicalAddress, String registeredResidentId, String businessObjectId, String time) {

        log.debug("Created one response item for logical-address {}, registeredResidentId {} and businessObjectId {}",
                new Object[] {logicalAddress, registeredResidentId, businessObjectId});

        CareContactType response = new CareContactType();
        PatientSummaryHeaderType header = new PatientSummaryHeaderType();
        PersonIdType patientId = new PersonIdType();
        patientId.setId(registeredResidentId);
        patientId.setType("1.2.752.129.2.1.3.1");
        header.setPatientId(patientId);
        header.setApprovedForPatient(true);
        header.setSourceSystemHSAId(logicalAddress);
        header.setDocumentId(businessObjectId);

        HealthcareProfessionalType author = new HealthcareProfessionalType();
        author.setHealthcareProfessionalCareGiverHSAId(logicalAddress);
        author.setAuthorTime(time);
        header.setAccountableHealthcareProfessional(author);
        header.setSourceSystemHSAId(logicalAddress);

        OrgUnitType orgUnit = new OrgUnitType();
        orgUnit.setOrgUnitHSAId(logicalAddress);
        if(TestProducerDb.TEST_LOGICAL_ADDRESS_1.equals(logicalAddress)){
            orgUnit.setOrgUnitName("Vårdcentralen Kusten, Kärna");
        } else if(TestProducerDb.TEST_LOGICAL_ADDRESS_2.equals(logicalAddress)){
            orgUnit.setOrgUnitName("Vårdcentralen Molnet");
        } else {
            orgUnit.setOrgUnitName("Vårdcentralen Stacken");
        }
        header.getAccountableHealthcareProfessional().setHealthcareProfessionalOrgUnit(orgUnit);

        response.setCareContactHeader(header);

        CareContactBodyType body = new CareContactBodyType();
        CVType cvtype = new CVType();

        body.setCareContactCode(cvtype);
        body.setCareContactReason("reason");
        body.setCareContactStatus(cvtype);

        OrgUnitType unit = new OrgUnitType();
        unit.setOrgUnitHSAId(logicalAddress);

        body.setCareContactOrgUnit(unit);
        response.setCareContactBody(body);
        return response;
    }
}
