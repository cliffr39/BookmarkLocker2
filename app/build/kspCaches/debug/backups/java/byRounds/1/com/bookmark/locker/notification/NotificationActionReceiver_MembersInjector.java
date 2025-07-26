package com.bookmark.locker.notification;

import com.bookmark.locker.data.database.BookmarkDatabase;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class NotificationActionReceiver_MembersInjector implements MembersInjector<NotificationActionReceiver> {
  private final Provider<BookmarkDatabase> databaseProvider;

  private final Provider<NotificationHelper> notificationHelperProvider;

  public NotificationActionReceiver_MembersInjector(Provider<BookmarkDatabase> databaseProvider,
      Provider<NotificationHelper> notificationHelperProvider) {
    this.databaseProvider = databaseProvider;
    this.notificationHelperProvider = notificationHelperProvider;
  }

  public static MembersInjector<NotificationActionReceiver> create(
      Provider<BookmarkDatabase> databaseProvider,
      Provider<NotificationHelper> notificationHelperProvider) {
    return new NotificationActionReceiver_MembersInjector(databaseProvider, notificationHelperProvider);
  }

  @Override
  public void injectMembers(NotificationActionReceiver instance) {
    injectDatabase(instance, databaseProvider.get());
    injectNotificationHelper(instance, notificationHelperProvider.get());
  }

  @InjectedFieldSignature("com.bookmark.locker.notification.NotificationActionReceiver.database")
  public static void injectDatabase(NotificationActionReceiver instance,
      BookmarkDatabase database) {
    instance.database = database;
  }

  @InjectedFieldSignature("com.bookmark.locker.notification.NotificationActionReceiver.notificationHelper")
  public static void injectNotificationHelper(NotificationActionReceiver instance,
      NotificationHelper notificationHelper) {
    instance.notificationHelper = notificationHelper;
  }
}
