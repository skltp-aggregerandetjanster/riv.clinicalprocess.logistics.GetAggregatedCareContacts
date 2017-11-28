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
package se.skltp.aggregatingservices.riv.clinicalprocess.logistics.logistics.getcarecontacts.integrationtest;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import riv.clinicalprocess.logistics.logistics.getcarecontactsrequest.v3.GetCareContactsResponderInterface;
import riv.clinicalprocess.logistics.logistics.getcarecontactsresponder.v3.GetCareContactsResponseType;
import riv.clinicalprocess.logistics.logistics.getcarecontactsresponder.v3.GetCareContactsType;
import se.skltp.agp.test.producer.TestProducerDb;

@WebService(serviceName = "GetCareContactsResponderService", portName = "GetCareContactsResponderPort", targetNamespace = "urn:riv:clinicalprocess.logistics.logistics:GetCareContactsResponder:3:rivtabp21", name = "GetCareContactsInteraction")
public class CareContactTestProducer implements GetCareContactsResponderInterface {

    private static final Logger log = LoggerFactory.getLogger(CareContactTestProducer.class);

    private TestProducerDb testDb;

    public void setTestDb(TestProducerDb testDb) {
        this.testDb = testDb;
    }

    @Override
    @WebResult(name = "GetCareContactsResponse", targetNamespace = "urn:riv:clinicalprocess:logistics:logistics:GetCareContactsResponder:3", partName = "parameters")
    @WebMethod(operationName = "GetCareContacts", action = "urn:riv:ehr:patientsummary:GetCareContactsResponder:2:GetCareContacts")
    public GetCareContactsResponseType getCareContacts(
            @WebParam(partName = "LogicalAddress", name = "LogicalAddress", targetNamespace = "urn:riv:itintegration:registry:1", header = true) String logicalAddress,
            @WebParam(partName = "parameters", name = "GetCareContacts", targetNamespace = "urn:riv:clinicalprocess:logistics:logistics:GetCareContactsResponder:3") GetCareContactsType request) {
        log.info("### Virtual service for GetCareContacts call the source system with logical address: {} and patientId: {}", logicalAddress, request.getPatientId().getId());

        GetCareContactsResponseType response = (GetCareContactsResponseType)testDb.processRequest(logicalAddress, request.getPatientId().getId());
        if (response == null) {
            // Return an empty response object instead of null if nothing is found
            response = new GetCareContactsResponseType();
        }

        log.info("### Virtual service got {} documents in the reply from the source system with logical address: {} and patientId: {}", new Object[] {response.getCareContact().size(), logicalAddress, request.getPatientId().getId()});

        // We are done
        return response;
    }

}
