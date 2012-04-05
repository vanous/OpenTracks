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
package com.google.android.apps.mytracks;

import com.google.android.apps.mytracks.services.ServiceUtils;
import com.google.android.apps.mytracks.services.TrackRecordingServiceConnection;
import com.google.android.maps.mytracks.R;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.test.ActivityInstrumentationTestCase2;

import java.io.File;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A unit test for {@link TrackDetailActivity}.
 *
 * @author Bartlomiej Niechwiej
 */
public class TrackDetailActivityTest extends ActivityInstrumentationTestCase2<TrackDetailActivity>{
  private SharedPreferences sharedPreferences;
  private TrackRecordingServiceConnection serviceConnection;

  public TrackDetailActivityTest() {
    super(TrackDetailActivity.class);
  }

  @Override
  protected void tearDown() throws Exception {
    clearSelectedAndRecordingTracks();
    waitForIdle();
    super.tearDown();
  }

  public void testInitialization_mainAction() {
    // Make sure we can start MyTracks and the activity doesn't start recording.
    assertInitialized();

    // Check if not recording.
    assertFalse(isRecording());
    assertEquals(-1, getRecordingTrackId());    
  }

  public void testInitialization_viewActionWithNoData() {
    // Simulate start with ACTION_VIEW intent.
    Intent startIntent = new Intent();
    startIntent.setAction(Intent.ACTION_VIEW);
    setActivityIntent(startIntent);

    assertInitialized();

    // Check if not recording.
    assertFalse(isRecording());
    assertEquals(-1, getRecordingTrackId());    
  }

  public void testInitialization_viewActionWithValidData() throws Exception {
    // Simulate start with ACTION_VIEW intent.
    Intent startIntent = new Intent();
    startIntent.setAction(Intent.ACTION_VIEW);
    Uri uri = Uri.fromFile(File.createTempFile("valid", ".gpx", getActivity().getFilesDir()));

    // TODO: Add a valid GPX.

    startIntent.setData(uri);
    setActivityIntent(startIntent);

    assertInitialized();

    // Check if not recording.
    assertFalse(isRecording());
    assertEquals(-1, getRecordingTrackId());
    
    // TODO: Finish this test.
  }

  public void testInitialization_viewActionWithInvalidData() throws Exception {
    // Simulate start with ACTION_VIEW intent.
    Intent startIntent = new Intent();
    startIntent.setAction(Intent.ACTION_VIEW);
    Uri uri = Uri.fromFile(File.createTempFile("invalid", ".gpx", getActivity().getFilesDir()));
    startIntent.setData(uri);
    setActivityIntent(startIntent);

    assertInitialized();

    // Check if not recording.
    assertFalse(isRecording());
    assertEquals(-1, getRecordingTrackId());
    
    // TODO: Finish this test.
  }

  private void assertInitialized() {
    assertNotNull(getActivity());

    serviceConnection = new TrackRecordingServiceConnection(getActivity(), null);
  }

  /**
   * Waits until the UI thread becomes idle.
   */
  private void waitForIdle() throws InterruptedException {
    // Note: We can't use getInstrumentation().waitForIdleSync() here.
    final Object semaphore = new Object();
    synchronized (semaphore) {
      final AtomicBoolean isIdle = new AtomicBoolean();
      getInstrumentation().waitForIdle(new Runnable() {
        @Override
        public void run() {
          synchronized (semaphore) {
            isIdle.set(true);
            semaphore.notify();
          }
        }
      });
      while (!isIdle.get()) {
        semaphore.wait();
      }
    }
  }

  /**
   * Clears {selected,recording}TrackId in the {@link #getSharedPreferences()}.
   */
  private void clearSelectedAndRecordingTracks() {
    Editor editor = getSharedPreferences().edit();
    editor.putLong(getActivity().getString(R.string.selected_track_key), -1);
    editor.putLong(getActivity().getString(R.string.recording_track_key), -1);

    editor.clear();
    editor.apply();
  }

  /**
   * Waits until the recording state changes to the given status.
   *
   * @param timeout the maximum time to wait, in milliseconds.
   * @param isRecording the final status to await.
   * @return the recording track ID.
   */
  private long awaitRecordingStatus(long timeout, boolean isRecording)
      throws TimeoutException, InterruptedException {
    long startTime = System.nanoTime();
    while (isRecording() != isRecording) {
      if (System.nanoTime() - startTime > timeout * 1000000) {
        throw new TimeoutException("Timeout while waiting for recording!");
      }
      Thread.sleep(20);
    }
    waitForIdle();
    assertEquals(isRecording, isRecording());
    return getRecordingTrackId();
  }

  private long getRecordingTrackId() {
    return getSharedPreferences().getLong(getActivity().getString(R.string.recording_track_key), -1);
  }

  private SharedPreferences getSharedPreferences() {
    if (sharedPreferences == null) {
      sharedPreferences = getActivity().getSharedPreferences(
          Constants.SETTINGS_NAME, Context.MODE_PRIVATE);
    }
    return sharedPreferences;
  }

  private boolean isRecording() {
    return ServiceUtils.isRecording(getActivity(),
        serviceConnection.getServiceIfBound(), getSharedPreferences());
  }
}
