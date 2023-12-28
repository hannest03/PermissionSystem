package it.smallcode.permissionsystem;

import it.smallcode.permissionsystem.services.registry.Service;
import it.smallcode.permissionsystem.services.registry.ServiceRegistry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServiceRegistryTest {

  private ServiceRegistry serviceRegistry;

  @BeforeEach
  public void setUp() {
    serviceRegistry = new ServiceRegistry();
  }

  @Test
  public void testRegisterAndGet() {
    interface Test extends Service {

    }

    class TestService implements Test {

    }
    
    Assertions.assertNull(serviceRegistry.getService(Test.class));

    TestService service = new TestService();
    serviceRegistry.registerService(Test.class, service);

    Assertions.assertEquals(service, serviceRegistry.getService(Test.class));
  }

}
