package se.skltp.aggregatingservices.riv.clinicalprocess.logistics.getcarecontacts;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
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
     * 
     * 
     * Ett anrop görs per funnet sourceSystem med följande värden i anropet:
     * 
     * 1. logicalAddress = sourceSystem (systemadressering)
     * 2. subjectOfCareId = orginal-request.subjectOfCareId
     * 3. careUnitId = orginal-request.careUnitId
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

        FindContentResponseType eiResp = (FindContentResponseType)src;
        List<EngagementType> inEngagements = eiResp.getEngagement();

        log.info("Got {} hits in the engagement index", inEngagements.size());

        Set<String> sourceSystems = new HashSet<String>();

        for (EngagementType inEng : inEngagements) {

            // Filter
            if (isBetween(reqFrom, reqTo, inEng.getMostRecentContent())) {
                
                log.info("Add SS: {}", inEng.getSourceSystem());
                // Add source system
                sourceSystems.add(inEng.getSourceSystem());
            }
        }

        // Prepare the result of the transformation as a list of request-payloads, 
        // one payload for each unique logical-address (e.g. source system since we are using systemaddressing),
        // each payload built up as an object-array according to the JAX-WS signature for the method in the service interface
        List<Object[]> reqList = new ArrayList<Object[]>();

        for (String sourceSystem: sourceSystems) {

            log.info("Calling source system using logical address {} for subject of care id {}", sourceSystem, originalRequest.getPatientId().getId());

            GetCareContactsType request = new GetCareContactsType();
            request.setPatientId(originalRequest.getPatientId()); 
            
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


    // Not in use in this service domain
    protected boolean isBetween(Date from, Date to, String tsStr) {
        return true;
    }
    
/*    protected boolean isBetween(Date from, Date to, String tsStr) {
        try {
            log.debug("Is {} between {} and {}", new Object[]{tsStr, from, to});
            Date ts = dtf.parse(tsStr);
            if (from != null && from.after(ts)) return false;
            if (to != null && to.before(ts)) return false;
            return true;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }*/
}