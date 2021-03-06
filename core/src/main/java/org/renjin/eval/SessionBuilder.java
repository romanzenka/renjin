/**
 * Renjin : JVM-based interpreter for the R language for the statistical analysis
 * Copyright © 2010-2016 BeDataDriven Groep B.V. and contributors
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, a copy is available at
 * https://www.gnu.org/licenses/gpl-2.0.txt
 */
package org.renjin.eval;

import org.apache.commons.vfs2.FileSystemManager;
import org.renjin.compiler.pipeline.SimpleVectorPipeliner;
import org.renjin.compiler.pipeline.VectorPipeliner;
import org.renjin.primitives.packaging.ClasspathPackageLoader;
import org.renjin.primitives.packaging.PackageLoader;
import org.renjin.repackaged.guava.collect.Lists;
import org.renjin.sexp.FunctionCall;
import org.renjin.sexp.Symbol;
import org.renjin.util.FileSystemUtils;

import java.util.List;

public class SessionBuilder {

  private boolean loadBasePackage = true;
  private List<String> packagesToLoad = Lists.newArrayList();

  private FileSystemManager fileSystemManager;
  private PackageLoader packageLoader;
  private VectorPipeliner vectorPipeliner;
  private ClassLoader classLoader;
 
  public SessionBuilder() {

  }

  /**
   *
   * @param fsm
   * @return
   * @deprecated see {@link #setFileSystemManager(FileSystemManager)}
   */
  @Deprecated
  public SessionBuilder withFileSystemManager(FileSystemManager fsm) {
    return setFileSystemManager(fsm);
  }
  
  /**
   * Disables loading of the R-Language portions of the base package:
   * primitives will still be available but none of the functions in the base 
   * package will be loaded. 
   */
  public SessionBuilder withoutBasePackage() {
    this.loadBasePackage = false;
    return this;
  }
  
  /**
   * Loads the default packages for R 3.3.2 (stats, graphics, grDevices, utils, datasets, methods)
   */
  public SessionBuilder withDefaultPackages() {
    packagesToLoad = Session.DEFAULT_PACKAGES;
    return this;
  }

  /**
   * Sets the {@link FileSystemManager} used to implement calls to R's builtin functions.
   *
   * <p>By default, the new {@code Session} will use {@link FileSystemUtils#getMinimalFileSystemManager()},
   * but a custom {@code FileSystemManager} can be provided to limit or customize the access of R scripts
   * to the filesystem.</p>
   *
   * @param fileSystemManager
   */
  public SessionBuilder setFileSystemManager(FileSystemManager fileSystemManager) {
    this.fileSystemManager = fileSystemManager;
    return this;
  }

  /**
   * Sets the {@link PackageLoader} implementation to be used for loading R packages by the new {@code Session}.
   *
   * <p>By default, the new {@code Session} will use a {@link ClasspathPackageLoader} with the provided
   * {@code ClassPathLoader}, or this class' {@code ClassPathLoader} if none is provided.
   *
   * <p>If new {@code Session} should load packages from remote repositories on demand, you can use the
   * {@code AetherPackageLoader} from the {@code renjin-aether-package-loader} module.
   *
   */
  public SessionBuilder setPackageLoader(PackageLoader packageLoader) {
    this.packageLoader = packageLoader;
    return this;
  }

  /**
   * Sets the {@link VectorPipeliner} implementation to use.
   *
   * <p>Note that this method will change in the near future!</p>
   *
   */
  public SessionBuilder setVectorPipeliner(VectorPipeliner vectorPipeliner) {
    this.vectorPipeliner = vectorPipeliner;
    return this;
  }

  /**
   * Sets the {@link ClassLoader} to use to resolve JVM classes by the {@code import()} builtin.
   */
  public SessionBuilder setClassLoader(ClassLoader classLoader) {
    this.classLoader = classLoader;
    return this;
  }

  /**
   * Binds a Renjin interface to its implementation
   * @deprecated Use {@link #setFileSystemManager(FileSystemManager)}
   */
  @Deprecated
  public <T> SessionBuilder bind(Class<T> clazz, T instance) {
    if(clazz.equals(FileSystemManager.class)) {
      setFileSystemManager((FileSystemManager) instance);
    } else if(clazz.equals(PackageLoader.class)) {
      setPackageLoader((PackageLoader) instance);
    } else if(clazz.equals(VectorPipeliner.class)) {
      setVectorPipeliner((VectorPipeliner) instance);
    } else if(clazz.equals(ClassLoader.class)) {
      setClassLoader((ClassLoader) instance);
    } else {
      // Do nothing: this was the behavior of the previous
      // implementation.
    }
    return this;
  }

  public Session build() {
    try {

      if(fileSystemManager == null) {
        fileSystemManager = FileSystemUtils.getMinimalFileSystemManager();
      }

      if(classLoader == null) {
        classLoader = getClass().getClassLoader();
      }

      if(vectorPipeliner == null) {
        vectorPipeliner = new SimpleVectorPipeliner();
      }

      if(packageLoader == null) {
        packageLoader = new ClasspathPackageLoader(classLoader);
      }

      Session session = new Session(fileSystemManager, classLoader, packageLoader, vectorPipeliner);
      if(loadBasePackage) {
        session.getTopLevelContext().init();
      }
      for(String packageToLoad : packagesToLoad) {
        session.getTopLevelContext().evaluate(FunctionCall.newCall(Symbol.get("library"),
            Symbol.get(packageToLoad)));
      }
      return session;
    } catch(Exception e) {
      throw new RuntimeException(e);
    }
  }
  
  public static Session buildDefault() {
    return new SessionBuilder().build();
  }
}
