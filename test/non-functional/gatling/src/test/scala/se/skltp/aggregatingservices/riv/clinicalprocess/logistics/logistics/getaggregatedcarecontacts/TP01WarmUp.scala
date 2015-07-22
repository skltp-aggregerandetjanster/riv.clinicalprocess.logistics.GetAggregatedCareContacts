package se.skltp.aggregatingservices.riv.clinicalprocess.logistics.logistics.getaggregatedcarecontacts

import se.skltp.agp.testnonfunctional.TP01WarmUpAbstract

/**
 * Simple requests to warm up service.
 */
class TP01WarmUp extends TP01WarmUpAbstract with CommonParameters {
  setUp(setUpAbstract(serviceName, urn, responseElement, responseItem, baseUrl))
}
