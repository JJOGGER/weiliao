/*
 * Copyright (C) 2012 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.diamond.jogger.base.scan.camera.open;

import android.hardware.Camera;

/**
 * 开启相机,获取相机Camera实列
 */
public class OpenCameraInterface {

    /**
     * 打开指定id的相机
     */
    public static Camera open(int cameraId) {

        int numCameras = Camera.getNumberOfCameras();
        if (numCameras == 0) {
            return null;
        }
        boolean explicitRequest = cameraId >= 0;
        if (!explicitRequest) {
            int index = 0;
            while (index < numCameras) {
                Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
                Camera.getCameraInfo(index, cameraInfo);
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    break;
                }
                index++;
            }
            cameraId = index;
        }
        Camera camera;
        if (cameraId < numCameras) {
            camera = Camera.open(cameraId);
        } else {
            if (explicitRequest) {
                camera = null;
            } else {
                camera = Camera.open(0);
            }
        }
        return camera;
    }


    public static Camera open() {
        return open(-1);
    }

}
