package ibm.labs.kc.containermgr.rest;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

class OnKafkaDisabledCondition implements Condition {

    @Override
      public boolean matches(
          ConditionContext context, 
          AnnotatedTypeMetadata metadata) {
          return (System.getenv("KAFKA_BROKERS") == null);
      }
  }