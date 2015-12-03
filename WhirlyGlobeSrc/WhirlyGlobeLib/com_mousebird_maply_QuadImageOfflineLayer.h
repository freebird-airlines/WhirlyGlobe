/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_mousebird_maply_QuadImageOfflineLayer */

#ifndef _Included_com_mousebird_maply_QuadImageOfflineLayer
#define _Included_com_mousebird_maply_QuadImageOfflineLayer
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_mousebird_maply_QuadImageOfflineLayer
 * Method:    setImageDepth
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_com_mousebird_maply_QuadImageOfflineLayer_setImageDepth
  (JNIEnv *, jobject, jint);

/*
 * Class:     com_mousebird_maply_QuadImageOfflineLayer
 * Method:    getImageDepth
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_mousebird_maply_QuadImageOfflineLayer_getImageDepth
  (JNIEnv *, jobject);

/*
 * Class:     com_mousebird_maply_QuadImageOfflineLayer
 * Method:    setAllowFrameLoading
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL Java_com_mousebird_maply_QuadImageOfflineLayer_setAllowFrameLoading
  (JNIEnv *, jobject, jboolean);

/*
 * Class:     com_mousebird_maply_QuadImageOfflineLayer
 * Method:    getFrameStatusNative
 * Signature: ([Z[I)I
 */
JNIEXPORT jint JNICALL Java_com_mousebird_maply_QuadImageOfflineLayer_getFrameStatusNative
  (JNIEnv *, jobject, jbooleanArray, jintArray);

/*
 * Class:     com_mousebird_maply_QuadImageOfflineLayer
 * Method:    setFrameLoadingPriority
 * Signature: ([ILcom/mousebird/maply/ChangeSet;)V
 */
JNIEXPORT void JNICALL Java_com_mousebird_maply_QuadImageOfflineLayer_setFrameLoadingPriority
  (JNIEnv *, jobject, jintArray, jobject);

/*
 * Class:     com_mousebird_maply_QuadImageOfflineLayer
 * Method:    getLoadedFrames
 * Signature: (I[Z[Z[I)V
 */
JNIEXPORT void JNICALL Java_com_mousebird_maply_QuadImageOfflineLayer_getLoadedFrames
  (JNIEnv *, jobject, jint, jbooleanArray, jbooleanArray, jintArray);

/*
 * Class:     com_mousebird_maply_QuadImageOfflineLayer
 * Method:    setMaxTiles
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_com_mousebird_maply_QuadImageOfflineLayer_setMaxTiles
  (JNIEnv *, jobject, jint);

/*
 * Class:     com_mousebird_maply_QuadImageOfflineLayer
 * Method:    setImportanceScale
 * Signature: (F)V
 */
JNIEXPORT void JNICALL Java_com_mousebird_maply_QuadImageOfflineLayer_setImportanceScale
  (JNIEnv *, jobject, jfloat);

/*
 * Class:     com_mousebird_maply_QuadImageOfflineLayer
 * Method:    setMultiLevelLoads
 * Signature: ([I)V
 */
JNIEXPORT void JNICALL Java_com_mousebird_maply_QuadImageOfflineLayer_setMultiLevelLoads
  (JNIEnv *, jobject, jintArray);

/*
 * Class:     com_mousebird_maply_QuadImageOfflineLayer
 * Method:    getTargetZoomLevel
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_mousebird_maply_QuadImageOfflineLayer_getTargetZoomLevel
  (JNIEnv *, jobject);

/*
 * Class:     com_mousebird_maply_QuadImageOfflineLayer
 * Method:    reload
 * Signature: (Lcom/mousebird/maply/ChangeSet;)V
 */
JNIEXPORT void JNICALL Java_com_mousebird_maply_QuadImageOfflineLayer_reload
  (JNIEnv *, jobject, jobject);

/*
 * Class:     com_mousebird_maply_QuadImageOfflineLayer
 * Method:    setSimultaneousFetches
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_com_mousebird_maply_QuadImageOfflineLayer_setSimultaneousFetches
  (JNIEnv *, jobject, jint);

/*
 * Class:     com_mousebird_maply_QuadImageOfflineLayer
 * Method:    setUseTargetZoomLevel
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL Java_com_mousebird_maply_QuadImageOfflineLayer_setUseTargetZoomLevel
  (JNIEnv *, jobject, jboolean);

/*
 * Class:     com_mousebird_maply_QuadImageOfflineLayer
 * Method:    setSingleLevelLoading
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL Java_com_mousebird_maply_QuadImageOfflineLayer_setSingleLevelLoading
  (JNIEnv *, jobject, jboolean);

/*
 * Class:     com_mousebird_maply_QuadImageOfflineLayer
 * Method:    nativeShutdown
 * Signature: (Lcom/mousebird/maply/ChangeSet;)V
 */
JNIEXPORT void JNICALL Java_com_mousebird_maply_QuadImageOfflineLayer_nativeShutdown
  (JNIEnv *, jobject, jobject);

/*
 * Class:     com_mousebird_maply_QuadImageOfflineLayer
 * Method:    nativeInit
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_mousebird_maply_QuadImageOfflineLayer_nativeInit
  (JNIEnv *, jclass);

/*
 * Class:     com_mousebird_maply_QuadImageOfflineLayer
 * Method:    initialise
 * Signature: (Lcom/mousebird/maply/CoordSystem;Lcom/mousebird/maply/ChangeSet;)V
 */
JNIEXPORT void JNICALL Java_com_mousebird_maply_QuadImageOfflineLayer_initialise
  (JNIEnv *, jobject, jobject, jobject);

/*
 * Class:     com_mousebird_maply_QuadImageOfflineLayer
 * Method:    dispose
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_mousebird_maply_QuadImageOfflineLayer_dispose
  (JNIEnv *, jobject);

/*
 * Class:     com_mousebird_maply_QuadImageOfflineLayer
 * Method:    nativeStartLayer
 * Signature: (Lcom/mousebird/maply/Scene;Lcom/mousebird/maply/MaplyRenderer;Lcom/mousebird/maply/Point2d;Lcom/mousebird/maply/Point2d;III)V
 */
JNIEXPORT void JNICALL Java_com_mousebird_maply_QuadImageOfflineLayer_nativeStartLayer
  (JNIEnv *, jobject, jobject, jobject, jobject, jobject, jint, jint, jint);

/*
 * Class:     com_mousebird_maply_QuadImageOfflineLayer
 * Method:    nativeViewUpdate
 * Signature: (Lcom/mousebird/maply/ViewState;)V
 */
JNIEXPORT void JNICALL Java_com_mousebird_maply_QuadImageOfflineLayer_nativeViewUpdate
  (JNIEnv *, jobject, jobject);

/*
 * Class:     com_mousebird_maply_QuadImageOfflineLayer
 * Method:    nativeEvalStep
 * Signature: (Lcom/mousebird/maply/ChangeSet;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_mousebird_maply_QuadImageOfflineLayer_nativeEvalStep
  (JNIEnv *, jobject, jobject);

/*
 * Class:     com_mousebird_maply_QuadImageOfflineLayer
 * Method:    nativeRefresh
 * Signature: (Lcom/mousebird/maply/ChangeSet;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_mousebird_maply_QuadImageOfflineLayer_nativeRefresh
  (JNIEnv *, jobject, jobject);

/*
 * Class:     com_mousebird_maply_QuadImageOfflineLayer
 * Method:    nativeTileDidLoad
 * Signature: (IIIILandroid/graphics/Bitmap;Lcom/mousebird/maply/ChangeSet;)V
 */
JNIEXPORT void JNICALL Java_com_mousebird_maply_QuadImageOfflineLayer_nativeTileDidLoad
  (JNIEnv *, jobject, jint, jint, jint, jint, jobject, jobject);

/*
 * Class:     com_mousebird_maply_QuadImageOfflineLayer
 * Method:    nativeTileDidNotLoad
 * Signature: (IIIILcom/mousebird/maply/ChangeSet;)V
 */
JNIEXPORT void JNICALL Java_com_mousebird_maply_QuadImageOfflineLayer_nativeTileDidNotLoad
  (JNIEnv *, jobject, jint, jint, jint, jint, jobject);

#ifdef __cplusplus
}
#endif
#endif