package ibm.labs.kc.containermgr.kafka;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

class OnKafkaEnabledCondition implements Condition {

    @Override
      public boolean matches(
          ConditionContext context, 
          AnnotatedTypeMetadata metadata) {
          return (System.getenv("KAFKA_BROKERS") != null);
      }
  }