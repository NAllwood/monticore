/*
 * ******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2015, MontiCore, All rights reserved.
 *
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

package de.monticore.io.paths;

import org.apache.commons.io.FilenameUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import static com.google.common.base.Preconditions.checkState;

final class ModelCoordinateImpl implements ModelCoordinate {
  
  /**
   * The relative location of the model.<br>
   * Example: {@code src/main/grammars/de/mc/statechart.mc}
   */
  private Path location;
  
  /**
   * The qualified path of the model. It is implied that any directories
   * preceding the file name are part of the model package.<br>
   * Example: {@code de/mc/statechart.mc}
   */
  private Path qualifiedPath;
  
  @Override
  public boolean hasLocation() {
    return this.location != null;
  }
  
  @Override
  public boolean isQualified() {
    return this.qualifiedPath != null;
  }
  
  @Override
  public void setLocation(Path location) {
    checkState(this.location == null, "The location of the ModelCoordinate was already set.");
    this.location = location;
  }
  
  @Override
  public void setQualifiedPath(Path qualifiedPath) {
    checkState(this.qualifiedPath == null,
        "The qualified path of the ModelCoordinate was already set.");
    this.qualifiedPath = qualifiedPath;
  }
  
  @Override
  public boolean exists() {
    checkState(location != null, "The location of the ModelCoordinate wasn't set.");
    return location != null;
  }
  
  @Override
  public String getBaseName() {
    if (hasLocation()) {
      return FilenameUtils.getBaseName(location.toString());
    }
    else {
      return FilenameUtils.getBaseName(qualifiedPath.toString());
    }
  }
  
  @Override
  public String getExtension() {
    if (hasLocation()) {
      return FilenameUtils.getExtension(location.toString());
    }
    else {
      return FilenameUtils.getExtension(qualifiedPath.toString());
    }
  }
  
  @Override
  public Path getLocation() {
    checkState(location != null, "The location of the ModelCoordinate wasn't set.");
    return this.location;
  }
  
  @Override
  public String getName() {
    if (hasLocation()) {
      return FilenameUtils.getName(location.toString());
    }
    else {
      return FilenameUtils.getName(qualifiedPath.toString());
    }
  }
  
  @Override
  public Path getPackagePath() {
    checkState(qualifiedPath != null, "The qualified path of the ModelCoordinate wasn't set.");
    return Paths.get(FilenameUtils.getPathNoEndSeparator(qualifiedPath.toString()));
  }
  
  @Override
  public Path getParentDirectoryPath() {
    checkState(qualifiedPath != null, "The qualified path of the ModelCoordinate wasn't set.");
    
    if (location.endsWith(".jar")) {
      return location.getParent();
    }
    else {
      int endIndex = location.getNameCount() - qualifiedPath.getNameCount();
      return location.subpath(0, endIndex).toAbsolutePath();
    }
  }
  
  @Override
  public String getQualifiedBaseName() {
    checkState(qualifiedPath != null, "The qualified path of the ModelCoordinate wasn't set.");
    // regex for replacing all file separators with dots
    return getQualifiedBasePath().toString().replaceAll("\\\\|/", ".");
  }
  
  @Override
  public Path getQualifiedBasePath() {
    checkState(qualifiedPath != null, "The qualified path of the ModelCoordinate wasn't set.");
    return getPackagePath().resolve(getBaseName());
  }
  
  @Override
  public Path getQualifiedPath() {
    checkState(qualifiedPath != null, "The qualified path of the ModelCoordinate wasn't set.");
    return this.qualifiedPath;
  }
  
  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof ModelCoordinate)) {
      return false;
    }
    ModelCoordinate coordinate = (ModelCoordinate) obj;
    return Objects.equals(location, coordinate.getLocation())
        && Objects.equals(qualifiedPath, coordinate.getQualifiedPath());
  }
  
  @Override
  public int hashCode() {
    int hashCode = 0;
    hashCode += location != null ? location.hashCode() : 0;
    hashCode += qualifiedPath != null ? qualifiedPath.hashCode() : 0;
    return hashCode;
  }
  
  @Override
  public String toString() {
    return "Location: " + Objects.toString(location) + " Package: "
        + Objects.toString(qualifiedPath);
  }
  
}
