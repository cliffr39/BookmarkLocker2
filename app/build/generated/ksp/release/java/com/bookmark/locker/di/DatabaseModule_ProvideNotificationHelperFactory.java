package com.bookmark.locker.di;

import android.content.Context;
import com.bookmark.locker.notification.NotificationHelper;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class DatabaseModule_ProvideNotificationHelperFactory implements Factory<NotificationHelper> {
  private final Provider<Context> contextProvider;

  public DatabaseModule_ProvideNotificationHelperFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public NotificationHelper get() {
    return provideNotificationHelper(contextProvider.get());
  }

  public static DatabaseModule_ProvideNotificationHelperFactory create(
      Provider<Context> contextProvider) {
    return new DatabaseModule_ProvideNotificationHelperFactory(contextProvider);
  }

  public static NotificationHelper provideNotificationHelper(Context context) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideNotificationHelper(context));
  }
}
