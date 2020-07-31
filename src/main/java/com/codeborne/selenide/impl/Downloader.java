package com.codeborne.selenide.impl;

import com.codeborne.selenide.Config;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;

import static com.codeborne.selenide.impl.FileHelper.ensureFolderExists;

@ParametersAreNonnullByDefault
public class Downloader {
  private final Randomizer random;

  public Downloader() {
    this(new Randomizer());
  }

  public Downloader(Randomizer random) {
    this.random = random;
  }

  @CheckReturnValue
  @Nonnull
  public String randomFileName() {
    return random.text();
  }

  @CheckReturnValue
  @Nonnull
  public File prepareTargetFile(Config config, String fileName) {
    File uniqueFolder = new File(config.downloadsFolder(), random.text());
    if (uniqueFolder.exists()) {
      throw new IllegalStateException("Unbelievable! Unique folder already exists: " + uniqueFolder.getAbsolutePath());
    }
    ensureFolderExists(uniqueFolder);
    return new File(uniqueFolder, fileName);
  }
}
