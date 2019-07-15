/*
 * Copyright 2010 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.android.apps.mytracks.content;

import com.google.android.apps.mytracks.content.Waypoint.WaypointType;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * A request for the service to create a waypoint at the current location.
 * 
 * @author Sandor Dornbush
 */
public class WaypointCreationRequest implements Parcelable {

  private WaypointType type;
  // true if this marker contains the track statistics
  private boolean isTrackStatistics;
  private String name;
  private String category;
  private String description;
  private String iconUrl;
  private String photoUrl;

  public final static WaypointCreationRequest DEFAULT_WAYPOINT = new WaypointCreationRequest(
      WaypointType.WAYPOINT, false);
  public final static WaypointCreationRequest DEFAULT_STATISTICS = new WaypointCreationRequest(
      WaypointType.STATISTICS, false);
  public final static WaypointCreationRequest DEFAULT_START_TRACK = new WaypointCreationRequest(
      WaypointType.STATISTICS, true);

  private WaypointCreationRequest(WaypointType type, boolean isTrackStatistics) {
    this(type, isTrackStatistics, null, null, null, null, null);
  }

  public WaypointCreationRequest(WaypointType type, boolean isTrackStatistics, String name,
      String category, String description, String iconUrl, String photoUrl) {
    this.type = type;
    this.isTrackStatistics = isTrackStatistics;
    this.name = name;
    this.category = category;
    this.description = description;
    this.iconUrl = iconUrl;
    this.photoUrl = photoUrl;
  }

  public static class Creator implements Parcelable.Creator<WaypointCreationRequest> {

    @Override
    public WaypointCreationRequest createFromParcel(Parcel source) {
      return new WaypointCreationRequest(
          WaypointType.values()[source.readInt()],
          source.readByte() == 1,
          source.readString(),
          source.readString(),
          source.readString(),
          source.readString(),
          source.readString());
    }

    public WaypointCreationRequest[] newArray(int size) {
      return new WaypointCreationRequest[size];
    }
  }

  public static final Creator CREATOR = new Creator();

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel parcel, int arg1) {
    parcel.writeInt(type.ordinal());
    parcel.writeByte((byte) (isTrackStatistics ? 1 : 0));
    parcel.writeString(name);
    parcel.writeString(category);
    parcel.writeString(description);
    parcel.writeString(iconUrl);
    parcel.writeString(photoUrl);
  }

  public WaypointType getType() {
    return type;
  }

  public boolean isTrackStatistics() {
    return isTrackStatistics;
  }

  public String getName() {
    return name;
  }

  public String getCategory() {
    return category;
  }

  public String getDescription() {
    return description;
  }

  public String getIconUrl() {
    return iconUrl;
  }
  
  public String getPhotoUrl() {
    return photoUrl;
  }
}