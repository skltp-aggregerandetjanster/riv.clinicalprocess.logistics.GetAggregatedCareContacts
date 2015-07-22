package se.skltp.aggregatingservices.riv.clinicalprocess.logistics.logistics.getaggregatedcarecontacts

import se.skltp.agp.testnonfunctional.TP04RobustnessAbstract

/**
 * Test VP:GetAggregatedCareContacts over 12 hours
 */
class TP04Robustness extends TP04RobustnessAbstract with CommonParameters {
  // override skltp-box with qa
  if (baseUrl.startsWith("http://33.33.33.33")) {
      baseUrl = "http://ine-sit-app03.sth.basefarm.net:9002/GetAggregatedCareContacts/service/v2"
  }
  setUp(setUpAbstract(serviceName, urn, responseElement, responseItem, baseUrl))
}
