package se.skltp.aggregatingservices.riv.clinicalprocess.logistics.logistics.getaggregatedcarecontacts

trait CommonParameters {
  val serviceName:String     = "CareContacts"
  val urn:String             = "urn:riv:clinicalprocess:logistics:logistics:GetCareContactsResponder:3"
  val responseElement:String = "GetCareContactsResponse"
  val responseItem:String    = "careContact"
  var baseUrl:String         = if (System.getProperty("baseUrl") != null && !System.getProperty("baseUrl").isEmpty()) {
                                   System.getProperty("baseUrl")
                               } else {
                                   "http://33.33.33.33:8081/GetAggregatedCareContacts/service/v3"
                               }
}
