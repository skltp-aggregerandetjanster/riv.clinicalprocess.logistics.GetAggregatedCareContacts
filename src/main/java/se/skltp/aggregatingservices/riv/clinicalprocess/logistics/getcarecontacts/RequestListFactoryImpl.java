package se.skltp.aggregatingservices.riv.clinicalprocess.logistics.getcarecontacts;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soitoolkit.commons.mule.util.ThreadSafeSimpleDateFormat;

import se.riv.clinicalprocess.logistics.getcarecontactsresponder.v2.GetCareContactsType;
import se.skltp.agp.riv.itintegration.engagementindex.findcontentresponder.v1.FindContentResponseType;
import se.skltp.agp.riv.itintegration.engagementindex.v1.EngagementType;
import se.skltp.agp.service.api.QueryObject;
import se.skltp.agp.service.api.RequestListFactory;

public class RequestListFactoryImpl implements RequestListFactory {

    private static final Logger log = LoggerFactory.getLogger(RequestListFactoryImpl.class);
    private static final ThreadSafeSimpleDateFormat dtf = new ThreadSafeSimpleDateFormat("yyyyMMddHHmmss");
    private static final ThreadSafeSimpleDateFormat df = new ThreadSafeSimpleDateFormat("yyyyMMdd");
    
    /**
     * Filtrera svarsposter från i EI (ei-engagement) baserat parametrar i GetCareContact requestet (req).
     * Följande villkor måste vara sanna för att en svarspost från EI skall tas med i svaret:
     * 
     * 1. req.timePeriod.start <= ei-engagement.mostRecentContent <= req.timePeriod.end
     * 2. req.careUnitHSAid.size == 0 or req.careUnitHSAid.contains(ei-engagement.logicalAddress)
     * 
     * Svarsposter från EI som passerat filtreringen grupperas på fältet sourceSystem samt postens fält logicalAddress (= PDL-enhet) samlas i listan careUnitId per varje sourceSystem
     * 
     * Ett anrop görs per funnet sourceSystem med följande värden i anropet:
     * 
     * 1. logicalAddress = sourceSystem (systemadressering)
     * 2. subjectOfCareId = orginal-request.subjectOfCareId
     * 3. careUnitId = listan av PDL-enheter som returnerats från EI för aktuellt source system)
     * 4. fromDate = orginal-request.fromDate
     * 5. toDate = orginal-request.toDate
     */
    @Override
    public List<Object[]> createRequestList(QueryObject qo, FindContentResponseType src) {

        GetCareContactsType originalRequest = (GetCareContactsType)qo.getExtraArg();
        Date reqFrom = null;
        Date reqTo = null;

        if(originalRequest.getTimePeriod() != null){
            reqFrom = parseTs(originalRequest.getTimePeriod().getStart());
            reqTo   = parseTs(originalRequest.getTimePeriod().getEnd());	
        }

        List<String> reqCareUnitList = originalRequest.getCareUnitHSAid();

        FindContentResponseType eiResp = (FindContentResponseType)src;
        List<EngagementType> inEngagements = eiResp.getEngagement();

        System.err.println("Got " +  inEngagements.size() + " hits in the engagement index");
        log.info("Got {} hits in the engagement index", inEngagements.size());

        Map<String, Set<String>> sourceSystem_pdlUnitList_map = new HashMap<String, Set<String>>();

        for (EngagementType inEng : inEngagements) {

            // Filter
            if (isBetween(reqFrom, reqTo, inEng.getMostRecentContent()) &&
                    isPartOf(reqCareUnitList, inEng.getLogicalAddress())) {

                System.err.println("Add SS: " + inEng.getSourceSystem() + " for PDL unit: " + inEng.getLogicalAddress());
                // Add pdlUnit to source system
                addPdlUnitToSourceSystem(sourceSystem_pdlUnitList_map, inEng.getSourceSystem(), inEng.getLogicalAddress());
            }
        }

        // Prepare the result of the transformation as a list of request-payloads, 
        // one payload for each unique logical-address (e.g. source system since we are using systemaddressing),
        // each payload built up as an object-array according to the JAX-WS signature for the method in the service interface
        List<Object[]> reqList = new ArrayList<Object[]>();

        for (Entry<String, Set<String>> entry : sourceSystem_pdlUnitList_map.entrySet()) {

            String sourceSystem = entry.getKey();
            List<String> careUnitList = new ArrayList<String>(entry.getValue().size());
            careUnitList.addAll(entry.getValue());

            System.err.println("Calling source system using logical address " + sourceSystem + " for subject of care id: " + originalRequest.getPatientId().getId());
            if (log.isInfoEnabled()) log.info("Calling source system using logical address {} for subject of care id {}", sourceSystem, originalRequest.getPatientId().getId());

            GetCareContactsType request = new GetCareContactsType();
            request.setPatientId(originalRequest.getPatientId()); 
            
            // Don't set by PDL-enhet (logicalAddress)
            if(originalRequest.getCareUnitHSAid() != null && originalRequest.getCareUnitHSAid().size() > 0){
                request.getCareUnitHSAid().addAll(originalRequest.getCareUnitHSAid()); 
            }
            
            request.setTimePeriod(originalRequest.getTimePeriod());

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

    protected boolean isBetween(Date from, Date to, String tsStr) {
        try {
            System.err.println("Is " + tsStr + " between " + from + " and " + to);
            Date ts = dtf.parse(tsStr);
            if (from != null && from.after(ts)) return false;
            if (to != null && to.before(ts)) return false;
            return true;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    protected boolean isPartOf(List<String> careUnitIdList, String careUnit) {

        System.err.println("Check presence of " + careUnit + " in " + careUnitIdList);

        if (careUnitIdList == null || careUnitIdList.size() == 0) return true;

        return careUnitIdList.contains(careUnit);
    }

    protected void addPdlUnitToSourceSystem(Map<String, Set<String>> sourceSystem_pdlUnitList_map, String sourceSystem, String pdlUnitId) {
        Set<String> careUnitList = sourceSystem_pdlUnitList_map.get(sourceSystem);
        if (careUnitList == null) {
            careUnitList = new HashSet<String>();
            sourceSystem_pdlUnitList_map.put(sourceSystem, careUnitList);
        } 
        careUnitList.add(pdlUnitId);
    }
}