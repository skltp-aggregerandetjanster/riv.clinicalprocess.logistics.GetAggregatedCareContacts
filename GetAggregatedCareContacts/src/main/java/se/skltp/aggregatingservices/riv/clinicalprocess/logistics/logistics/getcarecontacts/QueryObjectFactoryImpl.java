/**
 * Copyright (c) 2014 Inera AB, <http://inera.se/>
 *
 * This file is part of SKLTP.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package se.skltp.aggregatingservices.riv.clinicalprocess.logistics.logistics.getcarecontacts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soitoolkit.commons.mule.jaxb.JaxbUtil;
import org.w3c.dom.Node;

import riv.clinicalprocess.logistics.logistics.getcarecontactsresponder.v3.GetCareContactsType;
import se.skltp.agp.riv.itintegration.engagementindex.findcontentresponder.v1.FindContentType;
import se.skltp.agp.service.api.QueryObject;
import se.skltp.agp.service.api.QueryObjectFactory;

public class QueryObjectFactoryImpl implements QueryObjectFactory {

    private static final Logger log = LoggerFactory.getLogger(QueryObjectFactoryImpl.class);
    private static final JaxbUtil ju = new JaxbUtil(GetCareContactsType.class);

    private String eiServiceDomain;
    public void setEiServiceDomain(String eiServiceDomain) {
        this.eiServiceDomain = eiServiceDomain;
    }

    private String eiCategorization;
    public void setEiCategorization(String eiCategorization) {
        this.eiCategorization = eiCategorization;
    }


    /**
     * Transformerar GetCareContact request till EI FindContent request enligt:
     *
     * 1. patientId/id --> registeredResidentIdentification
     * 2. "riv:ehr:patientsummary" --> serviceDomain
     * 3. typeOfRequest --> categorization
     */
    @Override
    public QueryObject createQueryObject(Node node) {

        GetCareContactsType req = (GetCareContactsType)ju.unmarshal(node);

        log.debug("Transformed payload for pid: {}", req.getPatientId());

        FindContentType fc = new FindContentType();
        fc.setRegisteredResidentIdentification(req.getPatientId().getId());
        fc.setServiceDomain(eiServiceDomain);
        fc.setCategorization(eiCategorization);
        fc.setSourceSystem(req.getSourceSystemHSAId());
        QueryObject qo = new QueryObject(fc, req);

        return qo;
    }
}
