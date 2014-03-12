/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class za_co_monadic_scopus_Opus__ */

#ifndef _Included_za_co_monadic_scopus_Opus__
#define _Included_za_co_monadic_scopus_Opus__
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     za_co_monadic_scopus_Opus__
 * Method:    decoder_create
 * Signature: (II[I)J
 */
JNIEXPORT jlong JNICALL Java_za_co_monadic_scopus_Opus_00024_decoder_1create
  (JNIEnv *, jobject, jint, jint, jintArray);

/*
 * Class:     za_co_monadic_scopus_Opus__
 * Method:    decode
 * Signature: (J[BI[SII)I
 */
JNIEXPORT jint JNICALL Java_za_co_monadic_scopus_Opus_00024_decode__J_3BI_3SII
  (JNIEnv *, jobject, jlong, jbyteArray, jint, jshortArray, jint, jint);

/*
 * Class:     za_co_monadic_scopus_Opus__
 * Method:    decode
 * Signature: (J[BI[FII)I
 */
JNIEXPORT jint JNICALL Java_za_co_monadic_scopus_Opus_00024_decode__J_3BI_3FII
  (JNIEnv *, jobject, jlong, jbyteArray, jint, jfloatArray, jint, jint);

/*
 * Class:     za_co_monadic_scopus_Opus__
 * Method:    decoder_destroy
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_za_co_monadic_scopus_Opus_00024_decoder_1destroy
  (JNIEnv *, jobject, jlong);

/*
 * Class:     za_co_monadic_scopus_Opus__
 * Method:    decoder_get_ctl
 * Signature: (J[I)I
 */
JNIEXPORT jint JNICALL Java_za_co_monadic_scopus_Opus_00024_decoder_1get_1ctl
  (JNIEnv *, jobject, jlong, jintArray);

/*
 * Class:     za_co_monadic_scopus_Opus__
 * Method:    decoder_set_ctl
 * Signature: (JI)V
 */
JNIEXPORT void JNICALL Java_za_co_monadic_scopus_Opus_00024_decoder_1set_1ctl
  (JNIEnv *, jobject, jlong, jint);

/*
 * Class:     za_co_monadic_scopus_Opus__
 * Method:    encoder_create
 * Signature: (II[I)J
 */
JNIEXPORT jlong JNICALL Java_za_co_monadic_scopus_Opus_00024_encoder_1create
  (JNIEnv *, jobject, jint, jint, jintArray);

/*
 * Class:     za_co_monadic_scopus_Opus__
 * Method:    encode
 * Signature: (J[SI[BI)I
 */
JNIEXPORT jint JNICALL Java_za_co_monadic_scopus_Opus_00024_encode__J_3SI_3BI
  (JNIEnv *, jobject, jlong, jshortArray, jint, jbyteArray, jint);

/*
 * Class:     za_co_monadic_scopus_Opus__
 * Method:    encode
 * Signature: (J[FI[BI)I
 */
JNIEXPORT jint JNICALL Java_za_co_monadic_scopus_Opus_00024_encode__J_3FI_3BI
  (JNIEnv *, jobject, jlong, jfloatArray, jint, jbyteArray, jint);

/*
 * Class:     za_co_monadic_scopus_Opus__
 * Method:    encoder_destroy
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_za_co_monadic_scopus_Opus_00024_encoder_1destroy
  (JNIEnv *, jobject, jlong);

/*
 * Class:     za_co_monadic_scopus_Opus__
 * Method:    encoder_get_ctl
 * Signature: (J[I)I
 */
JNIEXPORT jint JNICALL Java_za_co_monadic_scopus_Opus_00024_encoder_1get_1ctl
  (JNIEnv *, jobject, jlong, jintArray);

/*
 * Class:     za_co_monadic_scopus_Opus__
 * Method:    encoder_set_ctl
 * Signature: (JI)V
 */
JNIEXPORT void JNICALL Java_za_co_monadic_scopus_Opus_00024_encoder_1set_1ctl
  (JNIEnv *, jobject, jlong, jint);

/*
 * Class:     za_co_monadic_scopus_Opus__
 * Method:    error_string
 * Signature: (I)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_za_co_monadic_scopus_Opus_00024_error_1string
  (JNIEnv *, jobject, jint);

#ifdef __cplusplus
}
#endif
#endif
