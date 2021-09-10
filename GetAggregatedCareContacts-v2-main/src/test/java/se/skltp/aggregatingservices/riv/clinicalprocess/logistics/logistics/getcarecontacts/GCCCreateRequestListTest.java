package se.skltp.aggregatingservices.riv.clinicalprocess.logistics.logistics.getcarecontacts;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import riv.clinicalprocess.logistics.logistics.getcarecontactsresponder.v2.GetCareContactsResponseType;
import se.skltp.aggregatingservices.api.AgpServiceFactory;
import se.skltp.aggregatingservices.tests.CreateRequestListTest;

@ExtendWith(SpringExtension.class)
public class GCCCreateRequestListTest extends CreateRequestListTest {

  private static GCCAgpServiceConfiguration configuration = new GCCAgpServiceConfiguration();
  private static AgpServiceFactory<GetCareContactsResponseType> agpServiceFactory = new GCCAgpServiceFactoryImpl();
  private static ServiceTestDataGenerator testDataGenerator = new ServiceTestDataGenerator();


  public GCCCreateRequestListTest() {
    super(testDataGenerator, agpServiceFactory, configuration);
  }
}