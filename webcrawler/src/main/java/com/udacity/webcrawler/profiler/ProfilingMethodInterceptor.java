package com.udacity.webcrawler.profiler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

/**
 * A method interceptor that checks whether {@link Method}s are annotated with the {@link Profiled}
 * annotation. If they are, the method interceptor records how long the method invocation took.
 */
final class ProfilingMethodInterceptor implements InvocationHandler {

  private final Clock clock;
  private final ProfilingState profilingState;
  private final Object delegate;

  ProfilingMethodInterceptor(Clock clock, Object delegate, ProfilingState state) {
    this.clock = Objects.requireNonNull(clock);
    this.delegate = delegate;
    this.profilingState = state;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    // TODO: This method interceptor should inspect the called method to see if it is a profiled
    //       method. For profiled methods, the interceptor should record the start time, then
    //       invoke the method using the object that is being profiled. Finally, for profiled
    //       methods, the interceptor should record how long the method call took, using the
    //       ProfilingState methods.
    boolean isGetAnnotation = method.getAnnotation(Profiled.class) != null;
    Instant start = clock.instant();
    Object res;
    try {
      res = method.invoke(delegate, args);
    } catch (InvocationTargetException ex) {
      throw ex.getTargetException();
    } catch (IllegalAccessException ex){
      throw new RuntimeException(ex);
    } finally {
      if (start != null) {
        Instant end = clock.instant();
        Duration duration = Duration.between(start, end);
        if (isGetAnnotation == true) {
          profilingState.record(delegate.getClass(), method, duration);
        }
      }
    }
    return res;
  }
}