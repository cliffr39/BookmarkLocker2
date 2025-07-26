package com.bookmark.locker.ai;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava"
})
public final class AIDescriptionService_Factory implements Factory<AIDescriptionService> {
  @Override
  public AIDescriptionService get() {
    return newInstance();
  }

  public static AIDescriptionService_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static AIDescriptionService newInstance() {
    return new AIDescriptionService();
  }

  private static final class InstanceHolder {
    private static final AIDescriptionService_Factory INSTANCE = new AIDescriptionService_Factory();
  }
}
