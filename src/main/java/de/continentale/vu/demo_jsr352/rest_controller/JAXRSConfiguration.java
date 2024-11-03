package de.continentale.vu.demo_jsr352.rest_controller;

import org.slf4j.Logger;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.Set;

import static org.slf4j.LoggerFactory.getLogger;

@ApplicationPath("rest")
public class JAXRSConfiguration extends Application {

  private static final Logger logger = getLogger(JAXRSConfiguration.class);

  @Override
  public Set<Class<?>> getClasses() {
    final Set<Class<?>> classes = Set.of(BatchControllerRestBean.class);
    logger.debug("Classes used for REST: {}",classes);

    return classes;
  }
}
