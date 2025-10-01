package com.pharma.taskmanager.di;

import com.pharma.taskmanager.utils.DateTimeUtils;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class UtilityModule_ProvideDateTimeUtilsFactory implements Factory<DateTimeUtils> {
  @Override
  public DateTimeUtils get() {
    return provideDateTimeUtils();
  }

  public static UtilityModule_ProvideDateTimeUtilsFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static DateTimeUtils provideDateTimeUtils() {
    return Preconditions.checkNotNullFromProvides(UtilityModule.INSTANCE.provideDateTimeUtils());
  }

  private static final class InstanceHolder {
    private static final UtilityModule_ProvideDateTimeUtilsFactory INSTANCE = new UtilityModule_ProvideDateTimeUtilsFactory();
  }
}
