//
// Created by Tristan on 2024-11-04.
//
#include <camera/NdkCameraManager.h>
#include <camera/NdkCameraError.h>
#include <camera/NdkCameraMetadataTags.h>
#include <camera/NdkCameraMetadata.h>

#include <GLES2/gl2.h>
#include <GLES2/gl2ext.h>
#include <android/log.h>
#include <jni.h>
#include <math.h>
#include <stdio.h>
#include <stdlib.h>




void swap(){
    // this is a pointer
    ACameraManager *cameraManager = ACameraManager_create();

    ACameraIdList *cameraIds = nullptr;
    ACameraManager_getCameraIdList(cameraManager, &cameraIds);



    for (int i = 0; i < cameraIds->numCameras; ++i)
    {
        const char* id = cameraIds->cameraIds[i];

        ACameraMetadata* metadataObj;
        ACameraManager_getCameraCharacteristics(cameraManager, id, &metadataObj);

        int32_t count = 0;
        const uint32_t* tags = nullptr;
        ACameraMetadata_getAllTags(metadataObj, &count, &tags);

        for (int tagIdx = 0; tagIdx < count; ++tagIdx)
        {
            // We are interested in entry that describes the facing of camera
            if (ACAMERA_LENS_FACING == tags[tagIdx]) {
                ACameraMetadata_const_entry lensInfo = { 0 };
                ACameraMetadata_getConstEntry(metadataObj, tags[tagIdx], &lensInfo);

                auto facing = static_cast<acamera_metadata_enum_android_lens_facing_t>(
                        lensInfo.data.u8[0]);

                // Found a back-facing camera
                if (facing == ACAMERA_LENS_FACING_BACK)
                //...

                break;
            }
         //   ...
        }

        ACameraMetadata_free(metadataObj);
    }
    ACameraManager_deleteCameraIdList(cameraIds);
    ACameraManager_delete(cameraManager);

}