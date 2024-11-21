package de.continentale.vu.demo_jsr352.listener;

import javax.batch.runtime.context.StepContext;

public class Jsr352LoggerUtils {

  /**
   * @param stepContext
   * @return "stepName: %s, executionId: %s, threadId: %s"
   */
  public static String toIdentity(final String label, final StepContext stepContext, Object... args) {
    final String label2 = String.format(label, args);
    return String.format(
        "%s stepName: %s, executionId: %s, threadId: %s",
        label2,
        stepContext.getStepName(),
        stepContext.getStepExecutionId(),
        Thread.currentThread().getId());
  }
}
