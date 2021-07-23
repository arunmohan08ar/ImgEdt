
#include <jni.h>
#include <android/bitmap.h>
#include "image_operators/async/Async.cpp"
#include "Bitmap_Utils.cpp"

/*#include <android/log.h>

#define  LOG_TAG    "cpp_test"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
*/


extern "C"
JNIEXPORT void JNICALL
Java_com_tsehsrah_imageops_imageOperations_utilities_CPPfuns_eXP2ProcJNI(JNIEnv *env, jobject thiz,
                                                        jobject bmp,
                                                 jobject ref,
                                                 jobject expsd,
                                                const jint frm,
                                                const jint to,
                                                const jint rX,
                                                const jint rY,
                                                const jint sX,
                                                const jint sY,
                                                const jint intensity,
                                                const jint NOC
                                                ) {

    AndroidBitmapInfo info_bmp,info_expsd,info_ref;
    AndroidBitmap_getInfo(env, bmp, &info_bmp);
    AndroidBitmap_getInfo(env, expsd, &info_expsd);
    AndroidBitmap_getInfo(env, ref, &info_ref);

    void* pixels_bmp;
    void* pixels_expsd;
    void* pixels_ref;
    AndroidBitmap_lockPixels(env, bmp, &pixels_bmp);
    AndroidBitmap_lockPixels(env, expsd, &pixels_expsd);
    AndroidBitmap_lockPixels(env, ref, &pixels_ref);

    auto*  line_bmp = (uint32_t*)pixels_bmp;
    auto*  line_exp = (uint32_t*)pixels_expsd;
    auto*  line_ref = (uint32_t*)pixels_ref;
  //  LOGI("starting exp");
    startAsync(Operators::EXPOSE,
               line_bmp,
               line_ref,
               line_exp,
               info_bmp,
               info_ref,
               info_expsd,
               frm,
               to,
               intensity,
               rX,
               rY,
               sX,
               sY,
               NOC
               );

    AndroidBitmap_unlockPixels(env, bmp);
    AndroidBitmap_unlockPixels(env, expsd);
    AndroidBitmap_unlockPixels(env, ref);

}


extern "C"
JNIEXPORT void JNICALL
Java_com_tsehsrah_imageops_imageOperations_utilities_CPPfuns_iNVProcJNI(JNIEnv *env, jobject thiz,
                                                     jobject bmp,
                                                     jobject ref,
                                                     const jint frm,
                                                     const jint to,
                                                     const jint x,
                                                     const jint y,
                                                     const jint NOC

) {
    AndroidBitmapInfo info,rInfo;
    AndroidBitmap_getInfo(env, bmp, &info);
    AndroidBitmap_getInfo(env, ref, &rInfo);

    void* pixels;
    void* rPixels;

    AndroidBitmap_lockPixels(env, bmp, &pixels);
    AndroidBitmap_lockPixels(env, ref, &rPixels);

    auto*  line = (uint32_t*)pixels;
    auto*  rLine = (uint32_t*)rPixels;
    startAsync(Operators::INVERSE,
               line,
               rLine,
               nullptr,
               info,
               rInfo,
               info,
               frm,
               to,
               0,
               x,
               y,
               NULL,
               NULL,
               NOC
    );

    AndroidBitmap_unlockPixels(env, ref);
    AndroidBitmap_unlockPixels(env, bmp);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_tsehsrah_imageops_imageOperations_utilities_CPPfuns_bINProcJNI(JNIEnv *env, jobject thiz,jobject bmp,
                                                     jobject ref,
                                                     const jint frm,
                                                     const jint to,
                                                     const jint x,
                                                     const jint y,
                                                     const jint NOC
                                                     ) {
   // LOGI("Received to cpp DR  %i      %i",frm,to);
    AndroidBitmapInfo info,rInfo;
    AndroidBitmap_getInfo(env, bmp, &info);
    AndroidBitmap_getInfo(env, ref, &rInfo);

    void* pixels;
    void* rPixels;

    AndroidBitmap_lockPixels(env, bmp, &pixels);
    AndroidBitmap_lockPixels(env, ref, &rPixels);

    auto*  line = (uint32_t*)pixels;
    auto*  rLine = (uint32_t*)rPixels;
    startAsync(Operators::BINARY,
               line,
               rLine,
               nullptr,
               info,
               rInfo,
               info,
               frm,
               to,
               0,
               x,
               y,
               NULL,
               NULL,
               NOC
               );

    AndroidBitmap_unlockPixels(env, bmp);
    AndroidBitmap_unlockPixels(env, ref);

}

extern "C"
JNIEXPORT void JNICALL
Java_com_tsehsrah_imageops_imageOperations_utilities_CPPfuns_dRProcJNI(JNIEnv *env, jobject thiz,
                                                    jobject bmp,
                                                    const jint frm,
                                                    const jint to,
                                                    const jint NOC
                                                    ) {
    AndroidBitmapInfo info;
    AndroidBitmap_getInfo(env, bmp, &info);

    void* pixels;
    AndroidBitmap_lockPixels(env, bmp, &pixels);

    auto*  line = (uint32_t*)pixels;
    auto*  line_ref = (uint32_t*)pixels;

    startAsync(Operators::DYNAMIC_RANGE,
               line,
               line_ref,
               nullptr,
               info,
               info,
               info,
               frm,
               to,
               NULL,
               NULL,
               NULL,
               NULL,
               NULL,
               NOC
               );


/*
    int from=maxfn(0,frm);
    int upto=minfn(255,to);
    const float diff=(upto-from)<1?1:(upto-from);
    const float rto=(float)255/diff;
    long px,pos,nlne;
    int r,g,b,a;
    for(u_int32_t  i=0;i<info.width;i++){
        pos=i*info.height;
        nlne=pos+info.height;
        while(pos<nlne){
            px=line[pos];
            gt_RGBA(px,r,g,b,a);
            r-=from;
            g-=from;
            b-=from;
            r*=rto;
            g*=rto;
            b*=rto;

            if(r<0)r=0;
            else if(r>255) r=255;
            if(g<0)g=0;
            else if(g>255) g=255;
            if(b<0)b=0;
            else if(b>255) b=255;

            line[pos]=gt_Pxl(r,g,b,a);
            pos++;
        }
    }*/

    AndroidBitmap_unlockPixels(env, bmp);
}
