package se.skltp.aggregatingservices.riv.clinicalprocess.logistics.logistics.getcarecontacts;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soitoolkit.commons.mule.util.ThreadSafeSimpleDateFormat;

import riv.clinicalprocess.logistics.logistics.getcarecontactsresponder.v2.GetCareContactsType;
import se.skltp.agp.riv.itintegration.engagementindex.findcontentresponder.v1.FindContentResponseType;
import se.skltp.agp.riv.itintegration.engagementindex.v1.EngagementType;
import se.skltp.agp.service.api.QueryObject;
import se.skltp.agp.service.api.RequestListFactory;

public class RequestListFactoryImpl implements RequestListFactory {

    private static final Logger log = LoggerFactory.getLogger(RequestListFactoryImpl.class);
    private static final ThreadSafeSimpleDateFormat df = new ThreadSafeSimpleDateFormat("yyyyMMdd");

    /**
     * Filtrera svarsposter från i EI (ei-engagement) baserat parametrar i GetCareContact requestet (req).
     * Följande villkor måste vara sanna för att en svarspost från EI skall tas med i svaret:
     *
     * 1. req.timePeriod.start <= ei-engagement.mostRecentContent <= req.timePeriod.end
     *
     * Ett anrop görs per funnet sourceSystem med följande värden i anropet:
     *
     * 1. logicalAddress = sourceSystem (systemadressering)
     * 2. subjectOfCareId = original-request.subjectOfCareId
     * 3. careUnitId = original-request.careUnitId
     * 4. fromDate = original-request.fromDate
     * 5. toDate = original-request.toDate
     */
    @Override
    public List<Object[]> createRequestList(QueryObject qo, FindContentResponseType src) {

        GetCareContactsType originalRequest = (GetCareContactsType)qo.getExtraArg();
        
        String reqCareUnit = originalRequest.getSourceSystemHSAId();
        
        FindContentResponseType eiResp = (FindContentResponseType)src;
        List<EngagementType> inEngagements = eiResp.getEngagement();

        log.info("Got {} hits in the engagement index", inEngagements.size());

        Map<String, List<String>> sourceSystem_pdlUnitList_map = new HashMap<String, List<String>>();
        
        for (EngagementType inEng : inEngagements) {
            if (isPartOf(reqCareUnit, inEng.getLogicalAddress())) {
                // Add pdlUnit to source system
                log.debug("Add SS: {} for PDL unit: {}", inEng.getSourceSystem(), inEng.getLogicalAddress());
                addPdlUnitToSourceSystem(sourceSystem_pdlUnitList_map, inEng.getSourceSystem(), inEng.getLogicalAddress());
            }
        }

        // Prepare the result of the transformation as a list of request-payloads,
        // one payload for each unique logical-address (e.g. source system since we are using system addressing),
        // each payload built up as an object-array according to the JAX-WS signature for the method in the service interface
        List<Object[]> reqList = new ArrayList<Object[]>();

        for (Entry<String, List<String>> entry : sourceSystem_pdlUnitList_map.entrySet()) {
            String sourceSystem = entry.getKey();

            if (log.isInfoEnabled()) {
                log.info("Calling source system using logical address {} for subject of care id {}", 
                        sourceSystem, originalRequest.getPatientId().getId());
            }
            GetCareContactsType request = originalRequest;
            Object[] reqArr = new Object[] {sourceSystem, request};
            reqList.add(reqArr);
        }
        
        log.debug("Transformed payload: {}", reqList);
        return reqList;
    }

    protected Date parseTs(String ts) {
        try {
            if (ts == null || ts.length() == 0) {
                return null;
            } else {
                return df.parse(ts);
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    boolean isPartOf(String careUnitId, String careUnit) {
        log.debug("Check presence of {} in {}", careUnit, careUnitId);
        if (StringUtils.isBlank(careUnitId)) return true;
        return careUnitId.equals(careUnit);
    }

    void addPdlUnitToSourceSystem(Map<String, List<String>> sourceSystem_pdlUnitList_map, String sourceSystem, String pdlUnitId) {
        List<String> careUnitList = sourceSystem_pdlUnitList_map.get(sourceSystem);
        if (careUnitList == null) {
            careUnitList = new ArrayList<String>();
            sourceSystem_pdlUnitList_map.put(sourceSystem, careUnitList);
        }
        careUnitList.add(pdlUnitId);
    }
    
    // Not in use in this service domain
    protected boolean isBetween(Date from, Date to, String tsStr) {
        return true;
    }
}
