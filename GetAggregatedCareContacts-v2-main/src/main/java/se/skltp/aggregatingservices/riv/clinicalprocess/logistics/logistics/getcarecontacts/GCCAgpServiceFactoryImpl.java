package se.skltp.aggregatingservices.riv.clinicalprocess.logistics.logistics.getcarecontacts;

import java.util.List;
import lombok.extern.log4j.Log4j2;
import riv.clinicalprocess.logistics.logistics.getcarecontactsresponder.v2.GetCareContactsResponseType;
import riv.clinicalprocess.logistics.logistics.getcarecontactsresponder.v2.GetCareContactsType;
import se.skltp.aggregatingservices.AgServiceFactoryBase;

@Log4j2
public class GCCAgpServiceFactoryImpl extends
    AgServiceFactoryBase<GetCareContactsType, GetCareContactsResponseType> {

  @Override
  public String getPatientId(GetCareContactsType queryObject) {
    return queryObject.getPatientId().getId();
  }

  @Override
  public String getSourceSystemHsaId(GetCareContactsType queryObject) {
    return queryObject.getSourceSystemHSAId();
  }

  @Override
  public GetCareContactsResponseType aggregateResponse(List<GetCareContactsResponseType> aggregatedResponseList) {

    GetCareContactsResponseType aggregatedResponse = new GetCareContactsResponseType();

    for (Object object : aggregatedResponseList) {
      GetCareContactsResponseType response = (GetCareContactsResponseType) object;
      aggregatedResponse.getCareContact().addAll(response.getCareContact());
    }

    log.info("Returning {} aggregated carecontacts v2", aggregatedResponse.getCareContact().size());

    return aggregatedResponse;
  }
}

