package se.skltp.aggregatingservices.riv.clinicalprocess.logistics.logistics.getcarecontacts;

import static se.skltp.aggregatingservices.data.TestDataDefines.*;

import lombok.extern.log4j.Log4j2;
import org.apache.cxf.message.MessageContentsList;
import org.springframework.stereotype.Service;
import riv.clinicalprocess.logistics.logistics.getcarecontactsresponder.v2.GetCareContactsResponseType;
import riv.clinicalprocess.logistics.logistics.getcarecontactsresponder.v2.GetCareContactsType;
import riv.clinicalprocess.logistics.logistics.v2.CareContactBodyType;
import riv.clinicalprocess.logistics.logistics.v2.CareContactType;
import riv.clinicalprocess.logistics.logistics.v2.HealthcareProfessionalType;
import riv.clinicalprocess.logistics.logistics.v2.OrgUnitType;
import riv.clinicalprocess.logistics.logistics.v2.PatientSummaryHeaderType;
import riv.clinicalprocess.logistics.logistics.v2.PersonIdType;
import se.skltp.aggregatingservices.data.TestDataDefines;
import se.skltp.aggregatingservices.data.TestDataGenerator;

@Log4j2
@Service
public class ServiceTestDataGenerator extends TestDataGenerator {

	@Override
	public String getPatientId(MessageContentsList messageContentsList) {
		GetCareContactsType request = (GetCareContactsType) messageContentsList.get(1);
		return request.getPatientId().getId();
	}

	@Override
	public Object createResponse(Object... responseItems) {
		log.info("Creating a response with {} items", responseItems.length);
		GetCareContactsResponseType response = new GetCareContactsResponseType();

		for (int i = 0; i < responseItems.length; i++) {
			response.getCareContact().add((CareContactType)responseItems[i]);
		}

		log.info("response.toString:" + response.toString());

		return response;
	}

	@Override
	public Object createResponseItem(String logicalAddress, String registeredResidentId, String businessObjectId, String time) {
		log.debug("Created ResponseItem for logical-address {}, registeredResidentId {} and businessObjectId {}",
				new Object[]{logicalAddress, registeredResidentId, businessObjectId});

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
		if(TEST_LOGICAL_ADDRESS_1.equals(logicalAddress)){
			orgUnit.setOrgUnitName("Vårdcentralen Kusten, Kärna");
		} else if(TEST_LOGICAL_ADDRESS_2.equals(logicalAddress)){
			orgUnit.setOrgUnitName("Vårdcentralen Molnet");
		} else {
			orgUnit.setOrgUnitName("Vårdcentralen Stacken");
		}
		header.getAccountableHealthcareProfessional().setHealthcareProfessionalOrgUnit(orgUnit);

		response.setCareContactHeader(header);

		CareContactBodyType body = new CareContactBodyType();
		body.setCareContactCode(0);
		body.setCareContactReason("reason");
		body.setCareContactStatus(0);

		OrgUnitType unit = new OrgUnitType();
		unit.setOrgUnitHSAId(logicalAddress);

		body.setCareContactOrgUnit(unit);
		response.setCareContactBody(body);

		return response;
	}

	public Object createRequest(String patientId, String sourceSystemHSAId){
		GetCareContactsType outcomeType = new GetCareContactsType();
		PersonIdType personIdType = new PersonIdType();
		personIdType.setId(patientId);
		personIdType.setType("1.2.752.129.2.1.3.1");
		outcomeType.setPatientId(personIdType);
		outcomeType.setSourceSystemHSAId(sourceSystemHSAId);

		return outcomeType;
	}
}
