package com.tym.shortvideo.filter.helper.type;

import android.hardware.Camera;

import com.tym.shortvideo.recodrender.RecordManager;
import com.tym.shortvideo.utils.CameraUtils;
import com.tym.shortvideo.utils.Size;

import java.nio.FloatBuffer;

/**
 * Created by cain on 17-7-26.
 */

public class TextureRotationUtils {

    // 摄像头是否倒置，主要是应对Nexus 5X (bullhead) 的后置摄像头图像倒置的问题
    private static boolean mBackReverse = false;

    public static final int CoordsPerVertex = 2;

    public static final float CubeVertices[] = {
            -1.0f, -1.0f,  // 0 bottom left
            1.0f, -1.0f,  // 1 bottom right
            -1.0f, 1.0f,  // 2 top left
            1.0f, 1.0f,  // 3 top right
    };

    public static void setRatio_1_1(boolean b, Size size) {
        if (b) {
            TextureVertices[0] = 0.0f;
            TextureVertices[1] = 0.0f;

            TextureVertices[2] = 1.0f;
            TextureVertices[3] = 0.0f;

            TextureVertices[4] = 0.0f;
            TextureVertices[5] = 0.875f;

            TextureVertices[6] = 1.0f;
            TextureVertices[7] = 0.875f;
            RecordManager.RECORD_HEIGHT = RecordManager.RECORD_WIDTH = Math.min(size.getHeight(), size.getWidth());
        } else {
            TextureVertices[0] = 0.0f;
            TextureVertices[1] = 0.0f;

            TextureVertices[2] = 1.0f;
            TextureVertices[3] = 0.0f;

            TextureVertices[4] = 0.0f;
            TextureVertices[5] = 1.0f;

            TextureVertices[6] = 1.0f;
            TextureVertices[7] = 1.0f;

            RecordManager.RECORD_HEIGHT = Math.max(size.getHeight(), size.getWidth());
            RecordManager.RECORD_WIDTH = Math.min(size.getHeight(), size.getWidth());
        }
    }

    public static final float TextureVertices[] = {
            0.0f, 0.0f,     // 0 bottom left
            1.0f, 0.0f,     // 1 bottom right
            0.0f, 0.875f,     // 2 top left
            1.0f, 0.875f      // 3 top right
    };

    public static final float TextureVertices_90[] = {
            1.0f, 0.0f,
            1.0f, 1.0f,
            0.0f, 0.0f,
            0.0f, 1.0f,
    };

    public static final float TextureVertices_180[] = {
            1.0f, 1.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
    };

    public static final float TextureVertices_270[] = {
            0.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 1.0f,
            0.0f, 0.0f,
    };

    public static float[] getTextureVertices() {
        float[] result;
        switch (CameraUtils.getPreviewOrientation()) {
            case 0:
                result = TextureVertices_90;
                break;

            case 90:
                result = TextureVertices;
                break;

            case 180:
                result = TextureVertices_270;
                break;

            case 270:
                result = TextureVertices_180;
                break;

            default:
                result = TextureVertices;
        }
        return result;
    }

    public static FloatBuffer getTextureBuffer() {
        FloatBuffer result;
        switch (CameraUtils.getPreviewOrientation()) {
            case 0:
                if (CameraUtils.getCameraID() == Camera.CameraInfo.CAMERA_FACING_BACK
                        && mBackReverse) {
                    result = GlUtil.createFloatBuffer(TextureRotationUtils.TextureVertices_270);
                } else {
                    result = GlUtil.createFloatBuffer(TextureRotationUtils.TextureVertices_90);
                }
                break;

            case 90:
                if (CameraUtils.getCameraID() == Camera.CameraInfo.CAMERA_FACING_BACK
                        && mBackReverse) {
                    result = GlUtil.createFloatBuffer(TextureRotationUtils.TextureVertices_180);
                } else {
                    result = GlUtil.createFloatBuffer(TextureRotationUtils.TextureVertices);
                }
                break;

            case 180:
                if (CameraUtils.getCameraID() == Camera.CameraInfo.CAMERA_FACING_BACK
                        && mBackReverse) {
                    result = GlUtil.createFloatBuffer(TextureRotationUtils.TextureVertices_90);
                } else {
                    result = GlUtil.createFloatBuffer(TextureRotationUtils.TextureVertices_270);
                }
                break;

            case 270:
                if (CameraUtils.getCameraID() == Camera.CameraInfo.CAMERA_FACING_BACK
                        && mBackReverse) {
                    result = GlUtil.createFloatBuffer(TextureRotationUtils.TextureVertices);
                } else {
                    result = GlUtil.createFloatBuffer(TextureRotationUtils.TextureVertices_180);
                }
                break;

            default:
                if (CameraUtils.getCameraID() == Camera.CameraInfo.CAMERA_FACING_BACK
                        && mBackReverse) {
                    result = GlUtil.createFloatBuffer(TextureRotationUtils.TextureVertices_180);
                } else {
                    result = GlUtil.createFloatBuffer(TextureRotationUtils.TextureVertices);
                }

        }
        return result;
    }

    public static void setBackReverse(boolean reverse) {
        mBackReverse = reverse;
    }
}
